package l2j.gameserver.network.external.client;

import java.util.List;
import java.util.concurrent.Future;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2MerchantInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.12.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestWearItem extends AClientPacket
{
	protected Future<?> removeWearItemsTask;
	
	@SuppressWarnings("unused")
	private int unknow;
	/** List of ItemID to Wear */
	private int listId;
	/** Number of Item to Wear */
	private int count;
	/** Table of ItemId containing all Item to Wear */
	private int[] items;
	/** Player that request a Try on */
	protected L2PcInstance player;
	
	@Override
	protected void readImpl()
	{
		player = getClient().getActiveChar();
		unknow = readD();
		listId = readD(); // List of ItemID to Wear
		count = readD(); // Number of Item to Wear
		
		if (count < 0)
		{
			count = 0;
		}
		
		if (count > 100)
		{
			count = 0; // prevent too long lists
		}
		
		// Create items table that will contain all ItemID to Wear
		items = new int[count];
		
		// Fill items table with all ItemID to Wear
		for (int i = 0; i < count; i++)
		{
			final int itemId = readD();
			items[i] = itemId;
		}
	}
	
	/**
	 * Launch Wear action.
	 */
	@Override
	public void runImpl()
	{
		// Get the current player and return if null
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getPrivateStore().isInStoreMode())
		{
			player.sendPacket(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isWearingFormalWear())
		{
			player.sendPacket(SystemMessage.CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR);
			return;
		}
		
		if (player.isMounted())
		{
			return;
		}
		
		if (player.isStunned() || player.isSleeping() || player.isAttackingNow() || player.isCastingNow() || player.isParalyzed() || player.isAlikeDead())
		{
			return;
		}
		
		// If Alternate rule Karma punishment is set to true, forbid Wear to player with Karma
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}
		
		// Check current target of the player and the INTERACTION_DISTANCE
		final L2Object target = player.getTarget();
		if (!player.isGM() && ((target == null) || !(target.isMerchant()) || !player.isInsideRadius(target, L2Npc.INTERACTION_DISTANCE, false, false)))
		{
			return;
		}
		
		MerchantTradeList list = null;
		
		// Get the current merchant targeted by the player
		final L2MerchantInstance merchant = ((target != null) && (target instanceof L2MerchantInstance)) ? (L2MerchantInstance) target : null;
		
		if (merchant == null)
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false merchant.");
			return;
		}
		
		final List<MerchantTradeList> lists = TradeControllerData.getInstance().getBuyListByNpcId(merchant.getId());
		
		if (lists == null)
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
			return;
		}
		
		for (final MerchantTradeList tradeList : lists)
		{
			if (tradeList.getListId() == listId)
			{
				list = tradeList;
			}
			
		}
		
		if (list == null)
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
			return;
		}
		
		listId = list.getListId();
		
		// Check if the quantity of Item to Wear
		if ((count < 1) || (listId >= 1000000))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Total Price of the Try On
		long totalPrice = 0;
		
		// Check for buylist validity and calculates summary values
		int slots = 0;
		int weight = 0;
		
		for (int i = 0; i < count; i++)
		{
			final int itemId = items[i];
			
			if (!list.containsItemId(itemId))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
				return;
			}
			
			final Item template = ItemData.getInstance().getTemplate(itemId);
			weight += template.getWeight();
			slots++;
			
			totalPrice += Config.WEAR_PRICE;
			if (totalPrice > Integer.MAX_VALUE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.");
				return;
			}
		}
		
		// Check the weight
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		// Check the inventory capacity
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
		if ((totalPrice < 0) || !player.getInventory().reduceAdena("Wear", (int) totalPrice, player.getLastTalkNpc(), false))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed the wear
		final InventoryUpdate playerIU = new InventoryUpdate();
		for (int i = 0; i < count; i++)
		{
			final int itemId = items[i];
			
			if (!list.containsItemId(itemId))
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id.");
				return;
			}
			
			// If player doesn't own this item : Add this L2ItemInstance to Inventory and set properties lastchanged to ADDED and wear to True
			// If player already own this item : Return its L2ItemInstance (will not be destroy because property wear set to False)
			final ItemInstance item = player.getInventory().addWearItem("Wear", itemId, player, merchant);
			
			// Equip player with this item (set its location)
			player.getInventory().equipItemAndRecord(item);
			
			// Add this Item in the InventoryUpdate Server->Client Packet
			playerIU.addItem(item);
		}
		
		// Send the InventoryUpdate Server->Client Packet to the player
		// Add Items in player inventory and equip them
		player.sendPacket(playerIU);
		
		// Update current load as well
		player.updateCurLoad();
		
		// Send a Server->Client packet UserInfo to this L2PcInstance and CharInfo to all L2PcInstance in its KnownPlayers
		player.broadcastUserInfo();
		
		// All wear items should be removed in ALLOW_WEAR_DELAY sec.
		if (removeWearItemsTask == null)
		{
			removeWearItemsTask = ThreadPoolManager.getInstance().schedule(() -> destroyWearedItems(player, "Wear", null, true), Config.WEAR_DELAY * 1000);
		}
	}
	
	/**
	 * Destroy all weared items from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param player
	 * @param process     : String Identifier of process triggering this action
	 * @param reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void destroyWearedItems(L2PcInstance player, String process, L2Object reference, boolean sendMessage)
	{
		// Go through all Items of the inventory
		for (final ItemInstance item : player.getInventory().getItems())
		{
			if (item == null)
			{
				continue;
			}
			
			// Check if the item is a Try On item in order to remove it
			if (item.isWear())
			{
				if (item.isEquipped())
				{
					player.getInventory().unEquipItemInSlotAndRecord(item.getEquipSlot());
				}
				
				if (player.getInventory().destroyItem(process, item, player, reference) == null)
				{
					LOG.warning("Player " + player.getName() + " can't destroy weared item: " + item.getName() + "[ " + item.getObjectId() + " ]");
					continue;
				}
				
				// Send an Unequipped Message in system window of the player for each Item
				final SystemMessage sm = new SystemMessage(SystemMessage.S1_DISARMED);
				sm.addItemName(item.getId());
				sendPacket(sm);
			}
		}
		
		// Update current load as well
		player.updateCurLoad();
		
		// Send the ItemList Server->Client Packet to the player in order to refresh its Inventory
		sendPacket(new ItemList(player.getInventory().getItems(), true));
		
		// Send a Server->Client packet UserInfo to this L2PcInstance and CharInfo to all L2PcInstance in its KnownPlayers
		player.broadcastUserInfo();
		
		// Sends message to client if requested
		player.sendMessage("Trying-on mode has ended.");
	}
}
