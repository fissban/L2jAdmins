package l2j.gameserver.model.holder;

/**
 * This class stores Soul Crystal leveling infos related to items, notably:
 * <ul>
 * <li>The current level on the hierarchy tree of items ;</li>
 * <li>The initial itemId from where we start ;</li>
 * <li>The succeeded itemId rewarded if absorb was successful ;</li>
 * <li>The broken itemId rewarded if absorb failed.</li>
 * </ul>
 */
public final class SoulCrystalHolder
{
	private final int level;
	private final int initialItemId;
	private final int stagedItemId;
	private final int brokenItemId;
	
	public SoulCrystalHolder(int level, int initialItemId, int stagedItemId, int brokenItemId)
	{
		this.level = level;
		this.initialItemId = initialItemId;
		this.stagedItemId = stagedItemId;
		this.brokenItemId = brokenItemId;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getInitialItemId()
	{
		return initialItemId;
	}
	
	public int getStagedItemId()
	{
		return stagedItemId;
	}
	
	public int getBrokenItemId()
	{
		return brokenItemId;
	}
}
