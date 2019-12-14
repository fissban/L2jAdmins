package l2j.gameserver.data.engines;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.conditions.Condition;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionElementSeed;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionGameChance;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionGameTime;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionLogicAnd;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionLogicNot;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionLogicOr;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerHp;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerLevel;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerRace;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerState;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionSlotItemId;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionTargetLevel;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionTargetUsesWeaponKind;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionUsingItemType;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionUsingSkill;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionWithSkill;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionGameTime.CheckGameTime;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerState.CheckPlayerState;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.AbnormalEffectType;
import l2j.gameserver.model.actor.manager.character.skills.funcs.FuncTemplate;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Lambda;
import l2j.gameserver.model.actor.manager.character.skills.funcs.LambdaCalc;
import l2j.gameserver.model.actor.manager.character.skills.funcs.LambdaConst;
import l2j.gameserver.model.actor.manager.character.skills.funcs.LambdaStats;
import l2j.gameserver.model.actor.manager.character.skills.funcs.LambdaStats.LambdaStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.WeaponType;

/**
 * @author mkizub
 */
public abstract class AbstractDocumentBase
{
	public static final Logger LOG = Logger.getLogger(AbstractDocumentBase.class.getName());
	
	private final File file;
	protected Map<String, String[]> tables;
	
	protected AbstractDocumentBase(File pFile)
	{
		file = pFile;
		tables = new HashMap<>();
	}
	
