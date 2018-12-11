package l2j.gameserver.model.holder;

import l2j.Config;

public class SeedDataHolder
{
	private int id;
	private final int level; // seed level
	private final int crop; // crop type
	private final int mature; // mature crop type
	private int type1;
	private int type2;
	private int manorId; // id of manor (castle id) where seed can be farmed
	private int isAlternative;
	private int limitSeeds;
	private int limitCrops;
	
	public SeedDataHolder(int level, int crop, int mature)
	{
		this.level = level;
		this.crop = crop;
		this.mature = mature;
	}
	
	public void setData(int id, int t1, int t2, int manorId, int isAlt, int lim1, int lim2)
	{
		this.id = id;
		type1 = t1;
		type2 = t2;
		this.manorId = manorId;
		isAlternative = isAlt;
		limitSeeds = lim1;
		limitCrops = lim2;
	}
	
	public int getManorId()
	{
		return manorId;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getCrop()
	{
		return crop;
	}
	
	public int getMature()
	{
		return mature;
	}
	
	public int getReward(int type)
	{
		return (type == 1 ? type1 : type2);
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public boolean isAlternative()
	{
		return (isAlternative == 1);
	}
	
	public int getSeedLimit()
	{
		return limitSeeds * Config.DROP_AMOUNT_MANOR;
	}
	
	public int getCropLimit()
	{
		return limitCrops * Config.DROP_AMOUNT_MANOR;
	}
	
}
