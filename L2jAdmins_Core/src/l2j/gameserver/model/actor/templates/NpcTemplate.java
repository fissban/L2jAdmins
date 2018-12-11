package l2j.gameserver.model.actor.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.enums.NpcRaceType;
import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.gameserver.model.holder.MinionHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;

/**
 * This class contains all generic data of a L2Spawn object.<BR>
 * @version $Revision: 1.1.2.4 $ $Date: 2005/04/02 15:57:51 $
 */
public final class NpcTemplate extends CharTemplate
{
	protected static final Logger LOG = Logger.getLogger(NpcTemplate.class.getName());
	
	private final int npcId;
	private final int idTemplate;
	private final String type;
	private final String name;
	private final String title;
	private final int level;
	private int rewardExp;
	private int rewardSp;
	private final int aggroRange;
	private final int rhand;
	private final int lhand;
	private String factionId;
	private final int factionRange;
	private final short ss;
	private final short bss;
	private final short ssRate;
	
	private boolean isQuestMonster = false;
	private boolean isMerchant = false;
	private boolean isManor = false;
	private boolean isFisher = false;
	private boolean isTrainer = false;
	private boolean isAuctioner = false;
	private boolean isWarehouse = false;
	
	private NpcRaceType race;
	private final String jClass;
	
	/** The table containing all Item that can be dropped by L2NpcInstance using this L2NpcTemplate */
	private final List<DropCategory> categories = new ArrayList<>();
	
	/** The table containing all Minions that must be spawn with the L2NpcInstance using this L2NpcTemplate */
	private final List<MinionHolder> minions = new ArrayList<>();
	
	private final List<ClassId> teachInfo = new ArrayList<>();
	private final Map<Integer, Skill> skills = new HashMap<>();
	
	// contains a list of quests for each event type (questStart, questAttack, questKill, etc)
	private final Map<ScriptEventType, List<Script>> scriptEvents = new HashMap<>();
	
	/**
	 * Constructor of L2Character.
	 * @param set The StatsSet object to transfer data to the method
	 */
	public NpcTemplate(StatsSet set)
	{
		super(set);
		npcId = set.getInteger("npcId");
		idTemplate = set.getInteger("idTemplate", npcId);
		type = set.getString("type");
		name = set.getString("name");
		title = set.getString("title", "");
		if (title.equalsIgnoreCase("Quest Monster"))
		{
			isQuestMonster = true;
		}
		level = set.getInteger("level", 1);
		rewardExp = set.getInteger("rewardExp", 0);
		rewardSp = set.getInteger("rewardSp", 0);
		aggroRange = set.getInteger("aggroRange", 0);
		rhand = set.getInteger("rhand");
		lhand = set.getInteger("lhand");
		
		final String f = set.getString("factionId", null);
		if (f == null)
		{
			factionId = null;
		}
		else
		{
			factionId = f.intern();
		}
		
		factionRange = set.getInteger("factionRange", 0);
		ss = (short) set.getInteger("ss", 0);
		bss = (short) set.getInteger("bss", 0);
		ssRate = (short) set.getInteger("ssRate", 0);
		race = NpcRaceType.NONE;
		jClass = set.getString("jClass");
	}
	
	public void addTeachInfo(ClassId classId)
	{
		teachInfo.add(classId);
	}
	
	public List<ClassId> getTeachInfo()
	{
		return teachInfo;
	}
	
	public boolean canTeach(ClassId classId)
	{
		// If the player is on a third class, fetch the class teacher
		// information for its parent class.
		if (classId.getId() >= 88)
		{
			return teachInfo.contains(classId.getParent());
		}
		
		return teachInfo.contains(classId);
	}
	
	/**
	 * Add new drop category
	 * @param cat
	 */
	public void addDropCategory(DropCategory cat)
	{
		categories.add(cat);
	}
	
	public void addMinion(MinionHolder minion)
	{
		minions.add(minion);
	}
	
	public void addSkill(Skill skill)
	{
		skills.put(skill.getId(), skill);
	}
	
	public List<DropCategory> getDropsCategory()
	{
		return categories;
	}
	
	/**
	 * if full drops and part drops, mats, miscellaneous & UNCATEGORIZED
	 * @return the list of all possible item drops of this L2NpcTemplate.
	 */
	public List<DropInstance> getAllDrops()
	{
		List<DropInstance> list = new ArrayList<>();
		for (DropCategory tmp : categories)
		{
			list.addAll(tmp.getAllDrops());
		}
		return list;
	}
	
	/**
	 * @return the list of all Minions that must be spawn with the L2NpcInstance using this L2NpcTemplate.
	 */
	public List<MinionHolder> getMinions()
	{
		return minions;
	}
	
