package l2j.gameserver.model.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemRequestHolder;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Advi
 */
public class CharacterTradeList
{
	private static final Logger LOG = Logger.getLogger(CharacterTradeList.class.getName());
	
	private final L2PcInstance owner;
	private L2PcInstance partner;
	private final List<TradeItemHolder> items;
	private String title;
	private boolean packaged;
	
	private boolean confirmed = false;
	private boolean locked = false;
	
	public CharacterTradeList(L2PcInstance owner)
	{
		items = new CopyOnWriteArrayList<>();
		this.owner = owner;
	}
	
	public L2PcInstance getOwner()
	{
		return owner;
	}
	
	public void setPartner(L2PcInstance partner)
	{
		this.partner = partner;
	}
	
	public L2PcInstance getPartner()
	{
		return partner;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public boolean isConfirmed()
	{
		return confirmed;
	}
	
	public boolean isPackaged()
	{
		return packaged;
	}
	
	public void setPackaged(boolean value)
	{
		packaged = value;
	}
	
	/**
	 * Retrieves items from TradeList
	 * @return
	 */
	public List<TradeItemHolder> getItems()
	{
		return items;
	}
	
	/**
	 * Returns the list of items in inventory available for transaction
	 * @param  inventory
	 * @return           L2ItemInstance : items in inventory
	 */
	public List<TradeItemHolder> getAvailableItems(PcInventory inventory)
	{
		List<TradeItemHolder> list = new ArrayList<>();
		for (TradeItemHolder item : items)
		{
			item = new TradeItemHolder(item, item.getCount(), item.getPrice());
			inventory.adjustAvailableItem(item);
			list.add(item);
		}
		
		return list;
	}
	
	/**
	 * Returns Item List size
	 * @return
	 */
	public int getItemCount()
	{
		return items.size();
	}
	
	/**
	 * Adjust available item from Inventory by the one in this list
	 * @param  item : L2ItemInstance to be adjusted
	 * @return      TradeItem representing adjusted item
	 */
	public TradeItemHolder adjustAvailableItem(ItemInstance item)
	{
		if (item.isStackable())
		{
			for (TradeItemHolder exclItem : items)
			{
				if (exclItem.getItem().getId() == item.getId())
				{
					if (item.getCount() <= exclItem.getCount())
					{
						return null;
					}
					return new TradeItemHolder(item, item.getCount() - exclItem.getCount(), item.getReferencePrice());
				}
			}
		}
		return new TradeItemHolder(item, item.getCount(), item.getReferencePrice());
	}
	
	/**
	 * Adjust ItemRequest by corresponding item in this list using its <b>ObjectId</b>
	 * @param item : ItemRequest to be adjusted
	 */
	public void adjustItemRequest(ItemRequestHolder item)
	{
		for (TradeItemHolder filtItem : items)
		{
			if (filtItem.getObjectId() == item.getObjectId())
			{
				if (filtItem.getCount() < item.getCount())
				{
					item.setCount(filtItem.getCount());
				}
				return;
			}
		}
		item.setCount(0);
	}
	
	/**
	 * Adjust ItemRequest by corresponding item in this list using its <b>ItemId</b>
	 * @param item : ItemRequest to be adjusted
	 */
	public void adjustItemRequestByItemId(ItemRequestHolder item)
	{
		for (TradeItemHolder filtItem : items)
		{
			if (filtItem.getItem().getId() == item.getItemId())
			{
				if (filtItem.getCount() < item.getCount())
				{
					item.setCount(filtItem.getCount());
				}
				return;
			}
		}
		item.setCount(0);
	}
	
	/**
	 * Add simplified item to TradeList
	 * @param  objectId : int
	 * @param  count    : int
	 * @return
	 */
	public synchronized TradeItemHolder addItem(int objectId, int count)
	{
		return addItem(objectId, count, 0);
	}
	
	/**
	 * Add item to TradeList
	 * @param  objectId : int
	 * @param  count    : int
	 * @param  price    : int
	 * @return
	 */
	public synchronized TradeItemHolder addItem(int objectId, int count, int price)
	{
		if (isLocked())
		{
			LOG.warning(owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}
		
		L2Object object = L2World.getInstance().getObject(objectId);
		if ((object == null) || !(object instanceof ItemInstance))
		{
			LOG.warning(owner.getName() + ": Attempt to add invalid item to TradeList!");
			return null;
		}
		
		ItemInstance item = (ItemInstance) object;
		
		if (!item.isTradeable())
		{
			return null;
		}
		
		if (item.getType() == EtcItemType.QUEST)
		{
			return null;
		}
		
		if (count <= 0)
		{
			return null;
		}
		
		if (count > item.getCount())
		{
			return null;
		}
		
		if (!item.isStackable() && (count > 1))
		{
			LOG.warning(owner.getName() + ": Attempt to add non-stackable item to TradeList with count > 1!");
			return null;
		}
		
		for (TradeItemHolder checkitem : items)
		{
			if (checkitem.getObjectId() == objectId)
			{
				return null;
			}
		}
		
		TradeItemHolder titem = new TradeItemHolder(item, count, price);
		items.add(titem);
		
		// If Player has already confirmed this trade, invalidate the confirmation
		invalidateConfirmation();
		return titem;
	}
	
	/**
	 * Add item to TradeList
	 * @param  itemId : int
	 * @param  count  : int
	 * @param  price  : int
	 * @return
	 */
	public synchronized TradeItemHolder addItemByItemId(int itemId, int count, int price)
	{
		if (isLocked())
		{
			LOG.warning(owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}
		
		Item item = ItemData.getInstance().getTemplate(itemId);
		if (item == null)
		{
			LOG.warning(owner.getName() + ": Attempt to add invalid item to TradeList!");
			return null;
		}
		
		if (!item.isTradeable())
		{
			return null;
		}
		
		if (item.getType() == EtcItemType.QUEST)
		{
			return null;
		}
		
		if (!item.isStackable() && (count > 1))
		{
			LOG.warning(owner.getName() + ": Attempt to add non-stackable item to TradeList with count > 1!");
			return null;
		}
		
		TradeItemHolder titem = new TradeItemHolder(item, count, price);
		items.add(titem);
		
		// If Player has already confirmed this trade, invalidate the confirmation
		invalidateConfirmation();
		return titem;
	}
	
	/**
	 * Remove item from TradeList
	 * @param  objectId : int
	 * @param  itemId   : int
	 * @param  count    : int
	 * @return
	 */
	public synchronized TradeItemHolder removeItem(int objectId, int itemId, int count)
	{
		if (isLocked())
		{
			LOG.warning(owner.getName() + ": Attempt to modify locked TradeList!");
			return null;
		}
		
		for (TradeItemHolder titem : items)
		{
			if ((titem.getObjectId() == objectId) || (titem.getItem().getId() == itemId))
			{
				// If Partner has already confirmed this trade, invalidate the confirmation
				if (partner != null)
				{
					CharacterTradeList partnerList = partner.getActiveTradeList();
					if (partnerList == null)
					{
						LOG.warning(partner.getName() + ": Trading partner (" + partner.getName() + ") is invalid in this trade!");
						return null;
					}
					partnerList.invalidateConfirmation();
				}
				
				// Reduce item count or complete item
				if ((count != -1) && (titem.getCount() > count))
				{
					titem.setCount(titem.getCount() - count);
				}
				else
				{
					items.remove(titem);
				}
				
				return titem;
			}
		}
		return null;
	}
	
	/**
	 * Update items in TradeList according their quantity in owner inventory
	 */
	public synchronized void updateItems()
	{
		for (TradeItemHolder titem : items)
		{
			ItemInstance item = owner.getInventory().getItemByObjectId(titem.getObjectId());
			if ((item == null) || (titem.getCount() < 1))
			{
				removeItem(titem.getObjectId(), -1, -1);
			}
			else if (item.getCount() < titem.getCount())
			{
				titem.setCount(item.getCount());
			}
		}
	}
	
	/**
	 * Lockes TradeList, no further changes are allowed
	 */
	public void lock()
	{
		locked = true;
	}
	
	/**
	 * Clears item list
	 */
	public void clear()
	{
		items.clear();
		locked = false;
	}
	
	/**
	 * Confirms TradeList
	 * @return : boolean
	 */
	public boolean confirm()
	{
		if (confirmed)
		{
			return true; // Already confirmed
		}
		
		// If Partner has already confirmed this trade, proceed exchange
		if (partner != null)
		{
			CharacterTradeList partnerList = partner.getActiveTradeList();
			if (partnerList == null)
			{
				LOG.warning(partner.getName() + ": Trading partner (" + partner.getName() + ") is invalid in this trade!");
				return false;
			}
			
			// Synchronization order to avoid deadlock
			CharacterTradeList sync1, sync2;
			if (getOwner().getObjectId() > partnerList.getOwner().getObjectId())
			{
				sync1 = partnerList;
				sync2 = this;
			}
			else
			{
				sync1 = this;
				sync2 = partnerList;
			}
			
			synchronized (sync1)
			{
				synchronized (sync2)
				{
					confirmed = true;
					if (partnerList.isConfirmed())
					{
						partnerList.lock();
						lock();
						if (!partnerList.validate())
						{
							return false;
						}
						if (!validate())
						{
							return false;
						}
						
						doExchange(partnerList);
					}
					else
					{
						partner.sendPacket(new SystemMessage(SystemMessage.C1_CONFIRMED_TRADE).addString(owner.getName()));
					}
				}
			}
		}
		else
		{
			confirmed = true;
		}
		
		return confirmed;
	}
	
	/**
	 * Cancels TradeList confirmation
	 */
	public void invalidateConfirmation()
	{
		confirmed = false;
	}
	
	/**
	 * Validates TradeList with owner inventory
	 * @return
	 */
	private boolean validate()
	{
		// Check for Owner validity
		if ((owner == null) || (L2World.getInstance().getObject(owner.getObjectId()) == null))
		{
			LOG.warning("Invalid owner of TradeList");
			return false;
		}
		
		// Check for Item validity
		for (TradeItemHolder titem : items)
		{
			ItemInstance item = owner.getInventory().checkItemManipulation(titem.getObjectId(), titem.getCount(), "transfer");
			if ((item == null) || (titem.getCount() < 1))
			{
				LOG.warning(owner.getName() + ": Invalid Item in TradeList");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Transfers all TradeItems from inventory to partner
	 * @param  partner
	 * @param  ownerIU
	 * @param  partnerIU
	 * @return
	 */
	private boolean transferItems(L2PcInstance partner, InventoryUpdate ownerIU, InventoryUpdate partnerIU)
	{
		for (TradeItemHolder titem : items)
		{
			ItemInstance oldItem = owner.getInventory().getItemByObjectId(titem.getObjectId());
			if (oldItem == null)
			{
				return false;
			}
			ItemInstance newItem = owner.getInventory().transferItem("Trade", titem.getObjectId(), titem.getCount(), partner.getInventory(), owner, partner);
			if (newItem == null)
			{
				return false;
			}
			
			// Add changes to inventory update packets
			if (ownerIU != null)
			{
				if ((oldItem.getCount() > 0) && (oldItem != newItem))
				{
					ownerIU.addModifiedItem(oldItem);
				}
				else
				{
					ownerIU.addRemovedItem(oldItem);
				}
			}
			
			if (partnerIU != null)
			{
				if (newItem.getCount() > titem.getCount())
				{
					partnerIU.addModifiedItem(newItem);
				}
				else
				{
					partnerIU.addNewItem(newItem);
				}
			}
		}
		return true;
	}
	
	/**
	 * Count items slots
	 * @param  partner
	 * @return
	 */
	public int countItemsSlots(L2PcInstance partner)
	{
		int slots = 0;
		
		for (TradeItemHolder item : items)
		{
			if (item == null)
			{
				continue;
			}
			Item template = ItemData.getInstance().getTemplate(item.getItem().getId());
			if (template == null)
			{
				continue;
			}
			if (!template.isStackable())
			{
				slots += item.getCount();
			}
			else if (partner.getInventory().getItemById(item.getItem().getId()) == null)
			{
				slots++;
			}
		}
		
		return slots;
	}
	
	/**
	 * Calc weight of items in tradeList
	 * @return
	 */
	public int calcItemsWeight()
	{
		int weight = 0;
		
		for (TradeItemHolder item : items)
		{
			if (item == null)
			{
				continue;
			}
			Item template = ItemData.getInstance().getTemplate(item.getItem().getId());
			if (template == null)
			{
				continue;
			}
			weight += item.getCount() * template.getWeight();
		}
		
		return weight;
	}
	
	/**
	 * Proceeds with trade
	 * @param partnerList
	 */
	private void doExchange(CharacterTradeList partnerList)
	{
		boolean success = false;
		// check weight and slots
		if ((!getOwner().getInventory().validateWeight(partnerList.calcItemsWeight())) || !(partnerList.getOwner().getInventory().validateWeight(calcItemsWeight())))
		{
			partnerList.getOwner().sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			getOwner().sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
		}
		else if ((!getOwner().getInventory().validateCapacity(partnerList.countItemsSlots(getOwner()))) || (!partnerList.getOwner().getInventory().validateCapacity(countItemsSlots(partnerList.getOwner()))))
		{
			partnerList.getOwner().sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			getOwner().sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
		}
		else
		{
			// Prepare inventory update packet
			InventoryUpdate ownerIU = new InventoryUpdate();
			InventoryUpdate partnerIU = new InventoryUpdate();
			
			// Transfer items
			partnerList.transferItems(getOwner(), partnerIU, ownerIU);
			transferItems(partnerList.getOwner(), ownerIU, partnerIU);
			
			// Send inventory update packet
			owner.sendPacket(ownerIU);
			partner.sendPacket(partnerIU);
			
			// Update current load as well
			owner.updateCurLoad();
			partner.updateCurLoad();
			
			success = true;
		}
		// Finish the trade
		partnerList.getOwner().onTradeFinish(success);
		getOwner().onTradeFinish(success);
	}
	
	/**
	 * Buy items from this PrivateStore list
	 * @param  player
	 * @param  items
	 * @param  price
	 * @return        : boolean true if success
	 */
	public synchronized boolean privateStoreBuy(L2PcInstance player, List<ItemRequestHolder> items, int price)
	{
		if (locked)
		{
			return false;
		}
		if (!validate())
		{
			lock();
			return false;
		}
		
		int slots = 0;
		int weight = 0;
		
		for (ItemRequestHolder item : items)
		{
			if (item == null)
			{
				continue;
			}
			Item template = ItemData.getInstance().getTemplate(item.getItemId());
			if (template == null)
			{
				continue;
			}
			weight += item.getCount() * template.getWeight();
			if (!template.isStackable())
			{
				slots += item.getCount();
			}
			else if (player.getInventory().getItemById(item.getItemId()) == null)
			{
				slots++;
			}
		}
		
		if (!player.getInventory().validateWeight(weight))
		{
			player.sendPacket(SystemMessage.WEIGHT_LIMIT_EXCEEDED);
			return false;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessage.SLOTS_FULL);
			return false;
		}
		
		PcInventory ownerInventory = owner.getInventory();
		PcInventory playerInventory = player.getInventory();
		
		// Transfer adena
		if (price > playerInventory.getAdena())
		{
			lock();
			return false;
		}
		
		// Prepare inventory update packets
		InventoryUpdate ownerIU = new InventoryUpdate();
		InventoryUpdate playerIU = new InventoryUpdate();
		
		playerInventory.reduceAdena("PrivateStore", price, player, owner);
		playerIU.addItem(playerInventory.getAdenaInstance());
		
		ownerInventory.addAdena("PrivateStore", price, owner, player);
		ownerIU.addItem(ownerInventory.getAdenaInstance());
		
		// Transfer items
		for (ItemRequestHolder item : items)
		{
			// Check if requested item is sill on the list and adjust its count
			adjustItemRequest(item);
			if (item.getCount() == 0)
			{
				continue;
			}
			
			// Check if requested item is available for manipulation
			ItemInstance oldItem = owner.getInventory().checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
			if (oldItem == null)
			{
				lock();
				return false;
			}
			
			// Proceed with item transfer
			ItemInstance newItem = ownerInventory.transferItem("PrivateStore", item.getObjectId(), item.getCount(), playerInventory, owner, player);
			if (newItem == null)
			{
				return false;
			}
			removeItem(item.getObjectId(), -1, item.getCount());
			
			// Add changes to inventory update packets
			if ((oldItem.getCount() > 0) && (oldItem != newItem))
			{
				ownerIU.addModifiedItem(oldItem);
			}
			else
			{
				ownerIU.addRemovedItem(oldItem);
			}
			if (newItem.getCount() > item.getCount())
			{
				playerIU.addModifiedItem(newItem);
			}
			else
			{
				playerIU.addNewItem(newItem);
			}
			
			// Send messages about the transaction to both players
			if (newItem.isStackable())
			{
				owner.sendPacket(new SystemMessage(SystemMessage.C1_PURCHASED_S3_S2_S).addString(player.getName()).addItemName(newItem.getId()).addNumber(item.getCount()));
				player.sendPacket(new SystemMessage(SystemMessage.PURCHASED_S3_S2_S_FROM_C1).addString(owner.getName()).addItemName(newItem.getId()).addNumber(item.getCount()));
			}
			else
			{
				owner.sendPacket(new SystemMessage(SystemMessage.C1_PURCHASED_S2).addString(player.getName()).addItemName(newItem.getId()));
				player.sendPacket(new SystemMessage(SystemMessage.PURCHASED_S2_FROM_C1).addString(owner.getName()).addItemName(newItem.getId()));
			}
		}
		
		// Send inventory update packet
		owner.sendPacket(ownerIU);
		player.sendPacket(playerIU);
		return true;
	}
	
	/**
	 * Sell items to this PrivateStore list
	 * @param  player
	 * @param  items
	 * @param  price
	 * @return        : boolean true if success
	 */
	public synchronized boolean privateStoreSell(L2PcInstance player, List<ItemRequestHolder> items, int price)
	{
		if (locked)
		{
			return false;
		}
		
		PcInventory ownerInventory = owner.getInventory();
		PcInventory playerInventory = player.getInventory();
		
		// we must check item are available before begining transaction, TODO: should we remove that check when transfering items as it's done here? (there might be synchro problems if player clicks fast if we remove it)
		for (ItemRequestHolder item : items)
		{
			// Check if requested item is available for manipulation
			ItemInstance oldItem = player.getInventory().checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
			if (oldItem == null)
			{
				return false;
			}
			
			if (oldItem.getId() != item.getItemId())
			{
				IllegalAction.report(player, player + " is cheating with sell items");
				return false;
			}
		}
		
		// Prepare inventory update packet
		InventoryUpdate ownerIU = new InventoryUpdate();
		InventoryUpdate playerIU = new InventoryUpdate();
		
		// Transfer items
		for (ItemRequestHolder item : items)
		{
			// Check if requested item is sill on the list and adjust its count
			adjustItemRequestByItemId(item);
			if (item.getCount() == 0)
			{
				continue;
			}
			
			// Check if requested item is available for manipulation
			ItemInstance oldItem = player.getInventory().checkItemManipulation(item.getObjectId(), item.getCount(), "sell");
			if (oldItem == null)
			{
				return false;
			}
			
			// Proceed with item transfer
			ItemInstance newItem = playerInventory.transferItem("PrivateStore", item.getObjectId(), item.getCount(), ownerInventory, player, owner);
			if (newItem == null)
			{
				return false;
			}
			removeItem(-1, item.getItemId(), item.getCount());
			
			// Add changes to inventory update packets
			if ((oldItem.getCount() > 0) && (oldItem != newItem))
			{
				playerIU.addModifiedItem(oldItem);
			}
			else
			{
				playerIU.addRemovedItem(oldItem);
			}
			if (newItem.getCount() > item.getCount())
			{
				ownerIU.addModifiedItem(newItem);
			}
			else
			{
				ownerIU.addNewItem(newItem);
			}
			
			// Send messages about the transaction to both players
			if (newItem.isStackable())
			{
				owner.sendPacket(new SystemMessage(SystemMessage.PURCHASED_S3_S2_S_FROM_C1).addString(player.getName()).addItemName(newItem.getId()).addNumber(item.getCount()));
				player.sendPacket(new SystemMessage(SystemMessage.C1_PURCHASED_S3_S2_S).addString(owner.getName()).addItemName(newItem.getId()).addNumber(item.getCount()));
			}
			else
			{
				owner.sendPacket(new SystemMessage(SystemMessage.PURCHASED_S2_FROM_C1).addString(player.getName()).addItemName(newItem.getId()));
				player.sendPacket(new SystemMessage(SystemMessage.C1_PURCHASED_S2).addString(owner.getName()).addItemName(newItem.getId()));
			}
		}
		
		// Transfer adena
		if (price > ownerInventory.getAdena())
		{
			return false;
		}
		
		ownerInventory.reduceAdena("PrivateStore", price, owner, player);
		ownerIU.addItem(ownerInventory.getAdenaInstance());
		
		playerInventory.addAdena("PrivateStore", price, player, owner);
		playerIU.addItem(playerInventory.getAdenaInstance());
		
		// Send inventory update packet
		owner.sendPacket(ownerIU);
		player.sendPacket(playerIU);
		return true;
	}
	
	/**
	 * @param  objectId
	 * @return
	 */
	public TradeItemHolder getItem(int objectId)
	{
		for (TradeItemHolder item : items)
		{
			if (item.getObjectId() == objectId)
			{
				return item;
			}
		}
		return null;
	}
}
