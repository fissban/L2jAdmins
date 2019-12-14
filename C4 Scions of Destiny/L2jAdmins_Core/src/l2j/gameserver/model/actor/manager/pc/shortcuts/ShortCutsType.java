package l2j.gameserver.model.actor.manager.pc.shortcuts;

/**
 * @author fissban
 */
public enum ShortCutsType
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
