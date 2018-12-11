package l2j.gameserver.model.shortcuts;

/**
 * This class ...
 * @version $Revision: 1.3.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class PcShortCutsInstance
{
	private final int slot;
	private final int page;
	private final PcShortCutsType type;
	private final int id;
	private final int level;
	private final int characterType;
	
	public PcShortCutsInstance(int slotId, int pageId, PcShortCutsType shortcutType, int shortcutId, int shortcutLevel, int characterType)
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
	
	public PcShortCutsType getType()
	{
		return type;
	}
	
	public int getCharacterType()
	{
		return characterType;
	}
}
