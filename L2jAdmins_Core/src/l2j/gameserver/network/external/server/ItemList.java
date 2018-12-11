package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 27 00 00 01 00 // item count 04 00 // itemType1 0-weapon/ring/earring/necklace 1-armor/shield 4-item/questitem/adena c6 37 50 40 // objectId cd 09 00 00 // itemId 05 00 00 00 // count 05 00 // itemType2 0-weapon 1-shield/armor 2-ring/earring/necklace 3-questitem 4-adena 5-item 00 00 //
 * always 0 ?? 00 00 // equipped 1-yes 00 00 // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand 00 00 // always 0 ?? 00 00 // always 0 ?? format h (h dddhhhh hh) revision 377 format h (h dddhhhd hh)
 * revision 415
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class ItemList extends AServerPacket
{
	private final List<ItemInstance> items;
	private final boolean showWindow;
	
	public ItemList(L2PcInstance cha, boolean showWindow)
	{
		items = cha.getInventory().getItems();
		this.showWindow = showWindow;
	}
	
	public ItemList(List<ItemInstance> items, boolean showWindow)
	{
		this.items = items;
		this.showWindow = showWindow;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x1b);
		if (showWindow)
		{
			writeH(0x01);
		}
		else
		{
			writeH(0x00);
		}
		
		writeH(items.size());
		
		for (int i = items.size() - 1; i >= 0; i--)
		{
			ItemInstance item = items.get(i);
			if ((item == null) || (item.getItem() == null))
			{
				continue;
			}
			
			writeH(item.getItem().getType1().getMask()); // item type1
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal()); // item type2
			writeH(item.getCustomType1()); // item type3
			if (item.isEquipped())
			{
				writeH(0x01);
			}
			else
			{
				writeH(0x00);
			}
			writeD(item.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(item.getEnchantLevel()); // enchant level
			// race tickets
			writeH(item.getCustomType2()); // item type3
		}
	}
}
