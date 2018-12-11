package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends AServerPacket
{
	private final L2PcInstance storePlayer;
	private final int playerAdena;
	private final List<TradeItemHolder> items;
	
	public PrivateStoreListBuy(L2PcInstance player, L2PcInstance storePlayer)
	{
		this.storePlayer = storePlayer;
		playerAdena = player.getInventory().getAdena();
		storePlayer.getPrivateStore().getSellList().updateItems(); // Update SellList for case inventory content has changed
		items = storePlayer.getPrivateStore().getBuyList().getAvailableItems(player.getInventory());
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb8);
		writeD(storePlayer.getObjectId());
		writeD(playerAdena);
		
		writeD(items.size());
		
		for (TradeItemHolder item : items)
		{
			writeD(item.getObjectId());
			writeD(item.getItem().getId());
			writeH(item.getEnchant());
			writeD(item.getCount()); // give max possible sell amount
			writeD(item.getItem().getReferencePrice());
			writeH(0);
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getItem().getType2().ordinal());
			writeD(item.getPrice());// buyers price
			writeD(item.getCount()); // maximum possible trade count
		}
	}
}
