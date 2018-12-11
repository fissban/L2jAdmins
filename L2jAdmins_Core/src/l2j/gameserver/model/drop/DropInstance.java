package l2j.gameserver.model.drop;

/**
 * Special thanks to nuocnam
 * @author: LittleVexy
 */
public class DropInstance
{
	public static final int MAX_CHANCE = 1000000;
	
	private int itemId;
	private int minDrop;
	private int maxDrop;
	private int chance;
	
	public DropInstance(int itemId, int minDrop, int maxDrop, double chance)
	{
		this.itemId = itemId;
		this.minDrop = minDrop;
		this.maxDrop = maxDrop;
		this.chance = (int) ((chance / 100) * DropInstance.MAX_CHANCE);
	}
	
	/**
	 * Returns the ID of the item dropped
	 * @return int
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Returns the minimum quantity of items dropped
	 * @return int
	 */
	public int getMinDrop()
	{
		return minDrop;
	}
	
	/**
	 * Returns the maximum quantity of items dropped
	 * @return int
	 */
	public int getMaxDrop()
	{
		return maxDrop;
	}
	
	/**
	 * Returns the chance of having a drop
	 * @return int
	 */
	public int getChance()
	{
		return chance;
	}
	
	public void increaseChance(double chanceIncrease)
	{
		chance *= chanceIncrease;
	}
	
	public void decreaseChance(float value)
	{
		chance /= value;
	}
}
