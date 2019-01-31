package l2j.gameserver.model.itemcontainer.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.PetInventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

public class PcInventory extends Inventory
{
	private final L2PcInstance owner;
	private ItemInstance adena;
	private ItemInstance ancientAdena;
	
	public PcInventory(L2PcInstance owner)
	{
		super();
		this.owner = owner;
	}
	
	@Override
	public L2PcInstance getOwner()
	{
		return owner;
	}
	
	@Override
	protected ItemLocationType getBaseLocation()
	{
		return ItemLocationType.INVENTORY;
	}
	
	@Override
	protected ItemLocationType getEquipLocation()
	{
		return ItemLocationType.PAPERDOLL;
	}
	
	public ItemInstance getAdenaInstance()
	{
		return adena;
	}
	
	@Override
	public int getAdena()
	{
		return adena != null ? adena.getCount() : 0;
	}
	
	/**
	 * Add adena to Inventory of the L2PcInstance and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process     : String Identifier of process triggering this action
	 * @param count       : int Quantity of adena to be added
	 * @param reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addAdena(String process, int count, L2Object reference, boolean sendMessage)
	{
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.EARNED_S1_ADENA).addNumber(count));
		}
		
		if (count > 0)
		{
			addItem(process, Inventory.ADENA_ID, count, owner, reference);
			
			// Send update packet
			sendUpdateItem(getAdenaInstance());
		}
	}
	
	/**
	 * Adds adena to PcInventory
	 * @param process   : String Identifier of process triggering this action
	 * @param count     : int Quantity of adena to be added
	 * @param actor     : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void addAdena(String process, int count, L2PcInstance actor, L2Object reference)
	{
		if (count > 0)
		{
			addItem(process, Inventory.ADENA_ID, count, actor, reference);
		}
	}
	
	/**
	 * Reduce adena in Inventory of the L2PcInstance and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  count       : int Quantity of adena to be reduced
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successful
	 */
	public boolean reduceAdena(String process, int count, L2Object reference, boolean sendMessage)
	{
		if (count > getAdena())
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
			}
			