	public Document parse()
	{
		Document doc;
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(file);
		}
		catch (final Exception e)
		{
			LOG.log(Level.SEVERE, "Error loading file " + file, e);
			return null;
		}
		try
		{
			parseDocument(doc);
		}
		catch (final Exception e)
		{
			LOG.log(Level.SEVERE, "Error in file " + file, e);
			return null;
		}
		return doc;
	}
	
	protected abstract void parseDocument(Document doc);
	
	protected abstract StatsSet getStatsSet();
	
	protected abstract String getTableValue(String name);
	
	protected abstract String getTableValue(String name, int idx);
	
	protected void resetTable()
	{
		tables = new HashMap<>();
	}
	
	protected void setTable(String name, String[] table)
	{
		tables.put(name, table);
	}
	
	protected void parseTemplate(Node n, Object template)
	{
		Condition condition = null;
		n = n.getFirstChild();
		if (n == null)
		{
			return;
		}
		if ("cond".equalsIgnoreCase(n.getNodeName()))
		{
			condition = parseCondition(n.getFirstChild(), template);
			final Node msg = n.getAttributes().getNamedItem("msg");
			
			if ((condition != null) && (msg != null))
			{
				condition.setMessage(msg.getNodeValue());
			}
			
			n = n.getNextSibling();
		}
		for (; n != null; n = n.getNextSibling())
		{
			if ("add".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Add", condition);
			}
			else if ("sub".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Sub", condition);
			}
			else if ("mul".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Mul", condition);
			}
			else if ("div".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Div", condition);
			}
			else if ("set".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Set", condition);
			}
			else if ("enchant".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "Enchant", condition);
			}
			else if ("skill".equalsIgnoreCase(n.getNodeName()))
			{
				attachSkill(n, template, condition);
			}
			else if ("effect".equalsIgnoreCase(n.getNodeName()))
			{
				if (template instanceof EffectTemplate)
				{
					throw new RuntimeException("Nested effects");
				}
				attachEffect(n, template, condition);
			}
			else if ("basemul".equalsIgnoreCase(n.getNodeName()))
			{
				attachFunc(n, template, "BaseMul", condition);
			}
		}
	}
	
	protected void attachFunc(Node n, Object template, String name, Condition attachCond)
	{
		final StatsType stat = Enum.valueOf(StatsType.class, n.getAttributes().getNamedItem("stat").getNodeValue());
		final String order = n.getAttributes().getNamedItem("order").getNodeValue();
		final Lambda lambda = getLambda(n, template);
		final int ord = Integer.decode(getValue(order, template));
		
		final Condition applyCond = parseCondition(n.getFirstChild(), template);
		final FuncTemplate ft = new FuncTemplate(attachCond, applyCond, name, stat, ord, lambda);
		if (template instanceof Item)
		{
			((Item) template).attach(ft);
		}
		else if (template instanceof Skill)
		{
			((Skill) template).attach(ft);
		}
		else if (template instanceof EffectTemplate)
		{
			((EffectTemplate) template).attach(ft);
		}
	}
	
	protected void attachLambdaFunc(Node n, Object template, LambdaCalc calc)
	{
		String name = n.getNodeName();
		final StringBuilder sb = new StringBuilder(name);
		sb.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		name = sb.toString();
		final Lambda lambda = getLambda(n, template);
		final FuncTemplate ft = new FuncTemplate(null, null, name, null, calc.getFuncs().size(), lambda);
		calc.addFunc(ft.getFunc(new Env(), calc));
	}
	
	protected void attachEffect(Node n, Object template, Condition attachCond)
	{
		final NamedNodeMap attrs = n.getAttributes();
		
		final String name = attrs.getNamedItem("name").getNodeValue();
		
		double power = 0.0;
		if (attrs.getNamedItem("power") != null)
		{
			power = Double.parseDouble(getValue(attrs.getNamedItem("power").getNodeValue(), template));
		}
		double rate = -1.0;
		if (attrs.getNamedItem("rate") != null)
		{
			rate = Double.parseDouble(getValue(attrs.getNamedItem("rate").getNodeValue(), template));
		}
		
		int count = 1;
		if (attrs.getNamedItem("count") != null)
		{
			count = Integer.decode(getValue(attrs.getNamedItem("count").getNodeValue(), template));
		}
		
		int time = 0;
		if (attrs.getNamedItem("time") != null)
		{
			time = Integer.decode(getValue(attrs.getNamedItem("time").getNodeValue(), template));
			if (Config.ENABLE_MODIFY_SKILL_DURATION)
			{
				Skill sk = (Skill) template;
				
				if (Config.SKILL_DURATION_LIST.containsKey(sk.getId()))
				{
					if ((sk.getLevel() >= 100) && (sk.getLevel() < 140))
					{
						time += Config.SKILL_DURATION_LIST.get(sk.getId());
					}
					else
					{
						time = Config.SKILL_DURATION_LIST.get(sk.getId());
					}
				}
			}
		}
		
		boolean self = attrs.getNamedItem("self") != null ? Boolean.parseBoolean(getValue(attrs.getNamedItem("self").getNodeValue(), template)) : false;
		boolean icon = attrs.getNamedItem("noicon") != null ? !Boolean.parseBoolean(getValue(attrs.getNamedItem("noicon").getNodeValue(), template)) : true;
		
		AbnormalEffectType abnormal = AbnormalEffectType.NULL;
		if (attrs.getNamedItem("abnormal") != null)
		{
			final String abn = attrs.getNamedItem("abnormal").getNodeValue();
			abnormal = AbnormalEffectType.valueOf(abn);
		}
		
		String stackType = "none";
		if (attrs.getNamedItem("stackType") != null)
		{
			stackType = attrs.getNamedItem("stackType").getNodeValue();
		}
		
		float stackOrder = 0;
		if (attrs.getNamedItem("stackOrder") != null)
		{
			stackOrder = Float.parseFloat(getValue(attrs.getNamedItem("stackOrder").getNodeValue(), template));
		}
		
		final Lambda lambda = getLambda(n, template);
		final Condition applayCond = parseCondition(n.getFirstChild(), template);
		
		final EffectTemplate et = new EffectTemplate(attachCond, applayCond, name, lambda, count, time, abnormal, stackType, stackOrder, icon, power, rate);
		
		parseTemplate(n, et);
		if (template instanceof Item)
		{
			((Item) template).attach(et);
		}
		else if ((template instanceof Skill) && !self)
		{
			((Skill) template).attach(et);
		}
		else if ((template instanceof Skill) && self)
		{
			((Skill) template).attachSelf(et);
		}
	}
	
	protected void attachSkill(Node n, Object template, Condition attachCond)
	{
		final NamedNodeMap attrs = n.getAttributes();
		int id = 0, lvl = 1;
		if (attrs.getNamedItem("id") != null)
		{
			id = Integer.decode(getValue(attrs.getNamedItem("id").getNodeValue(), template));
		}
		if (attrs.getNamedItem("lvl") != null)
		{
			lvl = Integer.decode(getValue(attrs.getNamedItem("lvl").getNodeValue(), template));
		}
		
		final Skill skill = SkillData.getInstance().getSkill(id, lvl);
		if (attrs.getNamedItem("chance") != null)
		{
			if ((template instanceof ItemWeapon) || (template instanceof Item))
			{
				skill.attach(new ConditionGameChance(Integer.decode(getValue(attrs.getNamedItem("chance").getNodeValue(), template))), true);
			}
			else
			{
				skill.attach(new ConditionGameChance(Integer.decode(getValue(attrs.getNamedItem("chance").getNodeValue(), template))), false);
			}
		}
		if (template instanceof ItemWeapon)
		{
			if ((attrs.getNamedItem("onUse") != null) || ((attrs.getNamedItem("onCrit") == null) && (attrs.getNamedItem("onCast") == null)))
			{
				((ItemWeapon) template).attach(skill); // Attach as skill triggered on use
			}
			if (attrs.getNamedItem("onCrit") != null)
			{
				((ItemWeapon) template).attachOnCrit(skill); // Attach as skill triggered on critical hit
			}
			if (attrs.getNamedItem("onCast") != null)
			{
				((ItemWeapon) template).attachOnCast(skill); // Attach as skill triggered on cast
			}
		}
		else if (template instanceof Item)
		{
			((Item) template).attach(skill); // Attach as skill triggered on use
		}
	}
	
	protected Condition parseCondition(Node n, Object template)
	{
		while ((n != null) && (n.getNodeType() != Node.ELEMENT_NODE))
		{
			n = n.getNextSibling();
		}
		if (n == null)
		{
			return null;
		}
		if ("and".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicAnd(n, template);
		}
		if ("or".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicOr(n, template);
		}
		if ("not".equalsIgnoreCase(n.getNodeName()))
		{
			return parseLogicNot(n, template);
		}
		if ("player".equalsIgnoreCase(n.getNodeName()))
		{
			return parsePlayerCondition(n);
		}
		if ("target".equalsIgnoreCase(n.getNodeName()))
		{
			return parseTargetCondition(n, template);
		}
		if ("using".equalsIgnoreCase(n.getNodeName()))
		{
			return parseUsingCondition(n);
		}
		if ("game".equalsIgnoreCase(n.getNodeName()))
		{
			return parseGameCondition(n);
		}
		return null;
	}
	
	protected Condition parseLogicAnd(Node n, Object template)
	{
		final ConditionLogicAnd cond = new ConditionLogicAnd();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				cond.add(parseCondition(n, template));
			}
		}
		if ((cond.conditions == null) || (cond.conditions.isEmpty()))
		{
			LOG.severe("Empty <and> condition in " + file);
		}
		return cond;
	}
	
	protected Condition parseLogicOr(Node n, Object template)
	{
		final ConditionLogicOr cond = new ConditionLogicOr();
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				cond.add(parseCondition(n, template));
			}
		}
		if ((cond.conditions == null) || (cond.conditions.isEmpty()))
		{
			LOG.severe("Empty <or> condition in " + file);
		}
		return cond;
	}
	
	protected Condition parseLogicNot(Node n, Object template)
	{
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				return new ConditionLogicNot(parseCondition(n, template));
			}
		}
		LOG.severe("Empty <not> condition in " + file);
		return null;
	}
	
	protected Condition parsePlayerCondition(Node n)
	{
		Condition cond = null;
		final int[] elementSeeds = new int[5];
		final NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node a = attrs.item(i);
			if ("race".equalsIgnoreCase(a.getNodeName()))
			{
				final Race race = Race.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerRace(race));
			}
			else if ("level".equalsIgnoreCase(a.getNodeName()))
			{
				final int lvl = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerLevel(lvl));
			}
			else if ("resting".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
			}
			else if ("flying".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
			}
			else if ("moving".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
			}
			else if ("running".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
			}
			else if ("standing".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.parseBoolean(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
			}
			else if ("behind".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.BEHIND, val));
			}
			else if ("front".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FRONT, val));
			}
			else if ("hp".equalsIgnoreCase(a.getNodeName()))
			{
				final int hp = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionPlayerHp(hp));
			}
			else if ("seed_fire".equalsIgnoreCase(a.getNodeName()))
			{
				elementSeeds[0] = Integer.decode(getValue(a.getNodeValue(), null));
			}
			else if ("seed_water".equalsIgnoreCase(a.getNodeName()))
			{
				elementSeeds[1] = Integer.decode(getValue(a.getNodeValue(), null));
			}
			else if ("seed_wind".equalsIgnoreCase(a.getNodeName()))
			{
				elementSeeds[2] = Integer.decode(getValue(a.getNodeValue(), null));
			}
			else if ("seed_various".equalsIgnoreCase(a.getNodeName()))
			{
				elementSeeds[3] = Integer.decode(getValue(a.getNodeValue(), null));
			}
			else if ("seed_any".equalsIgnoreCase(a.getNodeName()))
			{
				elementSeeds[4] = Integer.decode(getValue(a.getNodeValue(), null));
			}
		}
		
		// Elemental seed condition processing
		for (final int elementSeed : elementSeeds)
		{
			if (elementSeed > 0)
			{
				cond = joinAnd(cond, new ConditionElementSeed(elementSeeds));
				break;
			}
		}
		
		if (cond == null)
		{
			LOG.severe("Unrecognized <player> condition in " + file);
		}
		return cond;
	}
	
	protected Condition parseTargetCondition(Node n, Object template)
	{
		Condition cond = null;
		final NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node a = attrs.item(i);
			if ("level".equalsIgnoreCase(a.getNodeName()))
			{
				final int lvl = Integer.decode(getValue(a.getNodeValue(), template));
				cond = joinAnd(cond, new ConditionTargetLevel(lvl));
			}
			else if ("using".equalsIgnoreCase(a.getNodeName()))
			{
				int mask = 0;
				final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					final String item = st.nextToken().trim();
					for (final WeaponType wt : WeaponType.values())
					{
						if (wt.toString().equals(item))
						{
							mask |= wt.mask();
							break;
						}
					}
					for (final ArmorType at : ArmorType.values())
					{
						if (at.toString().equals(item))
						{
							mask |= at.mask();
							break;
						}
					}
				}
				cond = joinAnd(cond, new ConditionTargetUsesWeaponKind(mask));
			}
		}
		if (cond == null)
		{
			LOG.severe("Unrecognized <target> condition in " + file);
		}
		return cond;
	}
	
	protected Condition parseUsingCondition(Node n)
	{
		Condition cond = null;
		final NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node a = attrs.item(i);
			if ("kind".equalsIgnoreCase(a.getNodeName()))
			{
				int mask = 0;
				final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
				while (st.hasMoreTokens())
				{
					final String item = st.nextToken().trim();
					for (final WeaponType wt : WeaponType.values())
					{
						if (wt.toString().equals(item))
						{
							mask |= wt.mask();
							break;
						}
					}
					for (final ArmorType at : ArmorType.values())
					{
						if (at.toString().equals(item))
						{
							mask |= at.mask();
							break;
						}
					}
				}
				cond = joinAnd(cond, new ConditionUsingItemType(mask));
			}
			else if ("skill".equalsIgnoreCase(a.getNodeName()))
			{
				final int id = Integer.parseInt(a.getNodeValue());
				cond = joinAnd(cond, new ConditionUsingSkill(id));
			}
			else if ("slotitem".equalsIgnoreCase(a.getNodeName()))
			{
				final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
				final int id = Integer.parseInt(st.nextToken().trim());
				final int slot = Integer.parseInt(st.nextToken().trim());
				int enchant = 0;
				if (st.hasMoreTokens())
				{
					enchant = Integer.parseInt(st.nextToken().trim());
				}
				cond = joinAnd(cond, new ConditionSlotItemId(ParpedollType.values()[slot], id, enchant));
			}
		}
		if (cond == null)
		{
			LOG.severe("Unrecognized <using> condition in " + file);
		}
		return cond;
	}
	
	protected Condition parseGameCondition(Node n)
	{
		Condition cond = null;
		final NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node a = attrs.item(i);
			if ("skill".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionWithSkill(val));
			}
			else if ("night".equalsIgnoreCase(a.getNodeName()))
			{
				final boolean val = Boolean.valueOf(a.getNodeValue());
				cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
			}
			else if ("chance".equalsIgnoreCase(a.getNodeName()))
			{
				final int val = Integer.decode(getValue(a.getNodeValue(), null));
				cond = joinAnd(cond, new ConditionGameChance(val));
			}
		}
		if (cond == null)
		{
			LOG.severe("Unrecognized <game> condition in " + file);
		}
		return cond;
	}
	
	protected void parseTable(Node n)
	{
		final NamedNodeMap attrs = n.getAttributes();
		final String name = attrs.getNamedItem("name").getNodeValue();
		if (name.charAt(0) != '#')
		{
			throw new IllegalArgumentException("Table name must start with #");
		}
		final StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
		final List<String> array = new ArrayList<>();
		while (data.hasMoreTokens())
		{
			array.add(data.nextToken());
		}
		final String[] res = new String[array.size()];
		for (int i = 0; i < array.size(); i++)
		{
			// res[i] = getNumber(array.get(i), null);
			res[i] = array.get(i);
		}
		
		setTable(name, res);
	}
	
	protected void parseBeanSet(Node n, StatsSet set, Integer level)
	{
		final String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
		final String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
		final char ch = value.length() == 0 ? ' ' : value.charAt(0);
		if ((ch == '#') || (ch == '-') || Character.isDigit(ch))
		{
			set.set(name, String.valueOf(getValue(value, level)));
		}
		else
		{
			set.set(name, value);
		}
	}
	
	protected Lambda getLambda(Node n, Object template)
	{
		final Node nval = n.getAttributes().getNamedItem("val");
		if (nval != null)
		{
			final String val = nval.getNodeValue();
			if (val.charAt(0) == '#')
			{ // table by level
				return new LambdaConst(Double.parseDouble(getTableValue(val)));
			}
			else if (val.charAt(0) == '$')
			{
				if (val.equalsIgnoreCase("$player_level"))
				{
					return new LambdaStats(LambdaStatsType.PLAYER_LEVEL);
				}
				if (val.equalsIgnoreCase("$target_level"))
				{
					return new LambdaStats(LambdaStatsType.TARGET_LEVEL);
				}
				if (val.equalsIgnoreCase("$player_max_hp"))
				{
					return new LambdaStats(LambdaStatsType.PLAYER_MAX_HP);
				}
				if (val.equalsIgnoreCase("$player_max_mp"))
				{
					return new LambdaStats(LambdaStatsType.PLAYER_MAX_MP);
				}
				// try to find value out of item fields
				final StatsSet set = getStatsSet();
				final String field = set.getString(val.substring(1));
				if (field != null)
				{
					return new LambdaConst(Double.parseDouble(getValue(field, template)));
				}
				// failed
				throw new IllegalArgumentException("Unknown value " + val);
			}
			else
			{
				return new LambdaConst(Double.parseDouble(val));
			}
		}
		final LambdaCalc calc = new LambdaCalc();
		n = n.getFirstChild();
		while ((n != null) && (n.getNodeType() != Node.ELEMENT_NODE))
		{
			n = n.getNextSibling();
		}
		if ((n == null) || !"val".equals(n.getNodeName()))
		{
			throw new IllegalArgumentException("Value not specified");
		}
		
		for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			attachLambdaFunc(n, template, calc);
		}
		return calc;
	}
	
	protected String getValue(String value, Object template)
	{
		if (value.charAt(0) == '#')
		{// table by level
			if (template instanceof Skill)
			{
				return getTableValue(value);
			}
			else if (template instanceof Integer)
			{
				return getTableValue(value, ((Integer) template).intValue());
			}
			else
			{
				throw new IllegalStateException();
			}
		}
		return value;
	}
	
	protected Condition joinAnd(Condition cond, Condition c)
	{
		if (cond == null)
		{
			return c;
		}
		if (cond instanceof ConditionLogicAnd)
		{
			((ConditionLogicAnd) cond).add(c);
			return cond;
		}
		final ConditionLogicAnd and = new ConditionLogicAnd();
		and.add(cond);
		and.add(c);
		return and;
	}
}
