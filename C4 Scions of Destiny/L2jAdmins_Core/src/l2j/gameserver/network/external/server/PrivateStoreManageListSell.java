package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * 3 section to this packet 1)playerinfo which is always sent dd 2)list of items which can be added to sell d(hhddddhhhd) 3)list of items which have already been setup for sell in previous sell private store sell manageent d(hhddddhhhdd) *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreManageListSell extends AServerPacket
{
	private final L2PcInstance player;
	private final List<TradeItemHolder> itemList;
	private final List<TradeItemHolder> sellList;
	
	public PrivateStoreManageListSell(L2PcInstance player)
	{
		this.player = player;
		this.player.getPrivateStore().getSellList().updateItems();
		itemList = this.player.getInventory().getAvailableItems(player.getPrivateStore().getSellList());
		sellList = this.player.getPrivateStore().getSellList().getItems();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x9a);
		// section 1
		writeD(player.getObjectId());
		writeD(player.getPrivateStore().getSellList().isPackaged() ? 1 : 0); // Package sell
		writeD(player.getInventory().getAdena());
		
		// section2
		writeD(itemList.size()); // for potential sells
		for (TradeItemHolder item : itemList)
		{
			writeD(item.getItem().getType2().ordinal());
			writeD(item.getObjectId());
			writeD(item.getItem().getId());
			writeD(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());// enchant lvl
			writeH(item.getCustomType2());
			writeD(item.getItem().getBodyPart().getMask());
			writeD(item.getPrice()); // store price
		}
		
		// section 3
		writeD(sellList.size()); // count for any items already added for sell
		for (TradeItemHolder item : sellList)
		{
			writeD(item.getItem().getType2().ordinal());
			writeD(item.getObjectId());
			writeD(item.getItem().getId());
			writeD(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());// enchant lvl
			writeH(0x00);
			writeD(item.getItem().getBodyPart().getMask());
			writeD(item.getPrice());// your price
			writeD(item.getItem().getReferencePrice()); // store price
		}
	}
}
