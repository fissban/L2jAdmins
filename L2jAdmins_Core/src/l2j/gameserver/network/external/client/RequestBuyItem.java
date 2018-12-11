package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.12.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestBuyItem extends AClientPacket
{
	public class BuyItemAux
	{
		int itemId;
		int itemCount;
		
		public BuyItemAux(int itemId, int count)
		{
			this.itemId = itemId;
			itemCount = count;
		}
	}
	
	private int count;
	private int listId;
	private List<BuyItemAux> items = null;
	
	@Override
	protected void readImpl()
	{
		listId = readD();
		count = readD();
		
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			return;
		}
		
		items = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			int itemCount = readD();
			
			if ((itemId >= 1) && (itemCount >= 1))
			{
				items.add(new BuyItemAux(itemId, itemCount));
			}
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
		
		if (count < 1)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		//
		L2Object target = player.getTarget();
		L2Npc merchant = target instanceof L2Npc ? (L2Npc) target : null;
		
		// Se contempla el GM SHOP
		if (!player.isGM())
		{
			if (merchant == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!player.isInsideRadius(merchant, L2Npc.INTERACTION_DISTANCE, true, false))
			{
				LOG.info(RequestBuyItem.class.getSimpleName() + ": player " + player.getName() + " cant talk with npc " + target.getName());
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		MerchantTradeList list = null;
		
		if (merchant != null)
		{
			List<MerchantTradeList> lists = TradeControllerData.getInstance().getBuyListByNpcId(merchant.getId());
			
			if (!player.isGM())
			{
				if (lists == null)
				{
					IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
					return;
				}
				
				for (MerchantTradeList tradeList : lists)
				{
					if (tradeList.getListId() == listId)
					{
						list = tradeList;
					}
				}
			}
			else
			{
				list = TradeControllerData.getInstance().getBuyList(listId);
			}
		}
		else
		{
			list = TradeControllerData.getInstance().getBuyList(listId);
		}
		
		if (list == null)
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
			return;
		}
		
		listId = list.getListId();
		
		if (listId > 1000000) // lease
		{
			if ((merchant != null) && (merchant.getTemplate().getId() != (listId - 1000000)))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		double taxRate = 0;
		if ((merchant != null) && merchant.getIsInCastleTown())
		{
			taxRate = merchant.getCastle().getTaxRate();
		}
		
		long subTotal = 0;
		int tax = 0;
		
		// Check for buylist validity and calculates summary values
		long slots = 0;
		long weight = 0;
		
		for (BuyItemAux i : items)
		{
			int itemId = i.itemId;
			int count = i.itemCount;
			int price = -1;
			
			if (!list.containsItemId(itemId))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
				return;
			}
			
			Item template = ItemData.getInstance().getTemplate(itemId);
			if (template == null)
			{
				continue;
			}
			
			if ((count > Integer.MAX_VALUE) || (!template.isStackable() && (count > 1)))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase invalid quantity of items at the same time.");
				SystemMessage sm = new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				sm = null;
				return;
			}
			
			if (listId < 1000000)
			{
				price = list.getPriceForItemId(itemId);
				if ((itemId >= 3960) && (itemId <= 4026))
				{
					price *= Config.RATE_SIEGE_GUARDS_PRICE;
				}
			}
			
			if (price < 0)
			{
				LOG.warning("ERROR, no price found .. wrong buylist ??");
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if ((price == 0) && !player.isGM() && Config.ONLY_GM_ITEMS_FREE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to buy item for 0 adena.");
				return;
			}
			
			subTotal += (long) count * price; // Before tax
			tax = (int) (subTotal * taxRate);
			
			if ((subTotal + tax) > Integer.MAX_VALUE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.");
				return;
			}
			
			weight += (long) count * template.getWeight();
			if (!template.isStackable())
			{
				slots += count;
			}
			else if (player.getInventory().getItemById(itemId) == null)
			{
				slots++;
			}
		}
		
		if ((weight > Integer.MAX_VALUE) || (weight < 0) || !player.getInventory().validateWeight((int) weight))
		{
			sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if ((slots > Integer.MAX_VALUE) || (slots < 0) || !player.getInventory().validateCapacity((int) slots))
		{
			sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan
		if ((subTotal < 0) || !player.getInventory().reduceAdena("Buy", (int) (subTotal + tax), player.getLastTalkNpc(), false))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		if ((merchant != null) && merchant.getIsInCastleTown() && (merchant.getCastle().getOwnerId() > 0))
		{
			merchant.getCastle().addToTreasury(tax);
		}
		
		// Proceed the purchase
		for (BuyItemAux i : items)
		{
			int itemId = i.itemId;
			int count = i.itemCount;
			
			if (count < 0)
			{
				count = 0;
			}
			
			if (!list.containsItemId(itemId))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
				return;
			}
			
			if (list.countDecrease(itemId))
			{
				// Prevent infinite countable item exploit
				if (!list.decreaseCount(itemId, count))
				{
					player.sendMessage("Incorrect product count.");
					return;
				}
			}
			
			// Add item to Inventory and adjust update packet
			player.getInventory().addItem("Buy", itemId, count, player, merchant);
		}
		
		if (merchant != null)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(merchant.getObjectId());
			if (html.setFile("data/html/merchant/" + merchant.getId() + "-bought.htm"))
			{
				html.replace("%objectId%", merchant.getObjectId());
				player.sendPacket(html);
			}
		}
		
		// Update current load as well
		player.updateCurLoad();
		player.sendPacket(new ItemList(player, true));
	}
}
