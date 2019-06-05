package l2j.gameserver.model.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.TargetHandler;
import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.skills.conditions.Condition;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.enums.SkillConditionType;
import l2j.gameserver.model.skills.enums.SkillElementType;
import l2j.gameserver.model.skills.enums.SkillOpType;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.funcs.FuncTemplate;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * This class...
 * @version $Revision: 1.3.2.8.2.22 $ $Date: 2005/04/06 16:13:42 $
 */
public abstract class Skill
{
	protected static final Logger LOG = Logger.getLogger(Skill.class.getName());
	
	public static final int SKILL_CUBIC_MASTERY = 143;
	public static final int SKILL_LUCKY = 194;
	public static final int SKILL_CREATE_COMMON = 1320;
	public static final int SKILL_CREATE_DWARVEN = 172;
	public static final int SKILL_CRYSTALLIZE = 248;
	
	private L2Character affected;
	
	// these two build the primary key
	private final int id;
	private final int level;
	
	// not needed, just for easier debug
	private final String name;
	private final SkillOpType operateType;
	private final boolean magic;
	private final int mpConsume;
	private final int mpInitialConsume;
	private final int hpConsume;
	private final int itemConsumeCount;
	private final int itemConsumeId;
	// item consume count over time
	private final int itemConsumeOT;
	// item consume id over time
	private final int itemConsumeIdOT;
	// item consume time in milliseconds
	private final int itemConsumeTime;
	// how many times to consume an item
	private final int itemConsumeSteps;
	// for summon spells:
	// a) What is the total lifetime of summons (in millisecs)
	private final int summonTotalLifeTime;
	// b) how much lifetime is lost per second of idleness (non-fighting)
	private final int summonTimeLostIdle;
	// c) how much time is lost per second of activity (fighting)
	private final int summonTimeLostActive;
	
	private final int castRange;
	private final int effectRange;
	
	private final int maxCharges;
	private final int feed;
	
	private final Set<EffectType> negateStats = new HashSet<>();
	private final int maxNegatedEffects;
	
	// all times in milliseconds
	private boolean staticHitTime = false;
	private int hitTime;
	private final int coolTime;
	private final int reuseDelay;
	
	private final int reuseHashCode;
	
	/** Target type of the skill : SELF, PARTY, CLAN, PET... */
	private final SkillTargetType targetType;
	
	private final double power;
	private final int magicLevel;
	
	// Effecting area of the skill, in radius.
	// The radius center varies according to the targetType:
	// "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
	private final int skillRadius;
	
	private final SkillType skillType;
	private final EffectType effectType;
	private final int effectPower;
	
	// Cancel skills ids use in EffectNegateSpring
	private final List<Integer> negateIds = new ArrayList<>();
	
	private final boolean isPotion;
	private final SkillElementType element;
	
	private final BaseStatsType saveVs;
	
	private final SkillConditionType blowDamageCondition;
	private final boolean overhit;
	
	private final List<ClassId> canLearn; // which classes can learn
	private final List<Integer> teachers; // which NPC teaches
	private final int nextDanceCost;
	
	private final int baseCritRate;
	
	protected Condition preCondition;
	protected Condition itemPreCondition;
	protected List<FuncTemplate> funcTemplates;
	public List<EffectTemplate> effectTemplates;
	protected List<EffectTemplate> effectTemplatesSelf;
	
	// SkillDrain
	private final float absorbPart;
	private final int absorbAbs;
	// SkillSummon
	private final int npcId;
	private final float expPenalty;
	// SkillSummon
	private final CubicType cubicType;
	// SkillChargeDmg
	private final int numCharges;
	// SkillCreateItem
	private final int createItemId;
	private final int createItemCount;
	private final int randomCount;
	// SkillRecall
	private final LocationHolder loc;
	private final TeleportWhereType recallType;
	
