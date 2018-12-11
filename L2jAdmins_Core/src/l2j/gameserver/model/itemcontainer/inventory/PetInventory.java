package l2j.gameserver.model.itemcontainer.inventory;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.PetInventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

public class PetInventory extends Inventory
{
	private final L2PetInstance owner;
	
	public PetInventory(L2PetInstance owner)
	{
		this.owner = owner;
	}
	
	@Override
	public L2PetInstance getOwner()
	{
		return owner;
	}
	
	@Override
	public int getOwnerId()
	{
		// gets the L2PcInstance-owner's ID
		int id;
		try
		{
			id = owner.getOwner().getObjectId();
		}
		catch (NullPointerException e)
		{
			return 0;
		}
		return id;
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	public void refreshWeight()
	{
		super.refreshWeight();
		getOwner().updateAndBroadcastStatus(1);
	}
	
	public boolean validateCapacity(ItemInstance item)
	{
		int slots = 0;
		
		if (!(item.isStackable() && (getItemById(item.getId()) != null)))
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
	
	public boolean validateWeight(ItemInstance item, int count)
	{
		int weight = 0;
		Item template = ItemData.getInstance().getTemplate(item.getId());
		if (template == null)
		{
			return false;
		}
		weight += count * template.getWeight();
		return validateWeight(weight);
	}
	
	@Override
	public boolean validateWeight(int weight)
	{
		return ((totalWeight + weight) <= owner.getMaxLoad());
	}
	
	@Override
	protected ItemLocationType getBaseLocation()
	{
		return ItemLocationType.PET;
	}
	
	@Override
	protected ItemLocationType getEquipLocation()
	{
		return ItemLocationType.PET_EQUIP;
	}
	
	/**
	 * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the L2PcInstance.
	 * @param  process     : String Identifier of process triggering this action
	 * @param  objectId    : int Item Instance identifier of the item to be destroyed
	 * @param  count       : int Quantity of items to be destroyed
	 * @param  reference   : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @param  sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return             boolean informing if the action was successful
	 */
	public boolean destroyItem(String process, int objectId, int count, L2Object reference, boolean sendMessage)
	{
		final ItemInstance item = destroyItem(process, objectId, count, owner.getOwner(), reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				owner.getOwner().sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			
			return false;
		}
		
		// Send Pet inventory update packet
		final PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		owner.getOwner().sendPacket(petIU);
		
		if (sendMessage)
		{
			owner.getOwner().sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addNumber(count).addItemName(item.getId()));
		}
		return true;
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
		final ItemInstance item = destroyItemByItemId(process, itemId, count, owner.getOwner(), reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				owner.getOwner().sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			}
			return false;
		}
		
		// Send Pet inventory update packet
		final PetInventoryUpdate petIU = new PetInventoryUpdate();
		petIU.addItem(item);
		owner.getOwner().sendPacket(petIU);
		
		if (sendMessage)
		{
			owner.getOwner().sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addNumber(count).addItemName(itemId));
		}
		
		return true;
	}
}
