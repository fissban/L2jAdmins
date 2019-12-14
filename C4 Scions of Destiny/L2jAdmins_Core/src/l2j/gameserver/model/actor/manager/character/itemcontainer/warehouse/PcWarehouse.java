package l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.ItemContainer;
import l2j.gameserver.model.items.enums.ItemLocationType;

public class PcWarehouse extends ItemContainer
{
	private final L2PcInstance owner;
	
	public PcWarehouse(L2PcInstance owner)
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
		return ItemLocationType.WAREHOUSE;
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return ((items.size() + slots) <= owner.getWareHouseLimit());
	}
}
