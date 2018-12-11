package l2j.gameserver.model.holder;

/**
 * This class ...
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public final class SkillLearnHolder
{
	// these two build the primary key
	private final int id;
	private final int level;
	
	// not needed, just for easier debug
	private final String name;
	
	private final int spCost;
	private final int minLevel;
	private final int costId;
	private final int costCount;
	
	public SkillLearnHolder(int id, int lvl, int minLvl, String name, int spCost, int costId, int costCount)
	{
		this.id = id;
		level = lvl;
		minLevel = minLvl;
		this.name = name.intern();
		this.spCost = spCost;
		this.costId = costId;
		this.costCount = costCount;
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
	public int getMinLevel()
	{
		return minLevel;
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
	
	public int getIdCost()
	{
		return costId;
	}
	
	public int getCostCount()
	{
		return costCount;
	}
}
