package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2MerchantInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSellItem extends AClientPacket
{
	public class SellItemAux
	{
		int objectId;
		int count;
		
		public SellItemAux(int objectId, int count)
		{
			this.objectId = objectId;
			this.count = count;
		}
	}
	
	private int listId;
	private List<SellItemAux> sellItems;
	
	@Override
	protected void readImpl()
	{
		listId = readD();
		int count = readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			count = 0;
			return;
		}
		
		sellItems = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			readD();// itemId
			int cnt = readD();
			if (cnt <= 0)
			{
				continue;
			}
			sellItems.add(new SellItemAux(objectId, cnt));
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
		
		// the target of the character is obtained
		L2Object target = player.getTarget();
		if ((target == null) || !(target.isMerchant()) || !player.isInsideRadius(target, L2Npc.INTERACTION_DISTANCE, false, false))
		{
			return;
		}
		
		// It is expected that only an NPC of type L2 Merchant and with a list of items for sale can sell
		L2MerchantInstance merchant = (L2MerchantInstance) target;
		if (listId > 1000000) // lease
		{
			if (merchant.getId() != (listId - 1000000))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		// total price obtained from the sale of items
		long totalPrice = 0;
		// proceed the sell
		for (SellItemAux sell : sellItems)
		{
			ItemInstance item = player.getInventory().checkItemManipulation(sell.objectId, sell.count, "sell");
			if ((item == null) || (!item.getItem().isSellable()))
			{
				continue;
			}
			
			totalPrice += (item.getReferencePrice() * sell.count) / 2;
			// destroy item to sell
			player.getInventory().destroyItem("Sell", sell.objectId, sell.count, player, null);
		}
		
		// the greatest amount of adena that can be obtained is restricted by Integer.MAX_VALUE
		totalPrice = Math.min(totalPrice, Integer.MAX_VALUE);
		
		// add adena por player
		player.getInventory().addAdena("Sell", (int) totalPrice, merchant, false);
		
		// send html message
		NpcHtmlMessage soldMsg = new NpcHtmlMessage(merchant.getObjectId());
		if (soldMsg.setFile("data/html/merchant/" + merchant.getId() + "-sold.htm"))
		{
			soldMsg.replace("%objectId%", merchant.getObjectId());
			player.sendPacket(soldMsg);
		}
		
		// update item list
		player.sendPacket(new ItemList(player, true));
	}
}
