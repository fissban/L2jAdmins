package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:40 $
 */
public class PrivateStoreManageListBuy extends AServerPacket
{
	private final L2PcInstance player;
	private final List<ItemInstance> itemList;
	private final List<TradeItemHolder> buyList;
	
	public PrivateStoreManageListBuy(L2PcInstance player)
	{
		this.player = player;
		itemList = player.getInventory().getUniqueItems(false, true);
		buyList = player.getPrivateStore().getBuyList().getItems();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb7);
		// section 1
		writeD(player.getObjectId());
		writeD(player.getInventory().getAdena());
		
		// section2
		writeD(itemList.size()); // inventory items for potential buy
		for (ItemInstance item : itemList)
		{
			writeD(item.getId());
			writeH(0); // show enchant lvl as 0, as you can't buy enchanted weapons
			writeD(item.getCount());
			writeD(item.getReferencePrice());
			writeH(0x00);
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getItem().getType2().ordinal());
		}
		
		// section 3
		writeD(buyList.size()); // count for all items already added for buy
		for (TradeItemHolder item : buyList)
		{
			writeD(item.getItem().getId());
			writeH(0);
			writeD(item.getCount());
			writeD(item.getItem().getReferencePrice());
			writeH(0x00);
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getItem().getType2().ordinal());
			writeD(item.getPrice());// your price
			writeD(item.getItem().getReferencePrice());// fixed store price
		}
	}
}
