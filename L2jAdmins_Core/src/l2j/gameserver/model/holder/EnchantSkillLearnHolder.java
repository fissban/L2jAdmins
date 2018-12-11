package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public final class EnchantSkillLearnHolder
{
	// these two build the primary key
	private final int id;
	private final int level;
	
	// not needed, just for easier debug
	private final String name;
	
	private final int spCost;
	private final int baseLvl;
	private final int minSkillLevel;
	private final int exp;
	private final byte rate76;
	private final byte rate77;
	private final byte rate78;
	
	public EnchantSkillLearnHolder(int id, int level, int minSkillLvl, int baseLevel, String name, int cost, int exp, byte rate76, byte rate77, byte rate78)
	{
		this.id = id;
		this.level = level;
		baseLvl = baseLevel;
		minSkillLevel = minSkillLvl;
		this.name = name.intern();
		spCost = cost;
		this.exp = exp;
		this.rate76 = rate76;
		this.rate77 = rate77;
		this.rate78 = rate78;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * @return Returns the minLevel.
	 */
	public int getBaseLevel()
	{
		return baseLvl;
	}
	
	/**
	 * @return Returns the minSkillLevel.
	 */
	public int getMinSkillLevel()
	{
		return minSkillLevel;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return Returns the spCost.
	 */
	public int getSpCost()
	{
		return spCost;
	}
	
	/**
	 * @return Returns the exp.
	 */
	public int getExp()
	{
		return exp;
	}
	
	public byte getRate(L2PcInstance ply)
	{
		switch (ply.getLevel())
		{
			case 76:
				return rate76;
			case 77:
				return rate77;
			case 78:
				return rate78;
			default:
				return rate78;
		}
	}
}
