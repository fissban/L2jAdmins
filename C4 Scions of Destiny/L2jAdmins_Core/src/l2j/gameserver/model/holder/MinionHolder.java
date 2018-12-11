package l2j.gameserver.model.holder;

import l2j.util.Rnd;

/**
 * This class defines the spawn data of a Minion type In a group mob, there are one master called RaidBoss and several slaves called Minions. <B><U> Data</U> :</B><BR>
 * <li>minionId : The Identifier of the Minion to spawn</li>
 * <li>minionAmount : The number of this Minion Type to spawn</li>
 */
public class MinionHolder
{
	/** The Identifier of the L2Minion */
	private final int id;
	
	/** The number of this Minion Type to spawn */
	private int minionAmount;
	private final int amountMin;
	private final int amountMax;
	
	public MinionHolder(int minionId, int minionAmountMin, int minionAmountMax)
	{
		id = minionId;
		amountMin = minionAmountMin;
		amountMax = minionAmountMax;
	}
	
	/**
	 * Return the Identifier of the Minion to spawn.
	 * @return
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Return the amount of this Minion type to spawn.
	 * @return
	 */
	public int getAmount()
	{
		if (amountMax > amountMin)
		{
			minionAmount = Rnd.get(amountMin, amountMax);
			return minionAmount;
		}
		return amountMin;
	}
}
