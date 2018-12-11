package l2j.gameserver.model.fishing;

import l2j.gameserver.model.fishing.enums.PcFishLureType;

public class PcFishingInstance
{
	private int id;
	private int level;
	private String name;
	private int hp;
	private int hpRegen;
	private int type;
	private PcFishLureType lureType;
	private int fishGuts;
	private int gutsCheckTime;
	private int waitTime;
	private int combatTime;
	
	public PcFishingInstance(int id, int lvl, String name, int hp, int hpRegen, int type, PcFishLureType lureType, int fishGuts, int gutsCheckTime, int waitTime, int combatTime)
	{
		this.id = id;
		level = lvl;
		this.name = name.intern();
		this.hp = hp;
		this.hpRegen = hpRegen;
		this.type = type;
		this.lureType = lureType;
		this.fishGuts = fishGuts;
		this.gutsCheckTime = gutsCheckTime;
		this.waitTime = waitTime;
		this.combatTime = combatTime;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getHP()
	{
		return hp;
	}
	
	public int getHpRegen()
	{
		return hpRegen;
	}
	
	public int getType()
	{
		return type;
	}
	
	public PcFishLureType getLureType()
	{
		return lureType;
	}
	
	public int getFishGuts()
	{
		return fishGuts;
	}
	
	public int getGutsCheckTime()
	{
		return gutsCheckTime;
	}
	
	public int getWaitTime()
	{
		return waitTime;
	}
	
	public int getCombatTime()
	{
		return combatTime;
	}
}
