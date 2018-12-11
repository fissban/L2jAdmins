package l2j.gameserver.model.shortcuts;

/**
 * @author fissban
 */
public enum PcShortCutsType
{
	ITEM,
	SKILL,
	ACTION,
	MACRO,
	RECIPE;
	
	public int getMask()
	{
		return ordinal() + 1;
	}
}
