package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2MerchantInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.6 $ $Date: 2005/03/27 15:29:57 $
 */
public class PrivateStoreListSell extends AServerPacket
{
	private L2PcInstance storePlayer;
	private final int playerAdena;
	
	private final boolean packageSale;
	private final List<TradeItemHolder> items;
	
	// player's private shop
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance storePlayer)
	{
		this.storePlayer = storePlayer;
		playerAdena = player.getInventory().getAdena();
		items = storePlayer.getPrivateStore().getSellList().getItems();
		
		packageSale = storePlayer.getPrivateStore().getSellList().isPackaged();
	}
	
	// lease shop
	public PrivateStoreListSell(L2PcInstance player, L2MerchantInstance storeMerchant)
	{
		playerAdena = player.getInventory().getAdena();
		items = storePlayer.getPrivateStore().getSellList().getItems();
		
		packageSale = storePlayer.getPrivateStore().getSellList().isPackaged();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x9b);
		writeD(storePlayer.getObjectId());
		writeD(packageSale ? 1 : 0);
		writeD(playerAdena);
		
		writeD(items.size());
		for (TradeItemHolder item : items)
		{
			writeD(item.getItem().getType2().ordinal());
			writeD(item.getObjectId());
			writeD(item.getItem().getId());
			writeD(item.getCount());
			writeH(0x00);
			writeH(item.getEnchant());
			writeH(item.getCustomType2());
			writeD(item.getItem().getBodyPart().getMask());
			writeD(item.getPrice()); // your price
			writeD(item.getItem().getReferencePrice()); // store price
		}
	}
}
