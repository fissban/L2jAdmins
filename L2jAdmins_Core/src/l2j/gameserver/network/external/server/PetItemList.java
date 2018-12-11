package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class PetItemList extends AServerPacket
{
	private final List<ItemInstance> items;
	
	public PetItemList(L2PetInstance cha)
	{
		items = cha.getInventory().getItems();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xB2);
		
		writeH(items.size());
		
		for (final ItemInstance temp : items)
		{
			if (temp == null)
			{
				continue;
			}
			
			writeH(temp.getItem().getType1().getMask()); // item type1
			writeD(temp.getObjectId());
			writeD(temp.getId());
			writeD(temp.getCount());
			writeH(temp.getItem().getType2().ordinal()); // item type2
			writeH(0xff); // ?
			if (temp.isEquipped())
			{
				writeH(0x01);
			}
			else
			{
				writeH(0x00);
			}
			writeD(temp.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(temp.getEnchantLevel()); // enchant level
			writeH(0x00); // ?
		}
	}
}
