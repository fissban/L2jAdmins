package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.ItemContainer;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;

public class PcFreightManager extends ItemContainer
{
	private final L2PcInstance owner; // This is the L2PcInstance that owns this Freight
	private int activeLocationId = 0;
	private int tempOwnerId = 0;
	
	public PcFreightManager(L2PcInstance owner)
	{
		this.owner = owner;
	}
	
	@Override
	public L2PcInstance getOwner()
	{
		return owner;
	}
	
	@Override
	public ItemLocationType getBaseLocation()
	{
		return ItemLocationType.FREIGHT;
	}
	
	public void setActiveLocation(int locationId)
	{
		activeLocationId = locationId;
	}
	
	/**
	 * Verificamos si tenemos items para recolectar en una cierta ubicacion
	 * @return boolean
	 */
	public boolean getAvailablePackages()
	{
		for (ItemInstance item : items)
		{
			if ((item.getFreigtLocation() == 0) || (activeLocationId == 0) || (item.getFreigtLocation() == activeLocationId))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds item to PcFreight for further adjustments.
	 * @param item : L2ItemInstance to be added from inventory
	 */
	@Override
	protected void addItem(ItemInstance item)
	{
		super.addItem(item);
		if (activeLocationId > 0)
		{
			item.setFreigtLocation(item.getLocation(), activeLocationId);
		}
	}
	
	/**
	 * Get back items in PcFreight from database
	 */
	@Override
	public void restore()
	{
		int locationId = activeLocationId;
		activeLocationId = 0;
		super.restore();
		activeLocationId = locationId;
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		int cap = (owner == null ? Config.FREIGHT_SLOTS : owner.getFreightLimit());
		return ((getSize() + slots) <= cap);
	}
	
	@Override
	public int getOwnerId()
	{
		if (owner == null)
		{
			return tempOwnerId;
		}
		return super.getOwnerId();
	}
	
	public void doQuickRestore(int val)
	{
		tempOwnerId = val;
		restore();
	}
}
