package l2j.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.model.world.L2World;
import l2j.util.Rnd;

/**
 * @author Advi
 */
public abstract class ItemContainer
{
	protected static final Logger LOG = Logger.getLogger(ItemContainer.class.getName());
	
	protected List<ItemInstance> items;
	
	protected ItemContainer()
	{
		items = new CopyOnWriteArrayList<>();
	}
	
	public abstract L2Character getOwner();
	
	protected abstract ItemLocationType getBaseLocation();
	
	/**
	 * Returns the ownerID of the inventory
	 * @return int
	 */
	public int getOwnerId()
	{
		return getOwner() == null ? 0 : getOwner().getObjectId();
	}
	
	/**
	 * Returns the quantity of items in the inventory
	 * @return int
	 */
	public int getSize()
	{
		return items.size();
	}
	
	/**
	 * Returns the list of items in inventory
	 * @return ItemInstance : items in inventory
	 */
	public List<ItemInstance> getItems()
	{
		return items;
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param  itemIds a list of item Ids to check.
	 * @return         true if at least one items exists in player's inventory, false otherwise
	 */
	public boolean hasAtLeastOneItem(int... itemIds)
	{
		for (int itemId : itemIds)
		{
			if (getItemById(itemId) != null)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the item from inventory by using its <B>itemId</B><BR>
	 * @param  itemId : int designating the ID of the item
	 * @return        ItemInstance designating the item or null if not found in inventory
	 */
	public ItemInstance getItemById(int itemId)
	{
		return items.stream().filter(item -> item.getId() == itemId).findFirst().orElse(null);
	}
	
	/**
	 * Returns item from inventory by using its <B>objectId</B>
	 * @param  objectId : int designating the ID of the object
	 * @return          ItemInstance designating the item or null if not found in inventory
	 */
	public ItemInstance getItemByObjectId(int objectId)
	{
		return items.stream().filter(item -> item.getObjectId() == objectId).findFirst().orElse(null);
	}
	
	/**
	 * @param  itemId
	 * @param  enchantLevel
	 * @return
	 */
	public int getItemCount(int itemId, int enchantLevel)
	{
		int count = 0;
		
		for (final ItemInstance item : items)
		{
			if ((item.getId() == itemId) && ((item.getEnchantLevel() == enchantLevel) || (enchantLevel < 0)))
			{
				if (item.isStackable())
				{
					count = item.getCount();
				}
				else
				{
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Adds item to inventory
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be added
	 * @param  actor     : L2PcInstance Player requesting the item add
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance addItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		final ItemInstance oldItem = getItemById(item.getId());
		
		// If stackable item is found in inventory just add to current quantity
		if ((oldItem != null) && oldItem.isStackable())
		{
			oldItem.changeCount(process, item.getCount(), actor, reference);
			oldItem.setLastChange(ChangeType.MODIFIED);
			
			// And destroys the item
			ItemData.getInstance().destroyItem(process, item, actor, reference);
			item.updateDatabase();
			item = oldItem;
			
			// Updates database
			if ((item.getId() == Inventory.ADENA_ID) && (item.getCount() < (10000 * Config.DROP_CHANCE_ADENA)))
			{
				// Small adena changes won't be saved to database all the time
				if (Rnd.get(5) == 0)
				{
					item.updateDatabase();
				}
			}
			else
			{
				item.updateDatabase();
			}
		}
		// If item hasn't be found in inventory, create new one
		else
		{
			item.setOwnerId(process, getOwnerId(), actor, reference);
			item.setLocation(getBaseLocation());
			item.setLastChange((ChangeType.ADDED));
			
			// Add item in inventory
			addItem(item);
			
			// Updates database
			item.updateDatabase();
		}
		
		if (item.getItem().getWeight() > 0)
		{
			refreshWeight();
		}
		
		return item;
	}
	
	/**
	 * Adds item to inventory
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item Identifier of the item to be added
	 * @param  count     : int Quantity of items to be added
	 * @param  actor     : L2PcInstance Player requesting the item add
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance addItem(String process, int itemId, int count, L2PcInstance actor, L2Object reference)
	{
		ItemInstance item = getItemById(itemId);
		
		// If stackable item is found in inventory just add to current quantity
		if ((item != null) && item.isStackable())
		{
			item.changeCount(process, count, actor, reference);
			item.setLastChange(ChangeType.MODIFIED);
			// Updates database
			if ((itemId == Inventory.ADENA_ID) && (count < (10000 * Config.DROP_CHANCE_ADENA)))
			{
				// Small adena changes won't be saved to database all the time
				if (Rnd.get(5) == 0)
				{
					item.updateDatabase();
				}
			}
			else
			{
				item.updateDatabase();
			}
		}
		// If item hasn't be found in inventory, create new one
		else
		{
			final Item template = ItemData.getInstance().getTemplate(itemId);
			if (template == null)
			{
				LOG.log(Level.WARNING, (actor != null ? "[" + actor.getName() + "] " : "") + "Invalid ItemId requested: ", itemId);
				return null;
			}
			
			for (int i = 0; i < count; i++)
			{
				item = ItemData.getInstance().createItem(process, itemId, template.isStackable() ? count : 1, actor, reference);
				item.setOwnerId(getOwnerId());
				item.setLocation(getBaseLocation());
				item.setLastChange(ChangeType.ADDED);
				
				// Add item in inventory
				addItem(item);
				// Updates database
				item.updateDatabase();
				
				// If stackable, end loop as entire count is included in 1 instance of item
				if (template.isStackable() || !Config.MULTIPLE_ITEM_DROP)
				{
					break;
				}
			}
		}
		
		if (item != null && item.getItem().getWeight() > 0)
		{
			refreshWeight();
		}
		
		getOwner().getActingPlayer().getInventory().sendUpdateItem(item);
		return item;
	}
	
	/**
	 * Adds Wear/Try On item to inventory<BR>
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item Identifier of the item to be added
	 * @param  actor     : L2PcInstance Player requesting the item add
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new weared item
	 */
	public ItemInstance addWearItem(String process, int itemId, L2PcInstance actor, L2Object reference)
	{
		// Surch the item in the inventory of the player
		ItemInstance item = getItemById(itemId);
		
		// There is such item already in inventory
		if (item != null)
		{
			return item;
		}
		
		// Create and Init the ItemInstance corresponding to the Item Identifier and quantity
		// Add the ItemInstance object to allObjects of L2world
		item = ItemData.getInstance().createItem(process, itemId, 1, actor, reference);
		
		// Set Item Properties
		item.setWear(true); // "Try On" Item -> Don't save it in database
		item.setOwnerId(getOwnerId());
		item.setLocation(getBaseLocation());
		item.setLastChange((ChangeType.ADDED));
		
		// Add item in inventory and equip it if necessary (item location defined)
		addItem(item);
		
		// Calculate the weight loaded by player
		if (item.getItem().getWeight() > 0)
		{
			refreshWeight();
		}
		
		return item;
	}
	
	/**
	 * Transfers item to another inventory
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Identifier of the item to be transfered
	 * @param  count     : int Quantity of items to be transfered
	 * @param  target
	 * @param  actor     : L2PcInstance Player requesting the item transfer
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance transferItem(String process, int objectId, int count, ItemContainer target, L2PcInstance actor, L2Object reference)
	{
		if (target == null)
		{
			return null;
		}
		
		final ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		ItemInstance targetItem = item.isStackable() ? target.getItemById(item.getId()) : null;
		
		synchronized (item)
		{
			if (getItemByObjectId(objectId) != item)
			{
				return null;
			}
			
			// Check if requested quantity is available
			if (count > item.getCount())
			{
				count = item.getCount();
			}
			
			// If possible, move entire item object
			if ((item.getCount() == count) && (targetItem == null))
			{
				removeItem(item);
				target.addItem(process, item, actor, reference);
				targetItem = item;
			}
			else
			{
				if (item.getCount() > count)
				{
					item.changeCount(process, -count, actor, reference);
				}
				else
				// Otherwise destroy old item
				{
					removeItem(item);
					ItemData.getInstance().destroyItem(process, item, actor, reference);
				}
				
				if (targetItem != null)
				{
					targetItem.changeCount(process, count, actor, reference);
				}
				else
				{
					// Otherwise add new item
					targetItem = target.addItem(process, item.getId(), count, actor, reference);
				}
			}
			
			// Updates database
			item.updateDatabase(true);
			if ((targetItem != item) && (targetItem != null))
			{
				targetItem.updateDatabase();
			}
		}
		
		// Refresh inventory weight for actor
		if (item.getItem().getWeight() > 0)
		{
			refreshWeight();
		}
		
		return targetItem;
	}
	
	/**
	 * Destroy item from inventory and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		return destroyItem(process, item, item.getCount(), actor, reference);
	}
	
	/**
	 * Destroy item from inventory and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be destroyed
	 * @param  count
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, ItemInstance item, int count, L2PcInstance actor, L2Object reference)
	{
		synchronized (item)
		{
			if (item.getCount() < count)
			{
				return null;
			}
			
			// Adjust item quantity
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(ChangeType.MODIFIED);
				
				// Don't update often for untraced items
				if ((process != null) || (Rnd.get(10) == 0))
				{
					item.updateDatabase();
				}
				
				if (item.getItem().getWeight() > 0)
				{
					refreshWeight();
				}
				
				return item;
			}
			
			final boolean removed = removeItem(item);
			if (!removed)
			{
				return null;
			}
			
			ItemData.getInstance().destroyItem(process, item, actor, reference);
			
			item.updateDatabase();
			
			if (item.getItem().getWeight() > 0)
			{
				refreshWeight();
			}
		}
		
		return item;
	}
	
	/**
	 * Destroy item from inventory by using its <B>objectID</B> and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Instance identifier of the item to be destroyed
	 * @param  count     : int Quantity of items to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		final ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item identifier of the item to be destroyed
	 * @param  count     : int Quantity of items to be destroyed
	 * @param  actor     : L2PcInstance Player requesting the item destroy
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance destroyItemByItemId(String process, int itemId, int count, L2PcInstance actor, L2Object reference)
	{
		final ItemInstance item = getItemById(itemId);
		if (item == null)
		{
			return null;
		}
		
		return destroyItem(process, item, count, actor, reference);
	}
	
	/**
	 * Destroy all items from inventory and updates database
	 * @param process   : String Identifier of process triggering this action
	 * @param actor     : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public synchronized void destroyAllItems(String process, L2PcInstance actor, L2Object reference)
	{
		items.forEach(item -> destroyItem(process, item, actor, reference));
	}
	
	/**
	 * Get warehouse adena
	 * @return
	 */
	public int getAdena()
	{
		for (final ItemInstance item : items)
		{
			if (item.getId() == Inventory.ADENA_ID)
			{
				return item.getCount();
			}
		}
		
		return 0;
	}
	
	/**
	 * Adds item to inventory for further adjustments.
	 * @param item : ItemInstance to be added from inventory
	 */
	protected void addItem(ItemInstance item)
	{
		items.add(item);
	}
	
	/**
	 * Removes item from inventory for further adjustments.
	 * @param  item : ItemInstance to be removed from inventory
	 * @return
	 */
	protected boolean removeItem(ItemInstance item)
	{
		return items.remove(item);
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	protected void refreshWeight()
	{
		//
	}
	
	/**
	 * Delete item object from world
	 */
	public void deleteMe()
	{
		if (getOwner() != null)
		{
			items.forEach(item ->
			{
				item.updateDatabase();
				L2World.getInstance().removeObject(item);
			});
		}
		items.clear();
	}
	
	/**
	 * Update database with items in inventory
	 */
	public void updateDatabase()
	{
		if (getOwner() != null)
		{
			items.forEach(item ->
			{
				item.updateDatabase(true);
				L2World.getInstance().removeObject(item);
			});
		}
	}
	
	/**
	 * Get back items in container from database
	 */
	public void restore()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT owner_id, object_id, item_id, count, enchant_level, loc, loc_data, freightLocation, price_sell, price_buy, time_of_use, custom_type1, custom_type2 FROM character_items WHERE owner_id=? AND (loc=?)"))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					ItemInstance item = ItemInstance.restoreFromDb(rset);
					if (item == null)
					{
						continue;
					}
					
					L2World.getInstance().addObject(item);
					
					// If stackable item is found in inventory just add to current quantity
					if (item.isStackable() && (getItemById(item.getId()) != null))
					{
						addItem("Restore", item, null, getOwner());
					}
					else
					{
						addItem(item);
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.log(Level.WARNING, "could not restore container:", e);
			e.printStackTrace();
		}
		
		refreshWeight();
	}
	
	public boolean validateCapacity(int slots)
	{
		return true;
	}
	
	public boolean validateWeight(int weight)
	{
		return true;
	}
}
