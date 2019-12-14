package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.model.holder.ItemRequestHolder;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreSell extends AClientPacket
{
	private int storePlayerId;
	private int count;
	private int price;
	private List<ItemRequestHolder> items;
	
	@Override
	protected void readImpl()
	{
		storePlayerId = readD();
		count = readD();
		if ((count < 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			count = 0;
		}
		items = new ArrayList<>(count);
		
		long priceTotal = 0;
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			int itemId = readD();
			readH(); // TODO analyze this
			readH(); // TODO analyze this
			long count = readD();
			int price = readD();
			
			if ((count > Integer.MAX_VALUE) || (count < 0))
			{
				IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreSell] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!");
				count = 0;
				items = null;
				return;
			}
			items.add(new ItemRequestHolder(objectId, itemId, (int) count, price));
			priceTotal += price * count;
		}
		
		if ((priceTotal < 0) || (priceTotal > Integer.MAX_VALUE))
		{
			IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreSell] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!");
			count = 0;
			items = null;
			return;
		}
		
		price = (int) priceTotal;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		L2Object object = L2World.getInstance().getObject(storePlayerId);
		if ((object == null) || !(object instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance storePlayer = (L2PcInstance) object;
		
		if (!player.isInsideRadius(storePlayer, L2Npc.INTERACTION_DISTANCE, true, false))
		{
			return;
		}
		
		if (storePlayer.getPrivateStore().getStoreType() != PcStoreType.BUY)
		{
			return;
		}
		
		CharacterTradeList storeList = storePlayer.getPrivateStore().getBuyList();
		if (storeList == null)
		{
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && (player.isGM()))
		{
			player.sendMessage("Transactions are disable for your Access Level");
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (storePlayer.getInventory().getAdena() < price)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			storePlayer.sendMessage("You have not enough adena, canceling PrivateBuy.");
			storePlayer.getPrivateStore().setStoreType(PcStoreType.NONE);
			storePlayer.broadcastUserInfo();
			return;
		}
		
		if (!storeList.privateStoreSell(player, items, price))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			LOG.warning("PrivateStore sell has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
			return;
		}
		
		if (storeList.getItemCount() == 0)
		{
			storePlayer.getPrivateStore().setStoreType(PcStoreType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}
