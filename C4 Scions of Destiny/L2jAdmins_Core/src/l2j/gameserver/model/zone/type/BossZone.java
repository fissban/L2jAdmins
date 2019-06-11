package l2j.gameserver.model.zone.type;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.GameServer;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * @author DaRkRaGe
 */
public class BossZone extends Zone
{
	private String zoneName;
	private int timeInvade;
	private boolean enabled = true; // default value, unless overridden by xml...
	
	// track the times that players got disconnected. Players are allowed
	// to log back into the zone as long as their log-out was within timeInvade
	// time...
	// <player objectId, expiration time in milliseconds>
	private final Map<Integer, Long> playerAllowedReEntryTimes;
	
	// track the players admitted to the zone who should be allowed back in
	// after reboot/server down time (outside of their control), within 30
	// of server restart
	private List<Integer> playersAllowed;
	
	public BossZone(int id)
	{
		super(id);
		playerAllowedReEntryTimes = new ConcurrentHashMap<>();
		playersAllowed = new CopyOnWriteArrayList<>();
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("Name"))
		{
			zoneName = value;
		}
		else if (name.equals("InvadeTime"))
		{
			timeInvade = Integer.parseInt(value);
		}
		else if (name.equals("EnabledByDefault"))
		{
			enabled = Boolean.parseBoolean(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneType.BOSS, true);
		
		if (!enabled)
		{
			return;
		}
		
		if (character instanceof L2Summon)
		{
			return;
		}
		
		L2PcInstance player = character.getActingPlayer();
		
		if (player != null)
		{
			if (player.isGM())
			{
				player.sendMessage("You entered " + zoneName);
				return;
			}
			
			// if player has been (previously) cleared by npc/ai for entry and the zone is
			// set to receive players (aka not waiting for boss to respawn)
			if (playersAllowed.contains(player.getObjectId()))
			{
				// Get the information about this player's last logout-exit from
				// this zone.
				Long expirationTime = playerAllowedReEntryTimes.get(player.getObjectId());
				
				// with legal entries, do nothing.
				if (expirationTime == null) // legal null expirationTime entries
				{
					long serverStartTime = GameServer.dateTimeServerStarted.getTimeInMillis();
					if ((serverStartTime > (System.currentTimeMillis() - timeInvade)))
					{
						return;
					}
				}
				else
				{
					// legal non-null logoutTime entries
					playerAllowedReEntryTimes.remove(player.getObjectId());
					if (expirationTime.longValue() > System.currentTimeMillis())
					{
						return;
					}
				}
				
				playersAllowed.remove(playersAllowed.indexOf(player.getObjectId()));
			}
			
			// teleport out all players who attempt "illegal" (re-)entry
			player.teleToLocation(TeleportWhereType.TOWN);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneType.BOSS, false);
		
		if (!enabled)
		{
			return;
		}
		
		L2PcInstance player = character.getActingPlayer();
		
		if (player != null)
		{
			if (player.isGM())
			{
				player.sendMessage("You left " + zoneName);
				return;
			}
			
			// if the player just got disconnected/logged out, store the dc
			// time so that
			// decisions can be made later about allowing or not the player
			// to log into the zone
			if ((!player.isOnline()) && playersAllowed.contains(player.getObjectId()))
			{
				// mark the time that the player left the zone
				playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + timeInvade);
			}
		}
	}
	
	public String getZoneName()
	{
		return zoneName;
	}
	
	public int getTimeInvade()
	{
		return timeInvade;
	}
	
	public void setAllowedPlayers(List<Integer> list)
	{
		if (list != null)
		{
			playersAllowed = list;
		}
	}
	
	public List<Integer> getAllowedPlayers()
	{
		return playersAllowed;
	}
	
	public boolean isPlayerAllowed(L2PcInstance player)
	{
		if (player.isGM())
		{
			return true;
		}
		
		if (playersAllowed.contains(player.getObjectId()))
		{
			return true;
		}
		
		player.teleToLocation(MapRegionData.TeleportWhereType.TOWN);
		return false;
	}
	
	/**
	 * Occasionally, all players need to be sent out of the zone (for example, if the players are just running around without fighting for too long, or if all players die, etc). This call sends all online players to town and marks offline players to be teleported (by clearing their relog expiration
	 * times) when they log back in (no real need for off-line teleport).
	 */
	public void oustAllPlayers()
	{
		if (characterList == null)
		{
			return;
		}
		
		if (characterList.isEmpty())
		{
			return;
		}
		
		for (L2Character character : characterList.values())
		{
			if (character == null)
			{
				continue;
			}
			
			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (!player.isOnline())
				{
					player.teleToLocation(TeleportWhereType.TOWN);
				}
			}
		}
		playerAllowedReEntryTimes.clear();
		playersAllowed.clear();
	}
	
	/**
	 * This function is to be used by external sources, such as quests and AI in order to allow a player for entry into the zone for some time. Naturally if the player does not enter within the allowed time, he/she will be teleported out again...
	 * @param player
	 * @param durationInSec
	 */
	public void allowPlayerEntry(L2PcInstance player, int durationInSec)
	{
		if (!player.isGM())
		{
			playersAllowed.add(player.getObjectId());
			playerAllowedReEntryTimes.put(player.getObjectId(), System.currentTimeMillis() + (durationInSec * 1000));
		}
	}
	
	public void setZoneEnabled(boolean flag)
	{
		if (enabled != flag)
		{
			oustAllPlayers();
		}
		enabled = flag;
	}
}