	protected Skill(StatsSet set)
	{
		id = set.getInteger("skill_id");
		level = set.getInteger("level");
		
		reuseHashCode = SkillData.getSkillHashCode(id, level);
		
		name = set.getString("name");
		operateType = set.getEnum("operateType", SkillOpType.class);
		magic = set.getBool("isMagic", false);
		isPotion = set.getBool("isPotion", false);
		mpConsume = set.getInteger("mpConsume", 0);
		mpInitialConsume = set.getInteger("mpInitialConsume", 0);
		hpConsume = set.getInteger("hpConsume", 0);
		itemConsumeCount = set.getInteger("itemConsumeCount", 0);
		itemConsumeId = set.getInteger("itemConsumeId", 0);
		itemConsumeOT = set.getInteger("itemConsumeCountOT", 0);
		itemConsumeIdOT = set.getInteger("itemConsumeIdOT", 0);
		itemConsumeTime = set.getInteger("itemConsumeTime", 0);
		itemConsumeSteps = set.getInteger("itemConsumeSteps", 0);
		summonTotalLifeTime = set.getInteger("summonTotalLifeTime", 1200000); // 20 minutes default
		summonTimeLostIdle = set.getInteger("summonTimeLostIdle", 0);
		summonTimeLostActive = set.getInteger("summonTimeLostActive", 0);
		
		castRange = set.getInteger("castRange", -1);
		effectRange = set.getInteger("effectRange", -1);
		
		maxCharges = set.getInteger("maxCharges", 0);
		
		feed = set.getInteger("feed", 0);
		
		String str = set.getString("negateStats", "");
		
		if (!str.isEmpty())
		{
			for (String stat : str.split(" "))
			{
				EffectType type = null;
				try
				{
					type = Enum.valueOf(EffectType.class, stat);
				}
				catch (Exception e)
				{
					throw new IllegalArgumentException("SkillId: " + id + "Enum value of type " + EffectType.class.getName() + " required, but found: " + stat);
				}
				
				negateStats.add(type);
			}
		}
		
		maxNegatedEffects = set.getInteger("maxNegated", 0);
		
		String ids = set.getString("negateIds", "");
		
		if (!ids.isEmpty())
		{
			for (String id : ids.split(" "))
			{
				negateIds.add(Integer.parseInt(id));
			}
		}
		
		coolTime = set.getInteger("coolTime", 0);
		staticHitTime = set.getBool("staticHitTime", false);
		hitTime = set.getInteger("hitTime", 0);
		reuseDelay = set.getInteger("reuseDelay", 0);
		
		skillRadius = set.getInteger("skillRadius", 80);
		
		targetType = set.getEnum("target", SkillTargetType.class);
		power = set.getFloat("power", 0.f);
		magicLevel = set.getInteger("magicLvl", SkillTreeData.getInstance().getMinSkillLevel(id, level));
		
		skillType = set.getEnum("skillType", SkillType.class, SkillType.DUMMY);
		effectType = set.getEnum("effectType", EffectType.class, EffectType.BUFF);
		effectPower = set.getInteger("effectPower", 0);
		
		element = set.getEnum("element", SkillElementType.class, null);
		
		saveVs = set.getEnum("saveVs", BaseStatsType.class, null);
		
		blowDamageCondition = set.getEnum("blowDamageCondition", SkillConditionType.class, SkillConditionType.NONE);
		overhit = set.getBool("overHit", false);
		
		nextDanceCost = set.getInteger("nextDanceCost", 0);
		
		baseCritRate = set.getInteger("baseCritRate", ((skillType == SkillType.PDAM) || (skillType == SkillType.BLOW)) ? 0 : -1);
		
		String canLearn = set.getString("canLearn", null);
		if (canLearn == null)
		{
			this.canLearn = null;
		}
		else
		{
			this.canLearn = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(canLearn, " \r\n\t,;");
			while (st.hasMoreTokens())
			{
				String cls = st.nextToken();
				try
				{
					this.canLearn.add(ClassId.valueOf(cls));
				}
				catch (Throwable t)
				{
					LOG.log(Level.SEVERE, "Bad class " + cls + " to learn skill", t);
				}
			}
		}
		
		String teachers = set.getString("teachers", null);
		if (teachers == null)
		{
			this.teachers = null;
		}
		else
		{
			this.teachers = new ArrayList<>();
			StringTokenizer st = new StringTokenizer(teachers, " \r\n\t,;");
			while (st.hasMoreTokens())
			{
				String npcid = st.nextToken();
				try
				{
					this.teachers.add(Integer.parseInt(npcid));
				}
				catch (Throwable t)
				{
					LOG.log(Level.SEVERE, "Bad teacher id " + npcid + " to teach skill", t);
				}
			}
		}
		// SkillDrain
		absorbPart = set.getFloat("absorbPart", 0.f);
		absorbAbs = set.getInteger("absorbAbs", 0);
		// SkillCubic
		cubicType = set.getEnum("cubic", CubicType.class, CubicType.NONE);
		// SkillSummon
		npcId = set.getInteger("npcId", 0); // default for undescribed skills
		expPenalty = set.getFloat("expPenalty", 0.f);
		// SkillChargeDmg
		numCharges = set.getInteger("num_charges", 0);
		// SkillCreateItem
		createItemId = set.getInteger("create_item_id", 0);
		createItemCount = set.getInteger("create_item_count", 0);
		randomCount = set.getInteger("random_count", 1);
		// SkillRecall
		recallType = set.getEnum("recallType", TeleportWhereType.class, null);
		String coords = set.getString("teleCoords", null);
		if (coords != null)
		{
			String[] valuesSplit = coords.split(",");
			loc = new LocationHolder(Integer.parseInt(valuesSplit[0]), Integer.parseInt(valuesSplit[1]), Integer.parseInt(valuesSplit[2]));
		}
		else
		{
			loc = null;
		}
	}
	