	@Override
	public Map<Integer, Skill> getSkills()
	{
		return skills;
	}
	
	public void addScriptEvent(ScriptEventType eventType, Script script)
	{
		List<Script> eventList = scriptEvents.get(eventType);
		
		if (eventList == null)
		{
			eventList = new ArrayList<>();
			eventList.add(script);
			scriptEvents.put(eventType, eventList);
		}
		else
		{
			eventList.remove(script);
			
			if (!eventType.isMultipleRegistrationAllowed() && !eventList.isEmpty())
			{
				LOG.warning("Quest event not allowed in multiple quests. Skipped addition of Event Type \"" + eventType + "\" for NPC \"" + name + "\" and quest \"" + script.getName() + "\".");
			}
			else
			{
				eventList.add(script);
			}
		}
	}
	
	public void removeScript(Script script)
	{
		for (final Entry<ScriptEventType, List<Script>> entry : scriptEvents.entrySet())
		{
			if (entry.getValue().contains(script))
			{
				final Iterator<Script> it = entry.getValue().iterator();
				while (it.hasNext())
				{
					final Script s = it.next();
					if (s.getId() == script.getId())
					{
						it.remove();
					}
				}
				
				if (entry.getValue().isEmpty())
				{
					scriptEvents.remove(entry.getKey());
				}
			}
		}
	}
	
	public List<Script> getEventScript(ScriptEventType eventType)
	{
		return scriptEvents.get(eventType);
	}
	
	public Map<ScriptEventType, List<Script>> getEventScripts()
	{
		return scriptEvents;
	}
	
	public NpcRaceType getRace()
	{
		return race;
	}
	
	public void setRace(NpcRaceType npcRaceType)
	{
		race = npcRaceType;
	}
	
	public int getId()
	{
		return npcId;
	}
	
	public int getIdTemplate()
	{
		return idTemplate;
	}
	
	public String getType()
	{
		return type;
	}
	
	/**
	 * Checks types, ignore case.
	 * @param  type the type to check.
	 * @return      {@code true} if the type are the same, {@code false} otherwise.
	 */
	public boolean isType(String type)
	{
		return this.type.equalsIgnoreCase(type);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public byte getLevel()
	{
		return (byte) level;
	}
	
	public int getRewardExp()
	{
		return rewardExp;
	}
	
	public void incRewardExp(int exp)
	{
		rewardExp *= exp;
	}
	
	public void decRewardExp(int exp)
	{
		rewardExp /= exp;
	}
	
	public int getRewardSp()
	{
		return rewardSp;
	}
	
	public void incRewardSp(int sp)
	{
		rewardSp *= sp;
	}
	
	public void decRewardSp(int sp)
	{
		rewardSp /= sp;
	}
	
	public int getAggroRange()
	{
		return aggroRange;
	}
	
	public int getRhand()
	{
		return rhand;
	}
	
	public int getLhand()
	{
		return lhand;
	}
	
	public String getFactionId()
	{
		return factionId;
	}
	
	public int getFactionRange()
	{
		return factionRange;
	}
	
	public boolean isQuestMonster()
	{
		return isQuestMonster;
	}
	
	public String getjClass()
	{
		return jClass;
	}
	
	public short getSsRate()
	{
		return ssRate;
	}
	
	public short getBss()
	{
		return bss;
	}
	
	public short getSs()
	{
		return ss;
	}
	
	/**
	 * True = esta habilitado vender items <br>
	 * False = no esta habilitado avender items (Default)<br>
	 * @return boolean
	 */
	public boolean isMerchant()
	{
		return isMerchant;
	}
	
	/**
	 * True = esta habilitado a vender items <br>
	 * False = no esta habilitado a vender items (Default)<br>
	 * @param val
	 */
	public void setMerchant(boolean val)
	{
		isMerchant = val;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @return boolean
	 */
	public boolean isManor()
	{
		return isManor;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @param val
	 */
	public void setManor(boolean val)
	{
		isManor = val;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @return boolean
	 */
	public boolean isFisher()
	{
		return isFisher;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @param val
	 */
	public void setFisher(boolean val)
	{
		isFisher = val;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @return boolean
	 */
	public boolean isTrainer()
	{
		return isTrainer;
	}
	
	/**
	 * True = esta habilitado a vender seeds<br>
	 * False = no esta habilitado a vender seeds (Default)<br>
	 * @param val
	 */
	public void setTrainer(boolean val)
	{
		isTrainer = val;
	}
	
	public boolean isAuctioner()
	{
		return isAuctioner;
	}
	
	public void setAuctioner(boolean val)
	{
		isAuctioner = val;
	}
	
	public boolean isWarehouse()
	{
		return isWarehouse;
	}
	
	public void setWarehouse(boolean val)
	{
		isWarehouse = val;
	}
}
