package l2j.gameserver.model.actor.manager.pc.shortcuts;

/**
 * This class ...
 * @version $Revision: 1.3.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class ShortCutsHolder
{
	private final int slot;
	private final int page;
	private final ShortCutsType type;
	private final int id;
	private final int level;
	private final int characterType;
	
	public ShortCutsHolder(int slotId, int pageId, ShortCutsType shortcutType, int shortcutId, int shortcutLevel, int characterType)
	{
		slot = slotId;
		page = pageId;
		type = shortcutType;
		id = shortcutId;
		level = shortcutLevel;
		this.characterType = characterType;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getPage()
	{
		return page;
	}
	
	public int getSlot()
	{
		return slot;
	}
	
	public ShortCutsType getType()
	{
		return type;
	}
	
	public int getCharacterType()
	{
		return characterType;
	}
}
