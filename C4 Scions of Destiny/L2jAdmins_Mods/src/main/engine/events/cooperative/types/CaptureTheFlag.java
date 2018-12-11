package main.engine.events.cooperative.types;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import main.data.ConfigData;
import main.engine.events.cooperative.AbstractCooperative;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilInventory;
import main.util.UtilMessage;
import main.util.builders.html.Html;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.network.external.server.InventoryUpdate;

/**
 * @author fissban
 */
public class CaptureTheFlag extends AbstractCooperative
{
	/** FlagItem */
	private static final int FLAG_ITEM = 6718;
	/** Points of blue team */
	private static volatile int pointsBlue = 0;
	/** Points of red team */
	private static volatile int pointsRed = 0;
	/** Points of each player */
	private static Map<String, Integer> playerPoints = new ConcurrentHashMap<>();
	/** Flags and holders instances */
	private static Map<Integer, NpcHolder> flagAndHolders = new HashMap<>();
	
	public CaptureTheFlag()
	{
		super();
	}
	
	@Override
	public boolean canAttack(CharacterHolder attacker, CharacterHolder victim)
	{
		if (Util.areObjectType(L2PcInstance.class, attacker, victim))
		{
			// Check if the 2 players are participating in the event.
			if (playerInEvent(attacker, victim))
			{
				// Check the team of each character.
				if (attacker.getTeam() != victim.getTeam())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void createParty()
	{
		// Leader Party Blue
		PlayerHolder lpb = null;
		// Leader Party Red
		PlayerHolder lpr = null;
		
		if (getPlayersInEvent().size() <= 3)
		{
			return;
		}
		
		for (var ph : getPlayersInEvent())
		{
			if (ph == null)
			{
				continue;
			}
			
			// Remove from old party
			if (ph.getInstance().getParty() != null)
			{
				ph.getInstance().getParty().removePartyMember(ph.getInstance(), true);
			}
			
			PlayerHolder leader = null;
			
			switch (ph.getTeam())
			{
				case BLUE:
					leader = lpb;
					break;
				case RED:
					leader = lpr;
					break;
			}
			
			// If there is no leader yet, we assign one
			if (leader == null)
			{
				leader = ph;
				continue;
			}
			
			// If the party exceeds the limit of members we start the creation of a new one
			if (leader != null && leader.getInstance().getParty() != null && leader.getInstance().getParty().getMemberCount() >= 7)
			{
				switch (ph.getTeam())
				{
					case BLUE:
						lpb = ph;
						continue;
					case RED:
						lpr = ph;
						continue;
				}
			}
			
			// Created the party
			if (leader.getInstance().getParty() == null)
			{
				Party party = new Party(leader.getInstance());
				party.addPartyMember(ph.getInstance());
			}
			// Added a new party member
			else
			{
				leader.getInstance().getParty().addPartyMember(ph.getInstance());
			}
		}
	}
	
	@Override
	public void createTeams()
	{
		var lastTeam = TeamType.BLUE;
		
		for (var ph : getPlayersInEvent())
		{
			// Save the character's location before being sent to the event.
			ph.setLastLoc(ph.getInstance().getX(), ph.getInstance().getY(), ph.getInstance().getZ());
			
			switch (lastTeam)
			{
				case RED:
					// Teleport
					ph.getInstance().teleToLocation(ConfigData.CTF_SPAWN_TEAM_BLUE, ConfigData.CTF_RADIUS_SPAWN);
					// Set team
					ph.setTeam(TeamType.BLUE);
					// Set last team
					lastTeam = TeamType.BLUE;
					break;
				case BLUE:
					// Teleport
					ph.getInstance().teleToLocation(ConfigData.CTF_SPAWN_TEAM_RED, ConfigData.CTF_RADIUS_SPAWN);
					// Set el team
					ph.setTeam(TeamType.RED);
					// Set last team
					lastTeam = TeamType.RED;
					break;
				default:
					// Never happens!
					break;
			}
		}
	}
	
	@Override
	public void giveRewards()
	{
		// Check the winning team.
		var teamWinner = TeamType.NONE;
		
		// Tied teams
		if (pointsBlue == pointsRed)
		{
			//
		}
		else if (pointsBlue > pointsRed)
		{
			teamWinner = TeamType.BLUE;
		}
		else
		{
			teamWinner = TeamType.RED;
		}
		
		if (teamWinner == TeamType.NONE)
		{
			UtilMessage.sendAnnounceMsg("The event ends in a draw!", getPlayersInEvent());
		}
		else
		{
			UtilMessage.sendAnnounceMsg("The team " + teamWinner.name() + " was the winner!", getPlayersInEvent());
			
			for (var ph : getPlayersInEvent())
			{
				// Prizes are awarded to the winning team.
				if (ph.getTeam() == teamWinner)
				{
					ConfigData.CTF_REWARDS.forEach(rh -> UtilInventory.giveItems(ph, rh.getRewardId(), rh.getRewardCount(), 0));
				}
			}
		}
	}
	
	// LISTENERS -----------------------------------------------------------------------------------------
	
	/**
	 * @param ph
	 * @return
	 */
	@Override
	public boolean onExitWorld(PlayerHolder ph)
	{
		if (ph.hasFlag())
		{
			unequipFlag(ph, true);
		}
		
		return super.onExitWorld(ph);
	}
	
	@Override
	public void onDeath(CharacterHolder character)
	{
		// Check if the character is inside the event
		if (!playerInEvent(character))
		{
			return;
		}
		
		var ph = character.getActingPlayer();
		
		if (ph.hasFlag())
		{
			unequipFlag(ph, true);
		}
		
		var death = ThreadPoolManager.getInstance().schedule(() ->
		{
			deathTasks.remove(character.getObjectId());
			
			// Revive
			revivePlayer(ph);
			// Heal
			healToMax(ph);
			// Buff
			giveBuff(ph);
			
		}, 10 * 1000); // 10 sec
		
		deathTasks.put(character.getObjectId(), death);
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (Util.areObjectType(L2Playable.class, killer, victim))
		{
			var killerPc = killer.getActingPlayer();
			var victimPc = victim.getActingPlayer();
			
			if (playerInEvent(killerPc, victimPc))
			{
				// Increase the points of each team
				if (killerPc.getTeam() == TeamType.BLUE)
				{
					pointsBlue += ConfigData.CTF_POINT_KILL_PLAYER;
				}
				else
				{
					pointsRed += ConfigData.CTF_POINT_KILL_PLAYER;
				}
				
				// Send all the characters of the event the amount of points.
				sendPointsToAllParticipant();
				// Increase the points of each player
				increasePlayerPoint(killerPc, ConfigData.CTF_POINT_KILL_PLAYER);
			}
		}
	}
	
	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder character)
	{
		if (!Util.areObjectType(L2Npc.class, character))
		{
			return false;
		}
		
		// Check if the player participates in the event.
		if (!playerInEvent(ph))
		{
			return false;
		}
		
		var flag = (NpcHolder) character;
		
		if (flag.getId() == ConfigData.CTF_FLAG_ID)
		{
			if (ph.hasFlag())
			{
				return false;
			}
			if (ph.getTeam() != flag.getTeam())
			{
				equipFlag(ph, flag);
				return true;
			}
			return false;
		}
		
		if (flag.getId() == ConfigData.CTF_HOLDER_ID)
		{
			if (ph.hasFlag())
			{
				if (ph.getTeam() == flag.getTeam())
				{
					unequipFlag(ph, false);
					
					increasePlayerPoint(ph, ConfigData.CTF_POINT_DELIVER_FLAG);
					
					switch (ph.getTeam())
					{
						case BLUE:
							pointsBlue += ConfigData.CTF_POINT_DELIVER_FLAG;
							break;
						case RED:
							pointsRed += ConfigData.CTF_POINT_DELIVER_FLAG;
							break;
					}
					
					sendPointsToAllParticipant();
					return true;
				}
			}
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onUnequip(CharacterHolder player)
	{
		if (player instanceof PlayerHolder)
		{
			var ph = (PlayerHolder) player;
			var flag = ph.getInstance().getInventory().getPaperdollItem(ParpedollType.RHAND);
			
			if (flag == null && ph.hasFlag())
			{
				// drop flag
				unequipFlag(ph, true);
			}
		}
	}
	
	// XXX MISC ------------------------------------------------------------------------------------------------------------
	
	@Override
	public void onStart()
	{
		var xRed = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getX();
		var yRed = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getY();
		var zRed = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getZ();
		
		var xBlue = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getX();
		var yBlue = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getY();
		var zBlue = ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getZ();
		
		// Flag spawn
		addSpawnNpc(ConfigData.CTF_FLAG_ID, xBlue, yBlue, zBlue, 0, 0, 0, TeamType.BLUE);
		addSpawnNpc(ConfigData.CTF_FLAG_ID, xRed, yRed, zRed, 0, 0, 0, TeamType.RED);
		// Holder spawn
		addSpawnNpc(ConfigData.CTF_HOLDER_ID, xBlue - 100, yBlue, zBlue, 0, 0, 0, TeamType.BLUE);// FIXME chequear el -100 si queda bien
		addSpawnNpc(ConfigData.CTF_HOLDER_ID, xRed - 100, yRed, zRed, 0, 0, 0, TeamType.RED);// FIXME chequear el -100 si queda bien
	}
	
	@Override
	public void onEnd()
	{
		// Cancel party
		getPlayersInEvent().stream().filter(ph -> ph.getInstance().getParty() != null).forEach(ph -> ph.getInstance().getParty().removePartyMember(ph.getInstance(), true));
		
		// Unspawn all flags and holders
		flagAndHolders.values().stream().filter(npc -> npc.getInstance() != null).forEach(npc ->
		{
			// stop respawn
			if (npc.getInstance().getSpawn() != null)
			{
				npc.getInstance().getSpawn().stopRespawn();
			}
			// remove from world
			npc.getInstance().deleteMe();
		});
		
		// Generate ranking
		var pointsOrdered = new LinkedHashMap<String, Integer>();
		
		// Send html showing the points of each player
		// order the list according to your scores
		var LIMIT = 10;
		playerPoints.entrySet().stream().sorted(Entry.<String, Integer> comparingByValue().reversed()).limit(LIMIT).forEach(e -> pointsOrdered.put(e.getKey(), e.getValue()));
		
		// Generate the html of the ranking
		var hb = Html.eventRanking(pointsOrdered);
		// Send the html to each character in the event
		sendHtml(null, hb, getPlayersInEvent());
		
		// Clear
		flagAndHolders.clear();
		playerPoints.clear();
		pointsBlue = 0;
		pointsRed = 0;
	}
	
	/**
	 * Revive a character and send it to the spawn point according to his team
	 * @param ph
	 */
	private void revivePlayer(PlayerHolder ph)
	{
		if (!ph.getInstance().isDead())
		{
			return;
		}
		
		ph.getInstance().doRevive();
		
		switch (ph.getTeam())
		{
			case BLUE:
				ph.getInstance().teleToLocation(ConfigData.CTF_SPAWN_TEAM_BLUE, ConfigData.CTF_RADIUS_SPAWN);
				break;
			case RED:
				ph.getInstance().teleToLocation(ConfigData.CTF_SPAWN_TEAM_RED, ConfigData.CTF_RADIUS_SPAWN);
				break;
		}
	}
	
	/**
	 * Increase by 1 the number of points of a character
	 * @param ph
	 */
	private static void increasePlayerPoint(PlayerHolder ph, int p)
	{
		if (!playerPoints.containsKey(ph.getName()))
		{
			playerPoints.put(ph.getName(), 0);
		}
		
		int points = playerPoints.get(ph.getName()) + p;
		playerPoints.put(ph.getName(), points);
	}
	
	/**
	 * Equip a character with a flag.
	 * @param ph
	 * @param flagTeam
	 */
	private static void equipFlag(PlayerHolder ph, NpcHolder npc)
	{
		var teamType = ph.getTeam();
		
		// get and remove flag from memory
		var flag = flagAndHolders.remove(npc.getObjectId());
		// stop flag respawn
		if (flag.getInstance().getSpawn() != null)
		{
			flag.getInstance().getSpawn().stopRespawn();
		}
		// remove flag from world
		flag.getInstance().deleteMe();
		
		ph.setHasFlag(true);
		// Equip flag
		var flagItem = ItemData.getInstance().createItem("", FLAG_ITEM, 1, ph.getInstance(), null);
		if (flagItem != null)
		{
			ph.getInstance().getInventory().equipItem(flagItem);
		}
		// Send Message
		UtilMessage.sendAnnounceMsg("The " + teamType.name().toLowerCase() + " team has taken the flag!", getPlayersInEvent());
	}
	
	/**
	 * We remove the flag of a character.
	 * @param ph
	 */
	private void unequipFlag(PlayerHolder ph, boolean drop)
	{
		// Create new spawn
		switch (ph.getTeam())
		{
			case BLUE:
				var xRed = drop ? ph.getInstance().getX() : ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getX();
				var yRed = drop ? ph.getInstance().getY() : ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getY();
				var zRed = drop ? ph.getInstance().getZ() : ConfigData.CTF_FLAG_SPAWN_TEAM_RED.getZ();
				addSpawnNpc(ConfigData.CTF_FLAG_ID, xRed, yRed, zRed, 0, 0, 0, TeamType.RED);
				break;
			case RED:
				var xBlue = drop ? ph.getInstance().getX() : ConfigData.CTF_FLAG_SPAWN_TEAM_BLUE.getX();
				var yBlue = drop ? ph.getInstance().getY() : ConfigData.CTF_FLAG_SPAWN_TEAM_BLUE.getY();
				var zBlue = drop ? ph.getInstance().getZ() : ConfigData.CTF_FLAG_SPAWN_TEAM_BLUE.getZ();
				addSpawnNpc(ConfigData.CTF_FLAG_ID, xBlue, yBlue, zBlue, 0, 0, 0, TeamType.BLUE);
				break;
		}
		
		// Unequip flag
		var flag = ph.getInstance().getInventory().getPaperdollItem(ParpedollType.RHAND);
		if (flag != null)
		{
			var items = ph.getInstance().getInventory().unEquipItemInSlotAndRecord(ParpedollType.RHAND);
			
			var iu = new InventoryUpdate();
			iu.addItems(items);
			ph.getInstance().sendPacket(iu);
		}
		
		var teamType = ph.getTeam();
		if (drop)
		{
			UtilMessage.sendAnnounceMsg("The " + teamType.name().toLowerCase() + " has drop flag!", getPlayersInEvent());
		}
		else
		{
			UtilMessage.sendAnnounceMsg("The " + teamType.name().toLowerCase() + " team has delivered the flag!", getPlayersInEvent());
		}
	}
	
	@Override
	public NpcHolder addSpawnNpc(int npcId, int x, int y, int z, int heading, int randomOffset, long despawnDelay, TeamType teamType)
	{
		var npc = super.addSpawnNpc(npcId, x, y, z, heading, randomOffset, despawnDelay, teamType);
		flagAndHolders.put(npc.getObjectId(), npc);
		return npc;
	}
	
	/**
	 * Send all the characters a message on the screen showing the points of each team
	 */
	private static void sendPointsToAllParticipant()
	{
		UtilMessage.sendAnnounceMsg("BLUE " + pointsBlue + " | " + pointsRed + " RED", getPlayersInEvent());
	}
}
