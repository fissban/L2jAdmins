package l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.ItemContainer;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.items.enums.ItemLocationType;

public final class ClanWarehouse extends ItemContainer
{
	private final Clan clan;
	
	public ClanWarehouse(Clan clan)
	{
		this.clan = clan;
	}
	
	@Override
	public int getOwnerId()
	{
		return clan.getId();
	}
	
	@Override
	public L2PcInstance getOwner()
	{
		return clan.getLeader().getPlayerInstance();
	}
	
	@Override
	public ItemLocationType getBaseLocation()
	{
		return ItemLocationType.CLANWH;
	}
	
	public String getLocationId()
	{
		return "0";
	}
	
	public int getLocationId(boolean dummy)
	{
		return 0;
	}
	
	public void setLocationId(L2PcInstance dummy)
	{
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return ((items.size() + slots) <= Config.WAREHOUSE_SLOTS_CLAN);
	}
}
