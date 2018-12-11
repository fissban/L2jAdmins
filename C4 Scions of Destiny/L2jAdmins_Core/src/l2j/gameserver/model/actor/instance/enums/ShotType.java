package l2j.gameserver.model.actor.instance.enums;

/**
 * @author UnAfraid
 */
public enum ShotType
{
	SOULSHOTS,
	SPIRITSHOTS,
	BLESSED_SPIRITSHOTS,
	FISH_SOULSHOTS;
	
	private final int mask;
	
	private ShotType()
	{
		mask = (1 << ordinal());
	}
	
	public int getMask()
	{
		return mask;
	}
}
