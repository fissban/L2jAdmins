package l2j.gameserver.model.actor.base;

import l2j.gameserver.data.ExperienceData;

/**
 * Character Sub-Class Definition <BR>
 * Used to store key information about a character's sub-class.
 * @author Tempy
 */
public final class SubClass
{
	private PlayerClass playerClass;
	private long exp = ExperienceData.getInstance().getExpForLevel(40);
	private final int maxLevel = ExperienceData.getInstance().getMaxLevel();
	private int sp = 0;
	private int level = 40;
	private int classIndex = 1;
	
	public SubClass(int classId, int exp, int sp, int level, int classIndex)
	{
		playerClass = PlayerClass.values()[classId];
		this.exp = exp;
		this.sp = sp;
		this.level = level;
		this.classIndex = classIndex;
	}
	
	public SubClass(int classId, int classIndex)
	{
		// Used for defining a sub class using default values for XP, SP and player level.
		playerClass = PlayerClass.values()[classId];
		this.classIndex = classIndex;
	}
	
	public SubClass()
	{
		// Used for specifying ALL attributes of a sub class directly,
		// using the preset default values.
	}
	
	public PlayerClass getClassDefinition()
	{
		return playerClass;
	}
	
	public int getClassId()
	{
		return playerClass.ordinal();
	}
	
	public long getExp()
	{
		return exp;
	}
	
	public int getSp()
	{
		return sp;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getClassIndex()
	{
		return classIndex;
	}
	
	public void setClassId(int classId)
	{
		playerClass = PlayerClass.values()[classId];
	}
	
	public void setExp(long value)
	{
		if (value > ExperienceData.getInstance().getExpForLevel(maxLevel))
		{
			value = ExperienceData.getInstance().getExpForLevel(maxLevel);
		}
		
		exp = value;
	}
	
	public void setSp(int spValue)
	{
		sp = spValue;
	}
	
	public void setClassIndex(int classIndex)
	{
		this.classIndex = classIndex;
	}
	
	/**
	 * Set max level for subclass
	 * @param levelValue
	 */
	public void setLevel(int levelValue)
	{
		if (levelValue > maxLevel)
		{
			levelValue = maxLevel;
		}
		else if (levelValue < 40)
		{
			levelValue = 40;
		}
		
		level = levelValue;
	}
	
	/**
	 * Increase your actual level
	 */
	public void incLevel()
	{
		if (getLevel() == maxLevel)
		{
			return;
		}
		
		level++;
		setExp(ExperienceData.getInstance().getExpForLevel(getLevel()));
	}
	
	public void decLevel()
	{
		if (getLevel() == 40)
		{
			return;
		}
		
		level--;
		setExp(ExperienceData.getInstance().getExpForLevel(getLevel()));
	}
}
