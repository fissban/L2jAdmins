package l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance.FlagType;
import l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * A castle zone
 * @author durgus
 */
public class SiegeZone extends ZoneSpawn
{
	private int castleId;
	private Castle castle;
	
	public SiegeZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
		{
			castleId = Integer.parseInt(value);
			
			// Register self to the correct castle
			castle = CastleData.getInstance().getCastleById(castleId);
			castle.setZone(this);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (castle.getSiege().isInProgress())
		{
			character.setInsideZone(ZoneType.PVP, true);
			character.setInsideZone(ZoneType.SIEGE, true);
			
			if (character instanceof L2PcInstance)
			
			{
				L2PcInstance player = (L2PcInstance) character;
				
				player.sendPacket(SystemMessage.ENTERED_COMBAT_ZONE);
				
				if (player.isFlying())
				{
					boolean isCastleLord = (player.getClan() != null) && player.isClanLeader() && (player.getClan().getCastleId() == castle.getId());
					if (!isCastleLord)
					{
						player.teleToLocation(TeleportWhereType.TOWN);
					}
				}
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (castle.getSiege().isInProgress())
		{
			character.setInsideZone(ZoneType.PVP, false);
			character.setInsideZone(ZoneType.SIEGE, false);
			
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				player.sendPacket(SystemMessage.LEFT_COMBAT_ZONE);
				
				// Set pvp flag
				if (player.isStatusPvpFlag(FlagType.NON_PVP))
				{
					player.updatePvPStatus();
				}
			}
		}
		
		if (character instanceof L2SiegeSummonInstance)
		{
			((L2SiegeSummonInstance) character).unSummon();
		}
	}
	
	public void updateZoneStatusForCharactersInside()
	{
		if (castle.getSiege().isInProgress())
		{
			for (L2Character character : characterList.values())
			{
				try
				{
					onEnter(character);
				}
				catch (NullPointerException e)
				{
				}
			}
		}
		else
		{
			for (L2Character character : characterList.values())
			{
				try
				{
					character.setInsideZone(ZoneType.PVP, false);
					character.setInsideZone(ZoneType.SIEGE, false);
					
					if (character instanceof L2PcInstance)
					{
						((L2PcInstance) character).sendPacket(SystemMessage.LEFT_COMBAT_ZONE);
					}
					
					if (character instanceof L2SiegeSummonInstance)
					{
						((L2SiegeSummonInstance) character).unSummon();
					}
				}
				catch (NullPointerException e)
				{
				}
			}
		}
	}
	
	/**
	 * Removes all foreigners from the castle
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		for (L2Character temp : characterList.values())
		{
			if (!(temp instanceof L2PcInstance))
			{
				return;
			}
			
			if (((L2PcInstance) temp).getClanId() != owningClanId)
			{
				continue;
			}
			
			((L2PcInstance) temp).teleToLocation(TeleportWhereType.TOWN);
		}
	}
	
	/**
	 * Sends a message to all players in this zone
	 * @param message
	 */
	public void announceToPlayers(String message)
	{
		for (L2Character temp : characterList.values())
		{
			if (temp instanceof L2PcInstance)
			{
				((L2PcInstance) temp).sendMessage(message);
			}
		}
	}
	
	/**
	 * Returns all players within this zone
	 * @return
	 */
	public List<L2PcInstance> getAllPlayers()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (L2Character temp : characterList.values())
		{
			if (temp instanceof L2PcInstance)
			{
				players.add((L2PcInstance) temp);
			}
		}
		
		return players;
	}
}
