package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class GMViewItemList extends AServerPacket
{
	private final List<ItemInstance> items;
	private final L2PcInstance cha;
	private final String playerName;
	
	public GMViewItemList(L2PcInstance cha)
	{
		items = cha.getInventory().getItems();
		playerName = cha.getName();
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x94);
		writeS(playerName);
		writeD(cha.getInventoryLimit()); // inventory limit
		writeH(0x01); // show window ??
		writeH(items.size());
		
		for (final ItemInstance temp : items)
		{
			if ((temp == null) || (temp.getItem() == null))
			{
				continue;
			}
			
			writeH(temp.getItem().getType1().getMask()); // item type1
			writeD(temp.getObjectId());
			writeD(temp.getId());
			writeD(temp.getCount());
			writeH(temp.getItem().getType2().ordinal()); // item type2
			writeH(temp.getCustomType1()); // item type3
			writeH(temp.isEquipped() ? 0x01 : 0x00);
			writeD(temp.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(temp.getEnchantLevel()); // enchant level
			// race tickets
			writeH(temp.getCustomType2()); // item type3
		}
	}
}
