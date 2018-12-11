package l2j.gameserver.model.items.enums;

/**
 * @author fissban
 */
public enum ItemType1
{
	WEAPON_RING_EARRING_NECKLACE(0),
	SHIELD_ARMOR(1),
	ITEM_QUESTITEM_ADENA(4);
	
	int mask;
	
	ItemType1(int mask)
	{
		this.mask = mask;
	}
	
	public int getMask()
	{
		return mask;
	}
}
