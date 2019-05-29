package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.holder.ItemInfoHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 37 // Packet Identifier <BR>
 * 01 00 // Number of ItemInfo Trame of the Packet <BR>
 * <BR>
 * 03 00 // Update type : 01-add, 02-modify, 03-remove <BR>
 * 04 00 // Item Type 1 : 00-weapon/ring/earring/necklace, 01-armor/shield, 04-item/questitem/adena <BR>
 * c6 37 50 40 // ObjectId <BR>
 * cd 09 00 00 // ItemId <BR>
 * 05 00 00 00 // Quantity <BR>
 * 05 00 // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item <BR>
 * 00 00 // Filler (always 0) <BR>
 * 00 00 // Equipped : 00-No, 01-yes <BR>
 * 00 00 // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand <BR>
 * 00 00 // Enchant level (pet level shown in control item) <BR>
 * 00 00 // Pet name exists or not shown in control item <BR>
 * <BR>
 * <BR>
 * format h (hh dddhhhh hh) revision 377 <BR>
 * format h (hh dddhhhd hh) revision 415 <BR>
 * @version $Revision: 1.3.2.2.2.4 $ $Date: 2005/03/27 15:29:39 $ Rebuild 23.2.2006 by Advi
 */
public class InventoryUpdate extends AServerPacket
{
	private List<ItemInfoHolder> itemsList = new ArrayList<>();
	
	public InventoryUpdate()
	{
		//
	}
	
	public InventoryUpdate(ItemInstance... items)
	{
		addItems(items);
	}
	
	public InventoryUpdate(List<ItemInstance> items)
	{
		addItems(items);
	}
	
	public void addItems(ItemInstance... items)
	{
		for (var item : items)
		{
			synchronized (item)
			{
				itemsList.add(new ItemInfoHolder(item));
			}
		}
	}
	
	public void addItems(List<ItemInstance> items)
	{
		for (var item : items)
		{
			synchronized (item)
			{
				itemsList.add(new ItemInfoHolder(item));
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x27);
		writeH(itemsList.size());
		for (ItemInfoHolder item : itemsList)
		{
			writeH(item.getChange().ordinal()); // Update type : 01-add, 02-modify, 03-remove
			writeH(item.getItem().getType1().getMask()); // Item Type 1 : 00-weapon/ring/earring/necklace, 01-armor/shield, 04-item/quest item/adena
			writeD(item.getObjectId()); // ObjectId
			writeD(item.getItem().getId()); // ItemId
			writeD(item.getCount()); // Quantity
			writeH(item.getItem().getType2().ordinal());
			writeH(item.getCustomType1()); // Filler (always 0)
			writeH(item.getEquipped()); // Equipped : 00-No, 01-yes
			
			writeD(item.getItem().getBodyPart().getMask()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
			writeH(item.getEnchant()); // Enchant level (pet level shown in control item)
			writeH(item.getCustomType2()); // Pet name exists or not shown in control item
		}
	}
}
