package l2j.gameserver.model.actor.manager.character.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import l2j.DatabaseManager;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.IPaperdollListener;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.ListenerArmorSet;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.ListenerBow;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.ListenerFormalWear;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.ListenerPassiveSkills;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener.ListenerStats;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemEtcItem;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.InventoryUpdate;
import main.EngineModsManager;

/**
 * This class manages inventory
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer
{
	public static final int ADENA_ID = 57;
	public static final int ANCIENT_ADENA_ID = 5575;
	
	private static final int PAPERDOLL_TOTALSLOTS = 16;
	
	private final ItemInstance[] paperdoll;
	private final List<IPaperdollListener> paperdollListeners;
	
	// protected to be accessed from child classes only
	protected int totalWeight;
	
	// used to quickly check for using of items of special type
	private int wearedMask;
	
	private static final class ChangeRecorder implements IPaperdollListener
	{
		private final Inventory inventory;
		private final List<ItemInstance> changed;
		
		/**
		 * Constructor of the ChangeRecorder
		 * @param inventory
		 */
		public ChangeRecorder(Inventory inventory)
		{
			this.inventory = inventory;
			changed = new ArrayList<>();
			this.inventory.addPaperdollListener(this);
		}
		
		/**
		 * Add alteration in inventory when item equipped
		 */
		@Override
		public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable player)
		{
			if (!changed.contains(item))
			{
				changed.add(item);
			}
		}
		
		/**
		 * Add alteration in inventory when item unequipped
		 */
		@Override
		public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable player)
		{
			if (!changed.contains(item))
			{
				changed.add(item);
			}
		}
		
		/**
		 * Returns alterations in inventory
		 * @return List ItemInstance : array of alterated items
		 */
		public List<ItemInstance> getChangedItems()
		{
			return changed;
		}
	}
	
	/**
	 * Constructor of the inventory
	 */
	public Inventory()
	{
		paperdoll = new ItemInstance[PAPERDOLL_TOTALSLOTS];
		paperdollListeners = new ArrayList<>();
		
		if (this instanceof PcInventory)
		{
			addPaperdollListener(new ListenerArmorSet());
			addPaperdollListener(new ListenerBow());
			addPaperdollListener(new ListenerFormalWear());
			addPaperdollListener(new ListenerPassiveSkills());
		}
		
		// common
		addPaperdollListener(new ListenerStats());
	}
	
	protected abstract ItemLocationType getEquipLocation();
	
	/**
	 * Returns the instance of new ChangeRecorder
	 * @return ChangeRecorder
	 */
	public ChangeRecorder newRecorder()
	{
		return new ChangeRecorder(this);
	}
	
	/**
	 * Drop item from inventory and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  item      : ItemInstance to be dropped
	 * @param  actor     : L2PcInstance Player requesting the item drop
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!items.contains(item))
			{
				return null;
			}
			
			removeItem(item);
			item.setOwnerId(process, 0, actor, reference);
			item.setLocation(ItemLocationType.VOID);
			item.setLastChange(ChangeType.REMOVED);
			
			item.updateDatabase();
			refreshWeight();
		}
		return item;
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and updates database
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId  : int Item Instance identifier of the item to be dropped
	 * @param  count     : int Quantity of items to be dropped
	 * @param  actor     : L2PcInstance Player requesting the item drop
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, int objectId, int count, L2PcInstance actor, L2Object reference)
	{
		ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!items.contains(item))
			{
				return null;
			}
			
			// Adjust item quantity and create new instance to drop
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(ChangeType.MODIFIED);
				item.updateDatabase();
				
				item = ItemData.getInstance().createItem(process, item.getId(), count, actor, reference);
				
				item.updateDatabase();
				refreshWeight();
				return item;
			}
		}
		
		// Directly drop entire item
		return dropItem(process, item, actor, reference);
	}
	
	/**
	 * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)
	 * @param item : ItemInstance to be added from inventory
	 */
	@Override
	protected void addItem(ItemInstance item)
	{
		super.addItem(item);
		if (item.isEquipped())
		{
			equipItem(item);
		}
	}
	
	/**
	 * Removes item from inventory for further adjustments.
	 * @param item : ItemInstance to be removed from inventory
	 */
	@Override
	protected boolean removeItem(ItemInstance item)
	{
		// Unequip item if equipped
		for (int i = 0; i < paperdoll.length; i++)
		{
			if (paperdoll[i] == item)
			{
				unEquipItemInSlot(ParpedollType.values()[i]);
			}
		}
		
		return super.removeItem(item);
	}
	
	/**
	 * Returns the item in the paperdoll slot
	 * @param  slot
	 * @return      ItemInstance
	 */
	public ItemInstance getPaperdollItem(ParpedollType slot)
	{
		return paperdoll[slot.ordinal()];
	}
	
	/**
	 * Returns the item in the paperdoll Item slot
	 * @param  slot identifier
	 * @return      ItemInstance
	 */
	public ItemInstance getPaperdollItemByL2ItemId(int slot)
	{
		switch (slot)
		{
			case 0x01:
				return paperdoll[0];
			case 0x04:
				return paperdoll[1];
			case 0x02:
				return paperdoll[2];
			case 0x08:
				return paperdoll[3];
			case 0x20:
				return paperdoll[4];
			case 0x10:
				return paperdoll[5];
			case 0x40:
				return paperdoll[6];
			case 0x80:
				return paperdoll[7];
			case 0x0100:
				return paperdoll[8];
			case 0x0200:
				return paperdoll[9];
			case 0x0400:
				return paperdoll[10];
			case 0x0800:
				return paperdoll[11];
			case 0x1000:
				return paperdoll[12];
			case 0x2000:
				return paperdoll[13];
			case 0x4000:
				return paperdoll[14];
			case 0x040000:
				return paperdoll[15];
		}
		return null;
	}
	
	/**
	 * Returns the ID of the item in the paperdol slot
	 * @param  slot : int designating the slot
	 * @return      int designating the ID of the item
	 */
	public int getPaperdollItemId(ParpedollType slot)
	{
		final ItemInstance item = paperdoll[slot.ordinal()];
		if (item != null)
		{
			return item.getId();
		}
		return 0;
	}
	
	/**
	 * Returns the objectID associated to the item in the paperdoll slot
	 * @param  slot : int pointing out the slot
	 * @return      int designating the objectID
	 */
	public int getPaperdollObjectId(ParpedollType slot)
	{
		final ItemInstance item = paperdoll[slot.ordinal()];
		if (item != null)
		{
			return item.getObjectId();
		}
		return 0;
	}
	
	/**
	 * Adds new inventory's paperdoll listener
	 * @param listener
	 */
	public synchronized void addPaperdollListener(IPaperdollListener listener)
	{
		assert !paperdollListeners.contains(listener);
		
		paperdollListeners.add(listener);
	}
	
	/**
	 * Removes a paperdoll listener
	 * @param listener
	 */
	public synchronized void removePaperdollListener(IPaperdollListener listener)
	{
		paperdollListeners.remove(listener);
	}
	
	/**
	 * Equips an item in the given slot of the paperdoll. <U><I>Remark :</I></U> The item <B>HAS TO BE</B> already in the inventory
	 * @param  slot : int pointing out the slot of the paperdoll
	 * @param  item : ItemInstance pointing out the item to add in slot
	 * @return      ItemInstance designating the item placed in the slot before
	 */
	public synchronized ItemInstance setPaperdollItem(ParpedollType slot, ItemInstance item)
	{
		final ItemInstance old = paperdoll[slot.ordinal()];
		if (old != item)
		{
			if (old != null)
			{
				paperdoll[slot.ordinal()] = null;
				// Put old item from paperdoll slot to base location
				old.setLocation(getBaseLocation());
				old.setLastChange(ChangeType.MODIFIED);
				// Get the mask for paperdoll
				int mask = 0;
				for (final ItemInstance pi : paperdoll)
				{
					if (pi != null)
					{
						mask |= pi.getItem().getMask();
					}
				}
				wearedMask = mask;
				// Notify all paperdoll listener in order to unequip old item in slot
				for (final IPaperdollListener listener : paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					listener.notifyUnequiped(slot, old, (L2Playable) getOwner());
				}
				
				EngineModsManager.onUnequip(getOwner());
				
				old.updateDatabase();
			}
			// Add new item in slot of paperdoll
			if (item != null)
			{
				paperdoll[slot.ordinal()] = item;
				item.setLocation(getEquipLocation(), slot);
				item.setLastChange(ChangeType.MODIFIED);
				wearedMask |= item.getItem().getMask();
				for (final IPaperdollListener listener : paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					
					listener.notifyEquiped(slot, item, (L2Playable) getOwner());
				}
				
				EngineModsManager.onEquip(getOwner());
				
				item.updateDatabase();
			}
		}
		return old;
	}
	
	/**
	 * Return the mask of weared item
	 * @return int
	 */
	public int getWearedMask()
	{
		return wearedMask;
	}
	
	/**
	 * Unequips item in body slot and returns alterations.<br>
	 * Send packet InventoryUpdate
	 * @param  slot : int designating the slot of the paperdoll
	 * @return      ItemInstance : list of changes
	 */
	public List<ItemInstance> unEquipItemInBodySlotAndRecord(SlotType slot)
	{
		final ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInBodySlot(slot);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		
		getOwner().sendPacket(new InventoryUpdate(recorder.getChangedItems()));
		
		return recorder.getChangedItems();
	}
	
	/**
	 * Sets item in slot of the paperdoll to null value
	 * @param  pdollSlot : int designating the slot
	 * @return           ItemInstance designating the item in slot before change
	 */
	public ItemInstance unEquipItemInSlot(ParpedollType pdollSlot)
	{
		return setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Unepquips item in slot and returns alterations
	 * @param  slot : int designating the slot
	 * @return      List ItemInstance : list of items altered
	 */
	public List<ItemInstance> unEquipItemInSlotAndRecord(ParpedollType slot)
	{
		final ChangeRecorder recorder = newRecorder();
		try
		{
			unEquipItemInSlot(slot);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequips item in slot (i.e. equips with default value)
	 * @param slot : int designating the slot
	 */
	private void unEquipItemInBodySlot(SlotType slot)
	{
		ParpedollType pdollSlot = null;
		
		switch (slot)
		{
			case L_EAR:
				pdollSlot = ParpedollType.LEAR;
				break;
			case R_EAR:
				pdollSlot = ParpedollType.REAR;
				break;
			case NECK:
				pdollSlot = ParpedollType.NECK;
				break;
			case R_FINGER:
				pdollSlot = ParpedollType.RFINGER;
				break;
			case L_FINGER:
				pdollSlot = ParpedollType.LFINGER;
				break;
			case HAIR:
				pdollSlot = ParpedollType.HAIR;
				break;
			case HEAD:
				pdollSlot = ParpedollType.HEAD;
				break;
			case R_HAND:
				pdollSlot = ParpedollType.RHAND;
				break;
			case L_HAND:
				pdollSlot = ParpedollType.LHAND;
				break;
			case GLOVES:
				pdollSlot = ParpedollType.GLOVES;
				break;
			case CHEST: // fall through
			case FULL_ARMOR:
				pdollSlot = ParpedollType.CHEST;
				break;
			case LEGS:
				pdollSlot = ParpedollType.LEGS;
				break;
			case BACK:
				pdollSlot = ParpedollType.BACK;
				break;
			case FEET:
				pdollSlot = ParpedollType.FEET;
				break;
			case UNDERWEAR:
				pdollSlot = ParpedollType.UNDER;
				break;
			case LR_HAND:
				setPaperdollItem(ParpedollType.LHAND, null);
				setPaperdollItem(ParpedollType.RHAND, null);// this should be the same as in LRHAND
				pdollSlot = ParpedollType.LRHAND;
				break;
		}
		if (pdollSlot != null)
		{
			final ItemInstance old = setPaperdollItem(pdollSlot, null);
			if (old != null)
			{
				if (getOwner() instanceof L2PcInstance)
				{
					((L2PcInstance) getOwner()).refreshExpertisePenalty();
				}
			}
		}
	}
	
	public SlotType getSlotFromItem(ItemInstance item)
	{
		ParpedollType location = item.getEquipSlot();
		
		switch (location)
		{
			case UNDER:
				return SlotType.UNDERWEAR;
			case LEAR:
				return SlotType.L_EAR;
			case REAR:
				return SlotType.R_EAR;
			case NECK:
				return SlotType.NECK;
			case RFINGER:
				return SlotType.R_FINGER;
			case LFINGER:
				return SlotType.L_FINGER;
			case HAIR:
				return SlotType.HAIR;
			case HEAD:
				return SlotType.HEAD;
			case RHAND:
				return SlotType.R_HAND;
			case LHAND:
				return SlotType.L_HAND;
			case GLOVES:
				return SlotType.GLOVES;
			case CHEST:
				return item.getItem().getBodyPart();
			case LEGS:
				return SlotType.LEGS;
			case BACK:
				return SlotType.BACK;
			case FEET:
				return SlotType.FEET;
			case LRHAND:
				return SlotType.LR_HAND;
		}
		
		return SlotType.UNDERWEAR;
	}
	
	/**
	 * Equips item and returns list of alterations
	 * @param  item : ItemInstance corresponding to the item
	 * @return      ItemInstance[] : list of alterations
	 */
	public List<ItemInstance> equipItemAndRecord(ItemInstance item)
	{
		final ChangeRecorder recorder = newRecorder();
		
		try
		{
			equipItem(item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		
		getOwner().sendPacket(new InventoryUpdate(recorder.getChangedItems()));
		
		return recorder.getChangedItems();
	}
	
	/**
	 * Equips item in slot of paperdoll.
	 * @param item : ItemInstance designating the item and slot used.
	 */
	public synchronized void equipItem(ItemInstance item)
	{
		if (getOwner() instanceof L2PcInstance)
		{
			final L2PcInstance player = (L2PcInstance) getOwner();
			
			if (player.getPrivateStore().isInStoreMode())
			{
				return;
			}
			
			if (!player.isGM() && !player.isHero())
			{
				final int itemId = item.getId();
				// Heroe items
				if (((itemId >= 6611) && (itemId <= 6621)) || (itemId == 6842))
				{
					return;
				}
			}
		}
		
		final SlotType targetSlot = item.getItem().getBodyPart();
		
		switch (targetSlot)
		{
			case LR_HAND:
			{
				if (setPaperdollItem(ParpedollType.LHAND, null) != null)
				{
					// exchange 2h for 2h
					setPaperdollItem(ParpedollType.RHAND, null);
					setPaperdollItem(ParpedollType.LHAND, null);
				}
				else
				{
					setPaperdollItem(ParpedollType.RHAND, null);
				}
				
				setPaperdollItem(ParpedollType.RHAND, item);
				setPaperdollItem(ParpedollType.LRHAND, item);
				break;
			}
			case L_HAND:
			{
				if (!(item.getItem() instanceof ItemEtcItem) || (item.getItem().getType() != EtcItemType.ARROW))
				{
					final ItemInstance old1 = setPaperdollItem(ParpedollType.LRHAND, null);
					
					if (old1 != null)
					{
						setPaperdollItem(ParpedollType.RHAND, null);
					}
				}
				
				setPaperdollItem(ParpedollType.LHAND, null);
				setPaperdollItem(ParpedollType.LHAND, item);
				break;
			}
			case R_HAND:
			{
				if (getPaperdollItem(ParpedollType.LRHAND) != null)
				{
					setPaperdollItem(ParpedollType.LRHAND, null);
					setPaperdollItem(ParpedollType.LHAND, null);
					setPaperdollItem(ParpedollType.RHAND, null);
				}
				else
				{
					setPaperdollItem(ParpedollType.RHAND, null);
				}
				
				setPaperdollItem(ParpedollType.RHAND, item);
				break;
			}
			case L_EAR:
			case R_EAR:
			case R_EAR_L_EAR:
			{
				if (getPaperdollItem(ParpedollType.LEAR) == null)
				{
					setPaperdollItem(ParpedollType.LEAR, item);
				}
				else if (getPaperdollItem(ParpedollType.REAR) == null)
				{
					setPaperdollItem(ParpedollType.REAR, item);
				}
				else
				{
					setPaperdollItem(ParpedollType.LEAR, null);
					setPaperdollItem(ParpedollType.LEAR, item);
				}
				
				break;
			}
			case L_FINGER:
			case R_FINGER:
			case R_FINGER_L_FINGER:
			{
				if (getPaperdollItem(ParpedollType.LFINGER) == null)
				{
					setPaperdollItem(ParpedollType.LFINGER, item);
				}
				else if (getPaperdollItem(ParpedollType.RFINGER) == null)
				{
					setPaperdollItem(ParpedollType.RFINGER, item);
				}
				else
				{
					setPaperdollItem(ParpedollType.LFINGER, null);
					setPaperdollItem(ParpedollType.LFINGER, item);
				}
				
				break;
			}
			case NECK:
				setPaperdollItem(ParpedollType.NECK, item);
				break;
			case FULL_ARMOR:
				setPaperdollItem(ParpedollType.CHEST, null);
				setPaperdollItem(ParpedollType.LEGS, null);
				setPaperdollItem(ParpedollType.CHEST, item);
				break;
			case CHEST:
				setPaperdollItem(ParpedollType.CHEST, item);
				break;
			case LEGS:
			{
				// handle full armor
				final ItemInstance chest = getPaperdollItem(ParpedollType.CHEST);
				if ((chest != null) && (chest.getItem().getBodyPart() == SlotType.FULL_ARMOR))
				{
					setPaperdollItem(ParpedollType.CHEST, null);
				}
				
				setPaperdollItem(ParpedollType.LEGS, null);
				setPaperdollItem(ParpedollType.LEGS, item);
				break;
			}
			case FEET:
				setPaperdollItem(ParpedollType.FEET, item);
				break;
			case GLOVES:
				setPaperdollItem(ParpedollType.GLOVES, item);
				break;
			case HEAD:
				setPaperdollItem(ParpedollType.HEAD, item);
				break;
			case HAIR:
				setPaperdollItem(ParpedollType.HAIR, item);
				break;
			case UNDERWEAR:
				setPaperdollItem(ParpedollType.UNDER, item);
				break;
			case BACK:
				setPaperdollItem(ParpedollType.BACK, item);
				break;
			default:
				LOG.warning("unknown body slot:" + targetSlot);
		}
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	public void refreshWeight()
	{
		int weight = 0;
		
		for (final ItemInstance item : items)
		{
			if ((item != null) && (item.getItem() != null))
			{
				weight += item.getItem().getWeight() * item.getCount();
			}
		}
		
		totalWeight = weight;
	}
	
	/**
	 * Returns the totalWeight.
	 * @return int
	 */
	public int getTotalWeight()
	{
		return totalWeight;
	}
	
	/**
	 * Return the ItemInstance of the arrows needed for this bow.
	 * @param  bow : Item designating the bow
	 * @return     ItemInstance pointing out arrows for bow
	 */
	public ItemInstance findArrowForBow(Item bow)
	{
		int arrowsId = 0;
		
		switch (bow.getCrystalType())
		{
			default: // broken weapon.csv ??
			case CRYSTAL_NONE:
				arrowsId = 17;
				break; // Wooden arrow
			case CRYSTAL_D:
				arrowsId = 1341;
				break; // Bone arrow
			case CRYSTAL_C:
				arrowsId = 1342;
				break; // Fine steel arrow
			case CRYSTAL_B:
				arrowsId = 1343;
				break; // Silver arrow
			case CRYSTAL_A:
				arrowsId = 1344;
				break; // Mithril arrow
			case CRYSTAL_S:
				arrowsId = 1345;
				break; // Shining arrow
		}
		
		// Get the ItemInstance corresponding to the item identifier and return it
		return getItemById(arrowsId);
	}
	
	/**
	 * Get back items in inventory from database
	 */
	@Override
	public void restore()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT owner_id, object_id, item_id, count, enchant_level, loc, loc_data, freightLocation, price_sell, price_buy, time_of_use, custom_type1, custom_type2 FROM character_items WHERE owner_id=? AND (loc=? OR loc=?)"))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			ps.setString(3, getEquipLocation().name());
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					ItemInstance item = ItemInstance.restoreFromDb(rset);
					if (item == null)
					{
						continue;
					}
					
					if (getOwner() instanceof L2PcInstance)
					{
						final L2PcInstance player = (L2PcInstance) getOwner();
						
						if (!player.isGM() && !player.isHero())
						{
							final int itemId = item.getId();
							// Heroe items
							if (((itemId >= 6611) && (itemId <= 6621)) || (itemId == 6842))
							{
								item.setLocation(ItemLocationType.INVENTORY);
							}
						}
					}
					
					L2World.getInstance().addObject(item);
					
					// If stackable item is found in inventory just add to current quantity
					if (item.isStackable() && (getItemById(item.getId()) != null))
					{
						addItem("Restore", item, null, getOwner().getActingPlayer());
					}
					else
					{
						addItem(item);
					}
				}
			}
			
			refreshWeight();
		}
		catch (final Exception e)
		{
			LOG.warning("Could not restore inventory : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