	public abstract void useSkill(L2Character caster, List<L2Object> targets);
	
	public final boolean isPotion()
	{
		return isPotion;
	}
	
	public final SkillType getSkillType()
	{
		return skillType;
	}
	
	public final SkillElementType getElement()
	{
		return element;
	}
	
	/**
	 * @return the target type of the skill : SELF, PARTY, CLAN, PET...
	 */
	public final SkillTargetType getTargetType()
	{
		return targetType;
	}
	
	/**
	 * @return skill saveVs base stat (STR, INT ...).
	 */
	public final BaseStatsType getSaveVs()
	{
		return saveVs;
	}
	
	public final SkillConditionType getBlowDamageCondition()
	{
		return blowDamageCondition;
	}
	
	public final boolean isOverhit()
	{
		return overhit;
	}
	
	/**
	 * @param  activeChar
	 * @return            the power of the skill.
	 */
	public final double getPower(L2Character activeChar)
	{
		if (activeChar == null)
		{
			return power;
		}
		
		switch (getId())
		{
			case 1159:// SkillType.DEATHLINK
			case 4654:// SkillType.DEATHLINK
			case 4655:// SkillType.DEATHLINK
			case 4656:// SkillType.DEATHLINK
				return power * Math.pow(1.7165 - (activeChar.getCurrentHp() / activeChar.getStat().getMaxHp()), 2) * 0.577;
			case 314:// SkillType.FATAL
				return power * 3.5 * (1 - (activeChar.getCurrentHp() / activeChar.getStat().getMaxHp()));
			default:
				return power;
		}
	}
	
	public final double getPower()
	{
		return power;
	}
	
	public final int getMagicLevel()
	{
		return magicLevel;
	}
	
	/**
	 * @return the additional effect power or base probability.
	 */
	public final int getEffectPower()
	{
		return effectPower;
	}
	
	/**
	 * @return the additional effect skill type (ex : STUN, PARALYZE,...).
	 */
	public final EffectType getEffectType()
	{
		return effectType;
	}
	
	/**
	 * @return the castRange.
	 */
	public final int getCastRange()
	{
		return castRange;
	}
	
	/**
	 * @return the effectRange.
	 */
	public final int getEffectRange()
	{
		return effectRange;
	}
	
	/**
	 * @return the maxCharges.
	 */
	public final int getMaxCharges()
	{
		return maxCharges;
	}
	
	/**
	 * @return the pet food
	 */
	public final int getFeed()
	{
		return feed;
	}
	
	/**
	 * @return the negateStats.
	 */
	public final Set<EffectType> getNegateStats()
	{
		return negateStats;
	}
	
	public List<Integer> getNegateIds()
	{
		return negateIds;
	}
	
	/**
	 * @return the maxNegatedEffects.
	 */
	public final int getMaxNegatedEffects()
	{
		return maxNegatedEffects;
	}
	
	/**
	 * @return the hitTime.
	 */
	public final int getHitTime()
	{
		return hitTime;
	}
	
	/**
	 * Set HitTime in ms
	 * @param time
	 */
	public void setHitTime(int time)
	{
		hitTime = time;
	}
	
	/**
	 * @return the coolTime.
	 */
	public final int getCoolTime()
	{
		return coolTime;
	}
	
