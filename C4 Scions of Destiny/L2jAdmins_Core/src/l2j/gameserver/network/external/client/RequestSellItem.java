package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2MerchantInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSellItem extends AClientPacket
{
	private int listId;
	private int count;
	private int[] items; // count*3
	
	@Override
	protected void readImpl()
	{
		listId = readD();
		count = readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			count = 0;
			items = null;
			return;
		}
		items = new int[count * 3];
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			items[(i * 3) + 0] = objectId;
			int itemId = readD();
			items[(i * 3) + 1] = itemId;
			long cnt = readD();
			if ((cnt > Integer.MAX_VALUE) || (cnt <= 0))
			{
				count = 0;
				items = null;
				return;
			}
			items[(i * 3) + 2] = (int) cnt;
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
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}
		
		L2Object target = player.getTarget();
		if ((target == null) || !(target.isMerchant()) || !player.isInsideRadius(target, L2Npc.INTERACTION_DISTANCE, false, false))
		{
			return;
		}
		
		L2MerchantInstance merchant = (L2MerchantInstance) target;
		
		if (listId > 1000000) // lease
		{
			if (merchant.getId() != (listId - 1000000))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		long totalPrice = 0;
		// Proceed the sell
		for (int i = 0; i < count; i++)
		{
			int objectId = items[(i * 3) + 0];
			@SuppressWarnings("unused")
			int itemId = items[(i * 3) + 1];
			int count = items[(i * 3) + 2];
			
			if ((count < 0) || (count > Integer.MAX_VALUE))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.");
				sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
				return;
			}
			
			ItemInstance item = player.getInventory().checkItemManipulation(objectId, count, "sell");
			if ((item == null) || (!item.getItem().isSellable()))
			{
				continue;
			}
			
			totalPrice += (item.getReferencePrice() * count) / 2;
			if (totalPrice > Integer.MAX_VALUE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.");
				return;
			}
			
			item = player.getInventory().destroyItem("Sell", objectId, count, player, null);
			
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) int price = item.getReferencePrice()*(int)count/2; L2ItemInstance li = null; L2ItemInstance la = null; if (listId > 1000000) { li = merchant.findLeaseItem(item.getItemId(),item.getEnchantLevel()); la = merchant.getLeaseAdena(); if
			 * (li == null || la == null) continue; price = li.getPriceToBuy()*(int)count; // player sells, thus merchant buys. if (price > la.getCount()) continue; }
			 */
			/*
			 * TODO: Disabled until Leaseholders are rewritten ;-) if (item != null && listId > 1000000) { li.setCount(li.getCount()+(int)count); li.updateDatabase(); la.setCount(la.getCount()-price); la.updateDatabase(); }
			 */
		}
		player.getInventory().addAdena("Sell", (int) totalPrice, merchant, false);
		
		NpcHtmlMessage soldMsg = new NpcHtmlMessage(merchant.getObjectId());
		if (soldMsg.setFile("data/html/merchant/" + merchant.getId() + "-sold.htm"))
		{
			soldMsg.replace("%objectId%", merchant.getObjectId());
			player.sendPacket(soldMsg);
		}
		
		// Update current load as well
		player.updateCurLoad();
		
		player.sendPacket(new ItemList(player, true));
	}
}
