package l2j.gameserver.model.zone.type;

import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.ClanHallDecoration;

/**
 * A clan hall zone
 * @author durgus
 */
public class ClanHallZone extends ZoneSpawn
{
	private int clanHallId;
	
	public ClanHallZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("clanHallId"))
		{
			clanHallId = Integer.parseInt(value);
			
			// Register self to the correct clan hall
			ClanHallData.getInstance().getClanHallById(clanHallId).setZone(this);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// Set as in clan hall
			character.setInsideZone(ZoneType.CLANHALL, true);
			
			ClanHall clanHall = ClanHallData.getInstance().getClanHallById(clanHallId);
			if (clanHall == null)
			{
				return;
			}
			
			((L2PcInstance) character).sendPacket(new ClanHallDecoration(clanHall));
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// Unset clanhall zone
			character.setInsideZone(ZoneType.CLANHALL, false);
		}
	}
	
	/**
	 * Removes all foreigners from the clan hall
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		for (L2Character temp : characterList.values())
		{
			if (!(temp instanceof L2PcInstance))
			{
				continue;
			}
			
			if (((L2PcInstance) temp).getClanId() == owningClanId)
			{
				continue;
			}
			
			((L2PcInstance) temp).teleToLocation(TeleportWhereType.TOWN);
		}
	}
}