	/**
	 * @return the hpConsume.
	 */
	public final int getHpConsume()
	{
		return hpConsume;
	}
	
	/**
	 * @return the id.
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * @return the item consume count.
	 */
	public final int getItemConsumeCount()
	{
		return itemConsumeCount;
	}
	
	/**
	 * @return the item consume id.
	 */
	public final int getItemConsumeId()
	{
		return itemConsumeId;
	}
	
	/**
	 * @return the item consume count over time.
	 */
	public final int getItemConsumeOT()
	{
		return itemConsumeOT;
	}
	
	/**
	 * @return the item consume Id over time.
	 */
	public final int getItemConsumeIdOT()
	{
		return itemConsumeIdOT;
	}
	
	/**
	 * @return the itemConsume time in milliseconds.
	 */
	public final int getItemConsumeTime()
	{
		return itemConsumeTime;
	}
	
	/**
	 * @return the item consume summons over time.
	 */
	public final int getItemConsumeSteps()
	{
		return itemConsumeSteps;
	}
	
	/**
	 * @return the summon total life
	 */
	public final int getTotalLifeTime()
	{
		return summonTotalLifeTime;
	}
	
	/**
	 * @return the summon time lost idle
	 */
	public final int getTimeLostIdle()
	{
		return summonTimeLostIdle;
	}
	
	/**
	 * @return the summon time active
	 */
	public final int getTimeLostActive()
	{
		return summonTimeLostActive;
	}
	
	/**
	 * @return the level.
	 */
	public final int getLevel()
	{
		return level;
	}
	
	/**
	 * @return the magic.
	 */
	public final boolean isMagic()
	{
		return magic;
	}
	
	/**
	 * @return the mpConsume.
	 */
	public final int getMpConsume()
	{
		return mpConsume;
	}
	
	/**
	 * @return the mpInitialConsume.
	 */
	public final int getMpInitialConsume()
	{
		return mpInitialConsume;
	}
	
	/**
	 * @return the name.
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * @return the reuse delay.
	 */
	public int getReuseDelay()
	{
		return reuseDelay;
	}
	
	/**
	 * @return get reuse hash code ((skillId * 256) + skillLevel)
	 */
	public final int getReuseHashCode()
	{
		return reuseHashCode;
	}
	
	/**
	 * @return get skill radius
	 */
	public int getSkillRadius()
	{
		return skillRadius;
	}
	
	public boolean isActive()
	{
		return operateType == SkillOpType.ACTIVE;
	}
	
	public boolean isPassive()
	{
		return operateType == SkillOpType.PASSIVE;
	}
	
	public boolean isToggle()
	{
		return operateType == SkillOpType.TOGGLE;
	}
	
	public int getNextDanceMpCost()
	{
		return nextDanceCost;
	}
	
	public int getBaseCritRate()
	{
		return baseCritRate;
	}
	
	public boolean useSoulShot()
	{
		switch (getSkillType())
		{
			case PDAM:
			case CHARGEDAM:
			case BLOW:
				return true;
			default:
				return false;
		}
	}
	
	public boolean useSpiritShot()
	{
		return isMagic();
	}
	
	public final boolean useFishShot()
	{
		switch (getSkillType())
		{
			case PUMPING:
			case REELING:
				return true;
			default:
				return false;
		}
	}
	
	public boolean getCanLearn(ClassId cls)
	{
		return (canLearn == null) || canLearn.contains(cls);
	}
	
	public boolean canTeachBy(int npcId)
	{
		return (teachers == null) || teachers.contains(npcId);
	}
	
	public boolean isHotSpringsDisease()
	{
		return (getId() == 4551) || (getId() == 4552) || (getId() == 4553) || (getId() == 4554);
	}
	
	public boolean isEnemyOnly()
	{
		switch (skillType)
		{
			case DEBUFF:
			case AGGDAMAGE:
			case AGGREDUCE_CHAR:
			case MANADAM:
				return true;
			default:
				return false;
		}
	}
	
