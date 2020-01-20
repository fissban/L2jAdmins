package main.util;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;
import main.holders.RewardHolder;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class UtilInventory
{
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param  itemIds a list of item IDs to check for
	 * @return         {@code true} if all items exist in player's inventory, {@code false} otherwise
	 */
	public static boolean hasItems(L2PcInstance player, int... itemIds)
	{
		var inv = player.getInventory();
		for (var itemId : itemIds)
		{
			if (inv.getItemById(itemId) == null)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param  itemIds a list of item IDs to check for
	 * @return         {@code true} if all items exist in player's inventory, {@code false} otherwise
	 */
	public static boolean hasItems(PlayerHolder ph, int... itemIds)
	{
		var inv = ph.getInstance().getInventory();
		for (var itemId : itemIds)
		{
			if (inv.getItemById(itemId) == null)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param  itemId : ID of the item wanted to be count
	 * @return        the quantity of one sort of item hold by the player
	 */
	public static int getItemsCount(PlayerHolder ph, int itemId)
	{
		var count = 0;
		
		for (var item : ph.getInstance().getInventory().getItems())
		{
			if ((item != null) && (item.getId() == itemId))
			{
				count += item.getCount();
			}
		}
		
		return count;
	}
	
	/**
	 * Give items to the player's inventory.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of items to add.
	 */
	public static void giveItems(PlayerHolder ph, RewardHolder... rewards)
	{
		for (var r : rewards)
		{
			giveItems(ph, r.getRewardId(), r.getRewardCount(), 0);
		}
	}
	
	/**
	 * Give items to the player's inventory.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of items to add.
	 */
	public static void giveItems(PlayerHolder ph, int itemId, int itemCount)
	{
		giveItems(ph, itemId, itemCount, 0);
	}
	
	/**
	 * Give items to the player's inventory.
	 * @param itemId       : Identifier of the item.
	 * @param itemCount    : Quantity of items to add.
	 * @param enchantLevel : Enchant level of items to add.
	 */
	public static void giveItems(PlayerHolder ph, int itemId, int itemCount, int enchantLevel)
	{
		// Incorrect amount.
		if (itemCount <= 0)
		{
			return;
		}
		
		var player = ph.getInstance().getActingPlayer();
		// Add items to player's inventory.
		var item = player.getInventory().addItem("Engine", itemId, itemCount, player, player);
		if (item == null)
		{
			return;
		}
		
		// Set enchant level for the item.
		if (enchantLevel > 0)
		{
			item.setEnchantLevel(enchantLevel);
		}
		
		// Send message to the client.
		if (itemId == 57)
		{
			player.sendPacket(new SystemMessage(SystemMessage.EARNED_S1_ADENA).addNumber(itemCount));
		}
		else
		{
			if (itemCount > 1)
			{
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemId).addNumber(itemCount));
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(itemId));
			}
		}
		
		// Send status update packet.
		player.updateCurLoad();
	}
	
	/**
	 * Remove items from the player's inventory.
	 * @param itemId    : Identifier of the item.
	 * @param itemCount : Quantity of items to destroy.
	 */
	public static boolean takeItems(PlayerHolder ph, int itemId, int itemCount)
	{
		// Find item in player's inventory.
		var item = ph.getInstance().getInventory().getItemById(itemId);
		if (item == null)
		{
			return false;
		}
		
		// Tests on count value and set correct value if necessary.
		if ((itemCount < 0) || (itemCount > item.getCount()))
		{
			itemCount = item.getCount();
		}
		
		// Disarm item, if equipped.
		if (item.isEquipped())
		{
			ph.getInstance().getInventory().unEquipItemInBodySlotAndRecord(item.getItem().getBodyPart());
		}
		
		// Destroy the quantity of items wanted.
		var val = ph.getInstance().getInventory().destroyItemByItemId("Engine", itemId, itemCount, ph.getInstance(), true);
		
		// Send the ItemList Server->Client Packet to the player in order to refresh its Inventory
		ph.getInstance().sendPacket(new ItemList(ph.getInstance().getInventory().getItems(), false));
		
		ph.getInstance().broadcastUserInfo();
		return val;
	}
}
