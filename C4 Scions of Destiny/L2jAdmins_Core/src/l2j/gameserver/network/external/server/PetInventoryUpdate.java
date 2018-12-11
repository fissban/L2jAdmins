package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.holder.ItemInfoHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @author  Yme
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $ Rebuild 23.2.2006 by Advi
 */
public class PetInventoryUpdate extends AServerPacket
{
	private final List<ItemInfoHolder> itemList;
	
	public PetInventoryUpdate()
	{
		itemList = new ArrayList<>();
	}
	
	/**
	 * @param items
	 */
	public PetInventoryUpdate(List<ItemInfoHolder> items)
	{
		itemList = items;
	}
	
	public void addItem(ItemInstance item)
	{
		itemList.add(new ItemInfoHolder(item));
	}
	
	public void addNewItem(ItemInstance item)
	{
		itemList.add(new ItemInfoHolder(item, ChangeType.ADDED));
	}
	
	public void addModifiedItem(ItemInstance item)
	{
		itemList.add(new ItemInfoHolder(item, ChangeType.MODIFIED));
	}
	
	public void addRemovedItem(ItemInstance item)
	{
		itemList.add(new ItemInfoHolder(item, ChangeType.REMOVED));
	}
	
	public void addItems(List<ItemInstance> items)
	{
		for (ItemInstance item : items)
		{
			itemList.add(new ItemInfoHolder(item));
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb3);
		writeH(itemList.size());
		for (ItemInfoHolder item : itemList)
		{
			writeH(item.getChange().ordinal());
			writeH(item.getItem().getType1().getMask()); // item type1
			writeD(item.getObjectId());
			writeD(item.getItem().getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal()); // item type2
			writeH(0x00); // ?
			writeH(item.getEquipped());
			writeD(item.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(item.getEnchant()); // enchant level
			writeH(item.getCustomType2());
		}
	}
}