	public boolean hasAggro()
	{
		switch (skillType)
		{
			case MANAHEAL:
			case BALANCE_LIFE:
			case BUFF:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isOffensive()
	{
		switch (skillType)
		{
			case PDAM:
			case MDAM:
			case CPDAMPERCENT:
			case BLOW:
			case AGGDAMAGE:
			case DEBUFF:
			case DRAIN:// se puede pasar como effecto
			case CHARGEDAM:
			case DETECT_WEAKNESS:
			case MANADAM:
			case MUTE:
			case SPOIL:
			case MANA_BY_LEVEL:
			case SWEEP:
			case DRAIN_SOUL:
			case AGGREDUCE:
			case AGGREMOVE:
			case AGGREDUCE_CHAR:
			case SOW:
			case HARVEST:
				return true;
			default:
				return false;
		}
	}
	
	public boolean checkCondition(L2Character activeChar, boolean itemOrWeapon)
	{
		Condition condition = preCondition;
		if (itemOrWeapon)
		{
			condition = itemPreCondition;
		}
		
		if (condition == null)
		{
			return true;
		}
		
		Env env = new Env();
		env.setPlayer(activeChar);
		env.setSkill(this);
		
		if (!condition.test(env))
		{
			String msg = condition.getMessage();
			
			if (msg != null)
			{
				if (msg.contains("s1 "))
				{
					msg = msg.replace("s1", getName());
					condition.setMessage(msg);
				}
				activeChar.sendMessage(msg);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * <B><U> Values of skill type</U> :</B><br>
	 * <li>ONE : The skill can only be used on the L2PcInstance targeted, or on the caster if it's a L2PcInstance and no L2PcInstance targeted
	 * <li>TARGET_NONE,
	 * <li>TARGET_SELF,
	 * <li>TARGET_ONE,
	 * <li>TARGET_PARTY,
	 * <li>TARGET_ALLY,
	 * <li>TARGET_CLAN,
	 * <li>TARGET_PET,
	 * <li>TARGET_AREA,
	 * <li>TARGET_AURA,
	 * <li>TARGET_CORPSE,
	 * <li>TARGET_UNDEAD,
	 * <li>TARGET_AURA_UNDEAD,
	 * <li>TARGET_FRONT_AREA,
	 * <li>TARGET_CORPSE_ALLY,
	 * <li>TARGET_CORPSE_PLAYER,
	 * <li>TARGET_CORPSE_PET,
	 * <li>TARGET_ITEM,
	 * <li>TARGET_AREA_CORPSE_MOB,
	 * <li>TARGET_CORPSE_MOB,
	 * <li>TARGET_AURA_MOB,
	 * <li>TARGET_MOB,
	 * <li>TARGET_UNLOCKABLE,
	 * <li>TARGET_HOLY,
	 * <li>TARGET_PARTY_MEMBER,
	 * <li>TARGET_OWNER_PET
	 * <li>UNLOCKABLE
	 * <li>ITEM
	 * @param  activeChar The L2Character who use the skill
	 * @param  onlyFirst
	 * @return            all targets of the skill in a table in function a the skill type.
	 */
	public final List<L2Object> getTargetList(L2Character activeChar, boolean onlyFirst)
	{
		// Init to null the target of the skill
		L2Character target = null;
		// Get the L2Objcet targeted by the user of the skill at this moment
		L2Object objTarget = activeChar.getTarget();
		// If the L2Object targeted is a L2Character, it becomes the L2Character target
		if (objTarget instanceof L2Character)
		{
			target = (L2Character) objTarget;
		}
		
		ITargetTypeHandler handler = TargetHandler.getHandler(getTargetType());
		if (handler != null)
		{
			try
			{
				return handler.getTargetList(this, activeChar, onlyFirst, target);
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Exception in Skill.getTargetList(): " + e.getMessage(), e);
			}
		}
		activeChar.sendMessage("Target type of skill is not currently handled.");
		return Collections.emptyList();
	}
	
	public final List<L2Object> getTargetList(L2Character activeChar)
	{
		return getTargetList(activeChar, false);
	}
	
	public final L2Object getFirstOfTargetList(L2Character activeChar)
	{
		List<L2Object> targets = getTargetList(activeChar, true);
		
		if ((targets.isEmpty()))
		{
			return null;
		}
		return targets.get(0);
	}
	
	public final List<Func> getStatFuncs(Effect effect, L2Character player)
	{
		if (!(player instanceof L2PcInstance) && !(player instanceof L2Attackable) && !(player instanceof L2Summon) && !(player instanceof L2SiegeFlagInstance))
		{
			return Collections.emptyList();
		}
		if (funcTemplates == null)
		{
			return Collections.emptyList();
		}
		
		affected = player;
		
		List<Func> funcs = new ArrayList<>(funcTemplates.size());
		
		Env env = new Env();
		env.setPlayer(player);
		env.setSkill(this);
		
		for (FuncTemplate t : funcTemplates)
		{
			Func f = t.getFunc(env, this); // skill is owner
			if (f != null)
			{
				funcs.add(f);
			}
		}
		if (funcs.isEmpty())
		{
			return Collections.emptyList();
		}
		return funcs;
	}
	
	public boolean hasEffects()
	{
		return ((effectTemplates != null) && (!effectTemplates.isEmpty()));
	}
	
	public boolean hasSelfEffects()
	{
		return ((effectTemplatesSelf != null) && (!effectTemplatesSelf.isEmpty()));
	}
	
	public List<EffectTemplate> getEffectsTemplates()
	{
		return effectTemplates;
	}
	
	public final List<Effect> getEffects(L2Character effector, L2Character effected)
	{
		return getEffects(effector, effected, true);
	}
	
	public final List<Effect> getEffects(L2Character effector, L2Character effected, boolean start)
	{
		if (isPassive() || !hasEffects())
		{
			return Collections.emptyList();
		}
		
		if ((effector != effected) && effected.isInvul())
		{
			return Collections.emptyList();
		}
		
		// No effects for doors & siege headquarters
		if ((effected instanceof L2DoorInstance) || (effected instanceof L2SiegeFlagInstance))
		{
			return Collections.emptyList();
		}
		
		var effects = new ArrayList<Effect>();
		
		var env = new Env();
		env.setSkillMastery(Formulas.calcSkillMastery(effector, this));
		env.setPlayer(effector);
		env.setTarget(effected);
		env.setSkill(this);
		
		for (var et : effectTemplates)
		{
			var e = et.getEffect(env);
			
			if (e != null)
			{
				// check calculate effect success if define power in effect
				var bss = effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
				var sps = effector.isChargedShot(ShotType.SPIRITSHOTS);
				var ss = effector.isChargedShot(ShotType.SOULSHOTS);
				
				if ((e.getRate() > 0) && !Formulas.calcEffectSuccess(effector, effected, this, e.getRate(), true, ss, sps, bss))
				{
					continue;
				}
				
				effects.add(e);
				if (start)
				{
					e.scheduleEffect();
				}
			}
		}
		
		return effects;
	}
	
	public final List<Effect> getEffectsSelf(L2Character effector)
	{
		if (isPassive() || !hasSelfEffects())
		{
			return Collections.emptyList();
		}
		
		List<Effect> effects = new ArrayList<>(effectTemplatesSelf.size());
		
		Env env = new Env();
		env.setPlayer(effector);
		env.setTarget(effector);
		env.setSkill(this);
		
		for (EffectTemplate et : effectTemplatesSelf)
		{
			Effect e = et.getEffect(env);
			
			if (e != null)
			{
				e.setSelfEffect();
				e.scheduleEffect();
				effects.add(e);
			}
		}
		
		return effects;
	}
	
	public final void attach(FuncTemplate f)
	{
		if (funcTemplates == null)
		{
			funcTemplates = new ArrayList<>(1);
		}
		
		funcTemplates.add(f);
	}
	
	public final void attach(EffectTemplate effect)
	{
		if (effectTemplates == null)
		{
			effectTemplates = new ArrayList<>(1);
		}
		
		effectTemplates.add(effect);
	}
	
	public final void attachSelf(EffectTemplate effect)
	{
		if (effectTemplatesSelf == null)
		{
			effectTemplatesSelf = new ArrayList<>(1);
		}
		
		effectTemplatesSelf.add(effect);
	}
	
	public final void attach(Condition c, boolean itemOrWeapon)
	{
		if (itemOrWeapon)
		{
			itemPreCondition = c;
		}
		else
		{
			preCondition = c;
		}
	}
	
	@Override
	public String toString()
	{
		return "" + name + "[id=" + id + ",lvl=" + level + "]";
	}
	
	public L2Character getAffected()
	{
		return affected;
	}
	
	/**
	 * @return the absorbPart
	 */
	public float getAbsorbPart()
	{
		return absorbPart;
	}
	
	/**
	 * @return the absorbAbs
	 */
	public int getAbsorbAbs()
	{
		return absorbAbs;
	}
	
	/**
	 * @return the cubicType
	 */
	public CubicType getCubicType()
	{
		return cubicType;
	}
	
	public int getSummonNpcId()
	{
		return npcId;
	}
	
	/**
	 * @return the expPenalty
	 */
	public float getExpPenalty()
	{
		return expPenalty;
	}
	
	/**
	 * @return the num_charges
	 */
	public int getNumCharges()
	{
		return numCharges;
	}
	
	/**
	 * @return the create_item_id
	 */
	public int getCreateItemId()
	{
		return createItemId;
	}
	
	/**
	 * @return the create_item_count
	 */
	public int getCreateItemCount()
	{
		return createItemCount;
	}
	
	/**
	 * @return the random_count
	 */
	public int getRandomCount()
	{
		return randomCount;
	}
	
	public LocationHolder getTeleLocation()
	{
		return loc;
	}
	
	public TeleportWhereType getRecallType()
	{
		return recallType;
	}
	
	// MISC -----------------------
	
	/**
	 * Check if target should be added to the target list.
	 * <ul>
	 * <li>Target is dead.</li>
	 * <li>Target same as caster.</li>
	 * <li>Target inside peace zone.</li>
	 * <li>Target in the same party than caster.</li>
	 * <li>Caster can see target.</li>
	 * </ul>
	 * Additional checks.
	 * <ul>
	 * <li>Mustn't be in Observer mode.</li>
	 * <li>If not in PvP zones (arena, siege): target in not the same clan and alliance with caster, and usual PvP skill check.</li>
	 * <li>In case caster and target are L2Attackable : verify if caster isn't confused.</li>
	 * </ul>
	 * Caution: distance is not checked.
	 * @param  caster        The skill caster.
	 * @param  target        The victim
	 * @param  sourceInArena True means the caster is in a pvp or siege zone, and so the additional check will be skipped.
	 * @return
	 */
	public final boolean checkForAreaOffensiveSkills(L2Character caster, L2Character target, boolean sourceInArena)
	{
		if ((target == null) || target.isDead() || (target == caster))
		{
			return false;
		}
		
		final L2PcInstance player = caster.getActingPlayer();
		final L2PcInstance targetPlayer = target.getActingPlayer();
		if ((player != null) && (targetPlayer != null))
		{
			if ((targetPlayer == caster) || (targetPlayer == player))
			{
				return false;
			}
			
			if (targetPlayer.inObserverMode())
			{
				return false;
			}
			
			if (isOffensive() && (player.getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED) && player.isInsideZone(ZoneType.SIEGE) && (player.getSiegeState() == targetPlayer.getSiegeState()))
			{
				return false;
			}
			
			if (target.isInsideZone(ZoneType.PEACE))
			{
				return false;
			}
			
			if (player.isInParty() && targetPlayer.isInParty())
			{
				// Same party
				if (player.getParty().getLeader().getObjectId() == targetPlayer.getParty().getLeader().getObjectId())
				{
					return false;
				}
				
				// Same commandchannel
				if ((player.getParty().getCommandChannel() != null) && (player.getParty().getCommandChannel() == targetPlayer.getParty().getCommandChannel()))
				{
					return false;
				}
			}
			
			if (!sourceInArena && !(targetPlayer.isInsideZone(ZoneType.PVP) && !targetPlayer.isInsideZone(ZoneType.SIEGE)))
			{
				if (player.getClan() != null)
				{
					if ((player.getAllyId() != 0) && (player.getAllyId() == targetPlayer.getAllyId()))
					{
						return false;
					}
					
					if ((player.getClanId() != 0) && (player.getClanId() == targetPlayer.getClanId()))
					{
						return false;
					}
				}
				
				if (!player.checkPvpSkill(targetPlayer, this, (caster instanceof L2Summon)))
				{
					return false;
				}
			}
		}
		else if (target instanceof L2Attackable)
		{
			if ((caster instanceof L2Attackable) && !caster.isConfused())
			{
				return false;
			}
			
			if (isOffensive() && !target.isAutoAttackable(caster))
			{
				return false;
			}
		}
		return GeoEngine.getInstance().canSeeTarget(caster, target);
	}
	
	/**
	 * @return the staticHitTime
	 */
	public boolean isStaticHitTime()
	{
		return staticHitTime;
	}
}