			return false;
		}
		
		if (count > 0)
		{
			var adenaItem = getAdenaInstance();
			
			destroyItemByItemId(process, Inventory.ADENA_ID, count, owner, reference);
			
			// Send update packet
			sendUpdateItem(adenaItem);
			
			if (sendMessage)
			{
				owner.sendPacket(new SystemMessage(SystemMessage.S1_DISAPPEARED_ADENA).addNumber(count));
			}
		}
		
		return true;
	}
	
	public ItemInstance getAncientAdenaInstance()
	{
		return ancientAdena;
	}
	
	public int getAncientAdena()
	{
		return (ancientAdena != null) ? ancientAdena.getCount() : 0;
	}
	
	/**
	 * Add ancient adena to Inventory of the L2PcInstance and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process     : String Identifier of process triggering this action
	 * @param count       : int Quantity of ancient adena to be added
	 * @param reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addAncientAdena(String process, int count, L2Object reference, boolean sendMessage)
	{
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(Inventory.ANCIENT_ADENA_ID).addNumber(count));
		}
		
		if (count > 0)
		{
			addItem(process, Inventory.ANCIENT_ADENA_ID, count, owner, reference);
			
			// Send update packet
			sendUpdateItem(getAncientAdenaInstance());
		}
	}
	
	/**
	 * Reduce ancient adena in Inventory of the L2PcInstance and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  count       : int Quantity of ancient adena to be reduced
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successful
	 */
	public boolean reduceAncientAdena(String process, int count, L2Object reference, boolean sendMessage)
	{
		if (count > getAncientAdena())
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
			}
			
			return false;
		}
		
		if (count > 0)
		{
			var ancientAdenaItem = getAncientAdenaInstance();
			
			destroyItemByItemId(process, Inventory.ANCIENT_ADENA_ID, count, owner, reference);
			
			// Send update packet
			sendUpdateItem(ancientAdenaItem);
			
			if (sendMessage)
			{
				owner.sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addItemName(Inventory.ANCIENT_ADENA_ID).addNumber(count));
			}
		}
		
		return true;
	}
	
	/**
	 * Adds item to inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process     : String Identifier of process triggering this action
	 * @param item        : ItemInstance to be added
	 * @param reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addItem(String process, ItemInstance item, L2Object reference, boolean sendMessage)
	{
		if (item.getCount() > 0)
		{
			// Sends message to client if requested
			if (sendMessage)
			{
				if (item.getCount() > 1)
				{
					owner.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_S1_S2).addItemName(item.getId()).addNumber(item.getCount()));
				}
				else if (item.getEnchantLevel() > 0)
				{
					owner.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_A_S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getId()));
				}
				else
				{
					owner.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_S1).addItemName(item.getId()));
				}
			}
			
			// Add the item to inventory
			var newItem = addItem(process, item, owner, reference);
			
			// Send update packet
			sendUpdateItem(newItem);
			
			// If over capacity, drop the item
			if (!owner.isGM() && !validateCapacity(0) && item.isDropable())
			{
				dropItem("InvDrop", item, null, true, true);
			}
		}
	}
	
	/**
	 * Adds item to Inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param process     : String Identifier of process triggering this action
	 * @param itemId      : int Item Identifier of the item to be added
	 * @param count       : int Quantity of items to be added
	 * @param reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addItem(String process, int itemId, int count, L2Object reference, boolean sendMessage)
	{
		if (count > 0)
		{
			// Sends message to client if requested
			if (sendMessage)
			{
				if (count > 1)
				{
					if (process.equalsIgnoreCase("sweep") || process.equalsIgnoreCase("Quest"))
					{
						owner.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemId).addNumber(count));
					}
					else
					{
						owner.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_S1_S2).addItemName(itemId).addNumber(count));
					}
				}
				else
				{
					if (process.equalsIgnoreCase("sweep") || process.equalsIgnoreCase("Quest"))
					{
						owner.sendPacket(new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(itemId));
					}
					else
					{
						owner.sendPacket(new SystemMessage(SystemMessage.YOU_PICKED_UP_S1).addItemName(itemId));
					}
				}
			}
			
			// Add the item to inventory
			var item = addItem(process, itemId, count, owner, reference);
			
			// Send inventory update packet
			sendUpdateItem(item);
			
			// If over capacity, drop the item
			if (!owner.isGM() && !validateCapacity(0) && item.isDropable())
			{
				dropItem("InvDrop", item, null, true);
			}
		}
	}
	
	/**
	 * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  item        : ItemInstance to be destroyed
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successfull
	 */
	public boolean destroyItem(String process, ItemInstance item, L2Object reference, boolean sendMessage)
	{
		return destroyItem(process, item, item.getCount(), reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  item        : ItemInstance to be destroyed
	 * @param  count
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successful
	 */
	public boolean destroyItem(String process, ItemInstance item, int count, L2Object reference, boolean sendMessage)
	{
		item = destroyItem(process, item, count, owner, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return false;
		}
		
		// Send inventory update packet
		sendUpdateItem(item);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addItemName(item.getId()).addNumber(count));
		}
		
		return true;
	}
	
	/**
	 * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  objectId    : int Item Instance identifier of the item to be destroyed
	 * @param  count       : int Quantity of items to be destroyed
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successfull
	 */
	public boolean destroyItem(String process, int objectId, int count, L2Object reference, boolean sendMessage)
	{
		var item = getItemByObjectId(objectId);
		
		if ((item == null) || (item.getCount() < count))
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return false;
		}
		
		return destroyItem(process, item, count, reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  itemId      : int Item identifier of the item to be destroyed
	 * @param  count       : int Quantity of items to be destroyed
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successfull
	 */
	public boolean destroyItemByItemId(String process, int itemId, int count, L2Object reference, boolean sendMessage)
	{
		if (itemId == Inventory.ADENA_ID)
		{
			return reduceAdena(process, count, reference, sendMessage);
		}
		
		if (itemId == Inventory.ANCIENT_ADENA_ID)
		{
			return reduceAncientAdena(process, count, reference, sendMessage);
		}
		
		var item = getItemById(itemId);
		
		if ((item == null) || (item.getCount() < count) || (destroyItemByItemId(process, itemId, count, owner, reference) == null))
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return false;
		}
		
		// Send inventory update packet
		sendUpdateItem(item);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addItemName(itemId).addNumber(count));
		}
		
		return true;
	}
	
	/**
	 * Transfers item to another ItemContainer and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId
	 * @param  count     : int Quantity of items to be transfered
	 * @param  target
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance transferItem(String process, int objectId, int count, Inventory target, L2Object reference)
	{
		var oldItem = checkItemManipulation(objectId, count, "transfer");
		if (oldItem == null)
		{
			return null;
		}
		
		var newItem = transferItem(process, objectId, count, target, owner, reference);
		if (newItem == null)
		{
			return null;
		}
		// Send inventory update packet
		if (oldItem != newItem)
		{
			sendUpdateItem(oldItem);
		}
		
		// Update current load as well
		owner.updateCurLoad();
		
		// Send target update packet
		if (target instanceof PcInventory)
		{
			// Send inventory update packet
			((PcInventory) target).getOwner().getInventory().sendUpdateItem(newItem);
			
			// Update current load as well
			owner.updateCurLoad();
		}
		else if (target instanceof PetInventory)
		{
			var petIU = new PetInventoryUpdate();
			petIU.addItem(newItem);
			
			((PetInventory) target).getOwner().getOwner().sendPacket(petIU);
			owner.getPet().getInventory().refreshWeight();
			
		}
		
		return newItem;
	}
	
	/**
	 * Drop item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  item        : ItemInstance to be dropped
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @param  protectItem
	 * @return             boolean informing if the action was successful
	 */
	public boolean dropItem(String process, ItemInstance item, L2Object reference, boolean sendMessage, boolean protectItem)
	{
		item = dropItem(process, item, owner, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return false;
		}
		
		if (Config.DESTROY_DROPPED_PLAYER_ITEM)
		{
			if (!item.isEquipable() || (item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM))
			{
				item.setProtected(false);
			}
			else
			{
				item.setProtected(true);
			}
		}
		else
		{
			item.setProtected(true);
		}
		
		item.dropMe(owner, (owner.getX() + Rnd.get(50)) - 25, (owner.getY() + Rnd.get(50)) - 25, owner.getZ() + 20);
		
		// retail drop protection
		if (protectItem)
		{
			item.getDropProtection().protect(owner);
		}
		
		// Send inventory update packet
		sendUpdateItem(item);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.YOU_DROPPED_S1).addItemName(item.getId()));
		}
		
		return true;
	}
	
	public boolean dropItem(String process, ItemInstance item, L2Object reference, boolean sendMessage)
	{
		return dropItem(process, item, reference, sendMessage, false);
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  objectId    : int Item Instance identifier of the item to be dropped
	 * @param  count       : int Quantity of items to be dropped
	 * @param  x           : int coordinate for drop X
	 * @param  y           : int coordinate for drop Y
	 * @param  z           : int coordinate for drop Z
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @param  protectItem
	 * @return             ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, int objectId, int count, int x, int y, int z, L2Object reference, boolean sendMessage, boolean protectItem)
	{
		var invItem = getItemByObjectId(objectId);
		var item = dropItem(process, objectId, count, owner, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				owner.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return null;
		}
		
		item.dropMe(owner, x, y, z);
		
		if (Config.DESTROY_DROPPED_PLAYER_ITEM)
		{
			if (!item.isEquipable() || (item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM))
			{
				item.setProtected(false);
			}
			else
			{
				item.setProtected(true);
			}
		}
		else
		{
			item.setProtected(true);
		}
		
		// retail drop protection
		if (protectItem)
		{
			item.getDropProtection().protect(owner);
		}
		
		// Send inventory update packet
		sendUpdateItem(invItem);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			owner.sendPacket(new SystemMessage(SystemMessage.YOU_DROPPED_S1).addItemName(item.getId()));
		}
		
		return item;
	}
	
	public ItemInstance checkItemManipulation(int objectId, int count, String action)
	{
		if (L2World.getInstance().getObject(objectId) == null)
		{
			return null;
		}
		
		var item = getItemByObjectId(objectId);
		if ((item == null) || (item.getOwnerId() != owner.getObjectId()))
		{
			return null;
		}
		
		if ((count < 0) || ((count > 1) && !item.isStackable()))
		{
			return null;
		}
		
		if (count > item.getCount())
		{
			return null;
		}
		
		// Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
		if (((owner.getPet() != null) && (owner.getPet().getControlItemId() == objectId)) || (owner.getMountObjectId() == objectId))
		{
			return null;
		}
		
		if ((owner.getActiveEnchantItem() != null) && (owner.getActiveEnchantItem().getObjectId() == objectId))
		{
			return null;
		}
		
		if (item.isWear())
		{
			return null;
		}
		
		return item;
	}
	
	/**
	 * Returns the list of items in inventory available for transaction
	 * @param  allowAdena
	 * @param  allowAncientAdena
	 * @return                   ItemInstance : items in inventory
	 */
	public List<ItemInstance> getUniqueItems(boolean allowAdena, boolean allowAncientAdena)
	{
		var list = new ArrayList<ItemInstance>();
		for (ItemInstance item : items)
		{
			if ((!allowAdena && (item.getId() == Inventory.ADENA_ID)))
			{
				continue;
			}
			if ((!allowAncientAdena && (item.getId() == Inventory.ANCIENT_ADENA_ID)))
			{
				continue;
			}
			
			var isDuplicate = false;
			for (var litem : list)
			{
				if (litem.getId() == item.getId())
				{
					isDuplicate = true;
					break;
				}
			}
			if (!isDuplicate && item.getItem().isSellable() && item.isAvailable(getOwner(), false))
			{
				list.add(item);
			}
		}
		
		return list;
	}
	
	/**
	 * Returns the list of items in inventory available for transaction Allows an item to appear twice if and only if there is a difference in enchantment level.
	 * @param  allowAdena
	 * @param  allowAncientAdena
	 * @return                   ItemInstance : items in inventory
	 */
	public List<ItemInstance> getUniqueItemsByEnchantLevel(boolean allowAdena, boolean allowAncientAdena)
	{
		var list = new ArrayList<ItemInstance>();
		for (var item : items)
		{
			if ((!allowAdena && (item.getId() == Inventory.ADENA_ID)))
			{
				continue;
			}
			if ((!allowAncientAdena && (item.getId() == Inventory.ANCIENT_ADENA_ID)))
			{
				continue;
			}
			
			boolean isDuplicate = false;
			for (var litem : list)
			{
				if ((litem.getId() == item.getId()) && (litem.getEnchantLevel() == item.getEnchantLevel()))
				{
					isDuplicate = true;
					break;
				}
			}
			
			if (!isDuplicate && item.getItem().isSellable() && item.isAvailable(getOwner(), false))
			{
				list.add(item);
			}
		}
		
		return list;
	}
	
	/**
	 * Returns the list of all items in inventory that have a given item id.
	 * @param  itemId
	 * @return        List<ItemInstance> : matching items from inventory
	 */
	public List<ItemInstance> getAllItemsByItemId(int itemId)
	{
		return items.stream().filter(item -> item.getId() == itemId).collect(Collectors.toList());
	}
	
	/**
	 * Returns the list of all items in inventory that have a given item id AND a given enchantment level.
	 * @param  itemId
	 * @param  enchantment
	 * @return             List<ItemInstance> : matching items from inventory
	 */
	public List<ItemInstance> getAllItemsByItemId(int itemId, int enchantment)
	{
		return items.stream().filter(item -> (item.getId() == itemId) && (item.getEnchantLevel() == enchantment)).collect(Collectors.toList());
	}
	
	/**
	 * Returns the list of items in inventory available for transaction
	 * @param  allowAdena
	 * @return            ItemInstance : items in inventory
	 */
	public List<ItemInstance> getAvailableItems(boolean allowAdena)
	{
		return items.stream().filter(item -> item.isAvailable(getOwner(), allowAdena)).collect(Collectors.toList());
	}
	
	/**
	 * Returns the list of items in inventory available for transaction adjusted by tradeList
	 * @param  tradeList
	 * @return           ItemInstance : items in inventory
	 */
	public List<TradeItemHolder> getAvailableItems(CharacterTradeList tradeList)
	{
		var list = new ArrayList<TradeItemHolder>();
		for (var item : items)
		{
			if (item.isAvailable(getOwner(), false))
			{
				var adjItem = tradeList.adjustAvailableItem(item);
				if (adjItem != null)
				{
					list.add(adjItem);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Adjust TradeItem according his status in inventory
	 * @param item : ItemInstance to be adjusted
	 */
	public void adjustAvailableItem(TradeItemHolder item)
	{
		for (var adjItem : items)
		{
			if (adjItem.getId() == item.getItem().getId())
			{
				item.setObjectId(adjItem.getObjectId());
				item.setEnchant(adjItem.getEnchantLevel());
				
				if (adjItem.getCount() < item.getCount())
				{
					item.setCount(adjItem.getCount());
				}
				
				return;
			}
		}
		
		item.setCount(0);
	}
	
	/**
	 * Removes adena to PcInventory
	 * @param process   : String Identifier of process triggering this action
	 * @param count     : int Quantity of adena to be removed
	 * @param actor     : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void reduceAdena(String process, int count, L2PcInstance actor, L2Object reference)
	{
		if (count > 0)
		{
			destroyItemByItemId(process, Inventory.ADENA_ID, count, actor, reference);
		}
	}
	
	/**
	 * Adds specified amount of ancient adena to player inventory.
	 * @param process   : String Identifier of process triggering this action
	 * @param count     : int Quantity of adena to be added
	 * @param actor     : L2PcInstance Player requesting the item add
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void addAncientAdena(String process, int count, L2PcInstance actor, L2Object reference)
	{
		if (count > 0)
		{
			addItem(process, Inventory.ANCIENT_ADENA_ID, count, actor, reference);
		}
	}
	
	/**
	 * Adds item in inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be added
	 * @param  actor     : L2PcInstance Player requesting the item add
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	@Override
	public ItemInstance addItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		item = super.addItem(process, item, actor, reference);
		
		if ((item != null) && (item.getId() == Inventory.ADENA_ID) && !item.equals(adena))
		{
			adena = item;
		}
		
		if ((item != null) && (item.getId() == Inventory.ANCIENT_ADENA_ID) && !item.equals(ancientAdena))
		{
			ancientAdena = item;
		}
		
		return item;
	}
	
	/**
	 * Adds item in inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item Identifier of the item to be added
	 * @param  count     : int Quantity of items to be added
	 * @param  actor     : L2PcInstance Player requesting the item creation
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	@Override
	public ItemInstance addItem(String process, int itemId, int count, L2PcInstance actor, L2Object reference)
	{
		var item = super.addItem(process, itemId, count, actor, reference);
		
		if ((item != null) && (item.getId() == Inventory.ADENA_ID) && !item.equals(adena))
		{
			adena = item;
		}
		
		if ((item != null) && (item.getId() == Inventory.ANCIENT_ADENA_ID) && !item.equals(ancientAdena))
		{
			ancientAdena = item;
		}
		
		return item;
	}
	
	/**
	 * Transfers item to another inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Identifier of the item to be transfered
	 * @param  count     : int Quantity of items to be transfered
	 * @param  actor     : L2PcInstance Player requesting the item transfer
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	@Override
	public ItemInstance transferItem(String process, int objectId, int count, ItemContainer target, L2PcInstance actor, L2Object reference)
	{
		var item = super.transferItem(process, objectId, count, target, actor, reference);
		
		if ((adena != null) && ((adena.getCount() <= 0) || (adena.getOwnerId() != getOwnerId())))
		{
			adena = null;
		}
		
		if ((ancientAdena != null) && ((ancientAdena.getCount() <= 0) || (ancientAdena.getOwnerId() != getOwnerId())))
		{
			ancientAdena = null;
		}
		
		return item;
	}
	
	/**
	 * Destroy item from inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance destroyItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		return destroyItem(process, item, item.getCount(), actor, reference);
	}
	
	/**
	 * Destroy item from inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance destroyItem(String process, ItemInstance item, int count, L2PcInstance actor, L2Object reference)
	{
		item = super.destroyItem(process, item, count, actor, reference);
		
		if ((adena != null) && (adena.getCount() <= 0))
		{
			adena = null;
		}
		
		if ((ancientAdena != null) && (ancientAdena.getCount() <= 0))
		{
			ancientAdena = null;
		}
		
		if (item != null)
		{
			sendUpdateItem(item);
		}
		
		return item;
	}
	
	/**
	 * Destroys item from inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Instance identifier of the item to be destroyed
	 * @param  count     : int Quantity of items to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance destroyItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		var item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item identifier of the item to be destroyed
	 * @param  count     : int Quantity of items to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance destroyItemByItemId(String process, int itemId, int count, L2PcInstance actor, L2Object reference)
	{
		var item = getItemById(itemId);
		if (item == null)
		{
			return null;
		}
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Drop item from inventory and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be dropped
	 * @param  actor     : L2PcInstance Player requesting the item drop
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance dropItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		item = super.dropItem(process, item, actor, reference);
		
		if ((adena != null) && ((adena.getCount() <= 0) || (adena.getOwnerId() != getOwnerId())))
		{
			adena = null;
		}
		
		if ((ancientAdena != null) && ((ancientAdena.getCount() <= 0) || (ancientAdena.getOwnerId() != getOwnerId())))
		{
			ancientAdena = null;
		}
		
		return item;
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and checks adena and ancientAdena
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Instance identifier of the item to be dropped
	 * @param  count     : int Quantity of items to be dropped
	 * @param  actor     : L2PcInstance Player requesting the item drop
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	@Override
	public ItemInstance dropItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		var item = super.dropItem(process, objectId, count, actor, reference);
		
		if ((adena != null) && ((adena.getCount() <= 0) || (adena.getOwnerId() != getOwnerId())))
		{
			adena = null;
		}
		
		if ((ancientAdena != null) && ((ancientAdena.getCount() <= 0) || (ancientAdena.getOwnerId() != getOwnerId())))
		{
			ancientAdena = null;
		}
		
		return item;
	}
	
	/**
	 * <b>Overloaded</b>, when removes item from inventory, remove also owner shortcuts.
	 * @param item : ItemInstance to be removed from inventory
	 */
	@Override
	protected boolean removeItem(ItemInstance item)
	{
		// Removes any reference to the item from Shortcut bar
		owner.getShortCuts().removeItemFromShortCut(item.getObjectId());
		
		// Removes active Enchant Scroll
		if (item.equals(owner.getActiveEnchantItem()))
		{
			owner.setActiveEnchantItem(null);
		}
		
		if (item.getId() == Inventory.ADENA_ID)
		{
			adena = null;
		}
		else if (item.getId() == Inventory.ANCIENT_ADENA_ID)
		{
			ancientAdena = null;
		}
		
		return super.removeItem(item);
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	public void refreshWeight()
	{
		super.refreshWeight();
		owner.refreshOverloaded();
	}
	
	/**
	 * Get back items in inventory from database
	 */
	@Override
	public void restore()
	{
		super.restore();
		adena = getItemById(Inventory.ADENA_ID);
		ancientAdena = getItemById(Inventory.ANCIENT_ADENA_ID);
	}
	
	public static int[][] restoreVisibleInventory(int objectId)
	{
		var paperdoll = new int[16][3];
		
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var statement2 = con.prepareStatement("SELECT object_id,item_id,loc_data,enchant_level FROM character_items WHERE owner_id=? AND loc='PAPERDOLL'"))
		{
			statement2.setInt(1, objectId);
			try (var invdata = statement2.executeQuery())
			{
				while (invdata.next())
				{
					int slot = invdata.getInt("loc_data");
					paperdoll[slot][0] = invdata.getInt("object_id");
					paperdoll[slot][1] = invdata.getInt("item_id");
					paperdoll[slot][2] = invdata.getInt("enchant_level");
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not restore inventory:", e.getMessage());
			e.printStackTrace();
		}
		return paperdoll;
	}
	
	public boolean validateCapacity(ItemInstance item)
	{
		var slots = 0;
		
		if (!(item.isStackable() && (getItemById(item.getId()) != null)))
		{
			slots++;
		}
		
		return validateCapacity(slots);
	}
	
	public boolean validateCapacity(List<ItemInstance> items)
	{
		var slots = 0;
		
		for (var item : items)
		{
			if (!(item.isStackable() && (getItemById(item.getId()) != null)))
			{
				slots++;
			}
		}
		
		return validateCapacity(slots);
	}
	
	public boolean validateCapacityByItemId(int ItemId)
	{
		var slots = 0;
		
		var invItem = getItemById(ItemId);
		if (!((invItem != null) && invItem.isStackable()))
		{
			slots++;
		}
		
		return validateCapacity(slots);
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return ((items.size() + slots) <= owner.getInventoryLimit());
	}
	
	@Override
	public boolean validateWeight(int weight)
	{
		return ((totalWeight + weight) <= owner.getMaxLoad());
	}
	
	/**
	 * Actions:<br>
	 * <li>sendPacket <b>InventoryUpdate</b></li>
	 * <li>Update current load status on player</li>
	 * @param item
	 */
	public void sendUpdateItem(ItemInstance item)
	{
		var iu = new InventoryUpdate();
		iu.addItems(item);
		
		// Send Packet InventoryUpdate
		owner.sendPacket(iu);
		// Update CurLoad
		owner.updateCurLoad();
	}
}
