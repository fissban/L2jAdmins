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
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreBuy extends AClientPacket
{
	private int storePlayerId;
	private int count;
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
		
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			long count = readD();
			if (count > Integer.MAX_VALUE)
			{
				count = Integer.MAX_VALUE;
			}
			int price = readD();
			
			items.add(new ItemRequestHolder(objectId, (int) count, price));
		}
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
		
		if (!((storePlayer.getPrivateStore().getStoreType() == PcStoreType.SELL) || (storePlayer.getPrivateStore().getStoreType() == PcStoreType.PACKAGE_SELL)))
		{
			return;
		}
		
		CharacterTradeList storeList = storePlayer.getPrivateStore().getSellList();
		
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
		
		// FIXME: this check should be (and most probably is) done in the TradeList mechanics
		long priceTotal = 0;
		for (ItemRequestHolder ir : items)
		{
			if ((ir.getCount() > Integer.MAX_VALUE) || (ir.getCount() < 0))
			{
				IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreBuy] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!");
				return;
			}
			
			TradeItemHolder sellersItem = storeList.getItem(ir.getObjectId());
			
			if (sellersItem == null)
			{
				IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreBuy] player " + getClient().getActiveChar().getName() + " tried to buy an item not sold in a private store (buy), ban this player!");
				return;
			}
			
			if (ir.getPrice() != sellersItem.getPrice())
			{
				IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreBuy] player " + getClient().getActiveChar().getName() + " tried to change the seller's price in a private store (buy), ban this player!");
				return;
			}
			priceTotal += ir.getPrice() * ir.getCount();
		}
		
		// FIXME: this check should be (and most probably is) done in the TradeList mechanics
		if ((priceTotal < 0) || (priceTotal > Integer.MAX_VALUE))
		{
			IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreBuy] player " + getClient().getActiveChar().getName() + " tried an overflow exploit, ban this player!");
			return;
		}
		
		if (player.getInventory().getAdena() < priceTotal)
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (storePlayer.getPrivateStore().getStoreType() == PcStoreType.PACKAGE_SELL)
		{
			if (storeList.getItemCount() > count)
			{
				IllegalAction.report(getClient().getActiveChar(), "[RequestPrivateStoreBuy] player " + getClient().getActiveChar().getName() + " tried to buy less items then sold by package-sell, ban this player for bot-usage!");
				return;
			}
		}
		
		if (!storeList.privateStoreBuy(player, items, (int) priceTotal))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			LOG.warning("PrivateStore buy has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
			return;
		}
		
		if (storeList.getItemCount() == 0)
		{
			storePlayer.getPrivateStore().setStoreType(PcStoreType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}
