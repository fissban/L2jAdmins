package main.holders;

/**
 * @author fissban
 */
public class RewardHolder
{
	private int id;
	private int count;
	private int chance;
	
	/**
	 * @param rewardId
	 * @param rewardCount
	 */
	public RewardHolder(int rewardId, int rewardCount)
	{
		this.id = rewardId;
		this.count = rewardCount;
		this.chance = 100;
	}
	
	/**
	 * @param rewardId
	 * @param rewardCount
	 * @param rewardChance
	 */
	public RewardHolder(int rewardId, int rewardCount, int rewardChance)
	{
		this.id = rewardId;
		this.count = rewardCount;
		this.chance = rewardChance;
	}
	
	public int getRewardId()
	{
		return id;
	}
	
	public int getRewardCount()
	{
		return count;
	}
	
	public int getRewardChance()
	{
		return chance;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public void setChance(int chance)
	{
		this.chance = chance;
	}
}
