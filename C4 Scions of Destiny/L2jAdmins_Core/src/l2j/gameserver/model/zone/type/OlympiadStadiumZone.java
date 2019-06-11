package l2j.gameserver.model.zone.type;

import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.OlympiadGameTask;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ExOlympiadMatchEnd;
import l2j.gameserver.network.external.server.ExOlympiadUserInfo;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * An olympiad stadium
 * @author durgus
 */
public class OlympiadStadiumZone extends ZoneSpawn
{
	private int stadiumId;
	OlympiadGameTask task = null;
	
	public OlympiadStadiumZone(int id)
	{
		super(id);
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		this.task = task;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("StadiumId"))
		{
			stadiumId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneType.PVP, true);
		character.setInsideZone(ZoneType.NOLANDING, true);
		
		if (task != null)
		{
			if (task.isBattleStarted())
			{
				character.setInsideZone(ZoneType.PVP, true);
				if (character instanceof L2PcInstance)
				{
					character.sendPacket(new SystemMessage(SystemMessage.ENTERED_COMBAT_ZONE));
					task.getGame().sendOlympiadInfo(character);
				}
			}
		}
		
		if (character instanceof L2Playable)
		{
			L2PcInstance player = character.getActingPlayer();
			if (player != null)
			{
				// only participants, observers and GMs allowed
				if (!player.isGM() && !player.isInOlympiadMode() && !player.inObserverMode())
				{
					L2Summon summon = player.getPet();
					if (summon != null)
					{
						summon.unSummon();
					}
					
					player.teleToLocation(TeleportWhereType.TOWN);
				}
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneType.PVP, false);
		character.setInsideZone(ZoneType.NOLANDING, false);
		
		if (task != null)
		{
			if (task.isBattleStarted())
			{
				character.setInsideZone(ZoneType.PVP, false);
				if (character instanceof L2PcInstance)
				{
					character.sendPacket(new SystemMessage(SystemMessage.LEFT_COMBAT_ZONE));
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
	
	/**
	 * @return this zones stadium id (if any)
	 */
	public int getStadiumId()
	{
		return stadiumId;
	}
	
	public final void broadcastPacketToObservers(AServerPacket packet)
	{
		for (L2PcInstance player : getKnownTypeInside(L2PcInstance.class))
		{
			if (player.inObserverMode())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	public final void broadcastStatusUpdate(L2PcInstance player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (L2PcInstance plyr : getKnownTypeInside(L2PcInstance.class))
		{
			if (plyr.inObserverMode() || (plyr.getOlympiadSide() != player.getOlympiadSide()))
			{
				plyr.sendPacket(packet);
			}
		}
	}
	
	/**
	 * 
	 */
	public final void updateZoneStatusForCharactersInside()
	{
		if (task == null)
		{
			return;
		}
		
		final boolean battleStarted = task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
		{
			sm = new SystemMessage(SystemMessage.ENTERED_COMBAT_ZONE);
		}
		else
		{
			sm = new SystemMessage(SystemMessage.LEFT_COMBAT_ZONE);
		}
		
		for (L2Character character : characterList.values())
		{
			if (character == null)
			{
				continue;
			}
			
			if (battleStarted)
			{
				character.setInsideZone(ZoneType.PVP, true);
				if (character instanceof L2PcInstance)
				{
					character.sendPacket(sm);
				}
			}
			else
			{
				character.setInsideZone(ZoneType.PVP, false);
				if (character instanceof L2PcInstance)
				{
					character.sendPacket(sm);
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
}
