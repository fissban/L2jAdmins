package main.engine.events.cooperative.types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import main.data.ConfigData;
import main.engine.events.cooperative.AbstractCooperative;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilInventory;
import main.util.UtilMessage;
import main.util.builders.html.Html;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Survive extends AbstractCooperative
{
	/** Points of each player */
	private static Map<String, Integer> playerPoints = new ConcurrentHashMap<>();
	/** List of mobs */
	private static final List<NpcHolder> mobs = new CopyOnWriteArrayList<>();
	/** State of the rounds */
	private static int round = 0;
	
	public Survive()
	{
		super();
	}
	
	@Override
	public void onStart()
	{
		UtilMessage.sendAnnounceMsg("Prepared? Survive as long as you can!", getPlayersInEvent());
		
		startTimer("newRound", 10 * 1000, null, null, false);
	}
	
	@Override
	public void onEnd()
	{
		var pointsOrdered = new LinkedHashMap<String, Integer>();
		
		// Send html showing the points of each player
		// Order the list according to your scores
		var LIMIT = 10;
		playerPoints.entrySet().stream().sorted(Entry.<String, Integer> comparingByValue().reversed()).limit(LIMIT).forEach(e ->
		{
			pointsOrdered.put(e.getKey(), e.getValue());
		});
		
		// Generate the html of the ranking
		var hb = Html.eventRanking(pointsOrdered);
		// Send the html to each character in the event
		sendHtml(null, hb, getPlayersInEvent());
		
		// Clear
		cancelTimer("newRound", null, null);
		playerPoints.clear();
		round = 0;
		unspawn();
	}
	
	@Override
	public void createTeams()
	{
		for (var ph : getPlayersInEvent())
		{
			// Save the character's location before being sent to the event.
			ph.setLastLoc(ph.getInstance().getX(), ph.getInstance().getY(), ph.getInstance().getZ());
			
			ph.setTeam(TeamType.BLUE);
			// Teleport to event loc
			ph.getInstance().teleToLocation(149564, 46667, -3438, 200);
		}
	}
	
	@Override
	public void giveRewards()
	{
		var dontGiveReward = false;
		if (round <= 5)
		{
			dontGiveReward = true;
		}
		
		UtilMessage.sendAnnounceMsg("Congratulations winners", getPlayersInEvent());
		
		for (var ph : getPlayersInEvent())
		{
			if (dontGiveReward)
			{
				UtilMessage.sendAnnounceMsg("They did not beat round 5 and did not deserve a prize.", getPlayersInEvent());
				continue;
			}
			
			// Prizes are awarded.
			ConfigData.SURVIVE_REWARDS.forEach(rh -> UtilInventory.giveItems(ph, rh.getRewardId(), rh.getRewardCount(), 0));
		}
	}
	
	// LISTENERS -----------------------------------------------------------------------------------------
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder player)
	{
		switch (timerName)
		{
			case "newRound":
			{
				// Increase round
				round++;
				
				UtilMessage.sendAnnounceMsg("Round " + round, getPlayersInEvent());
				
				// Give spawn to all mobs
				for (int i = 0; i < ConfigData.SURVIVE_MOBS_PER_ROUND * round; i++)
				{
					var rnd = Rnd.get(0, ConfigData.SURVIVE_MOBS_ID.size() - 1);
					
					var mobId = ConfigData.SURVIVE_MOBS_ID.get(rnd);
					
					mobs.add(addSpawnNpc(mobId, 149564, 46667, -3438, 0, ConfigData.SURVIVE_RANGE_SPAWN, 0, TeamType.RED));
				}
				break;
			}
		}
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (Util.areObjectType(L2Playable.class, killer))
		{
			var killerPc = killer.getActingPlayer();
			
			if (playerInEvent(killerPc) && mobs.contains(victim))
			{
				// Increase player point
				increasePlayerPoint(killerPc);
				
				// Increase the points of each player
				mobs.remove(victim);
				// Return the npc spawn to the original
				victim.setTeam(TeamType.NONE);
				// If they killed all the mobs we start another round
				if (mobs.isEmpty())
				{
					UtilMessage.sendAnnounceMsg("Watch out for another wave of mobs!", getPlayersInEvent());
					// In 10 seconds we start the round
					startTimer("newRound", 10 * 1000, null, null, false);
				}
			}
		}
	}
	
	@Override
	public boolean canAttack(CharacterHolder attacker, CharacterHolder victim)
	{
		if (Util.areObjectType(L2PcInstance.class, attacker))
		{
			if (playerInEvent(attacker) && mobs.contains(victim))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onDeath(CharacterHolder character)
	{
		// Check if the character is inside the event
		if (!playerInEvent(character))
		{
			return;
		}
		
		var pc = character.getActingPlayer();
		// Remove the player from the list
		removePlayerFromEvent(pc);
		// Send it to your last location
		pc.getInstance().teleToLocation(pc.getLastLoc(), 0);
		// Revival
		pc.getInstance().doRevive();
	}
	
	/**
	 * Increase by 1 the number of points of a character
	 * @param ph
	 */
	private static void increasePlayerPoint(PlayerHolder ph)
	{
		if (!playerPoints.containsKey(ph.getName()))
		{
			playerPoints.put(ph.getName(), 0);
		}
		
		var points = playerPoints.get(ph.getName());
		playerPoints.put(ph.getName(), ++points);
	}
	
	private static void unspawn()
	{
		// Delete All mobs
		mobs.stream().filter(mob -> mob.getInstance() != null).forEach(mob ->
		{
			Spawn spawn = mob.getInstance().getSpawn();
			if (spawn != null)
			{
				spawn.stopRespawn();
			}
			mob.getInstance().deleteMe();
		});
		// Clear
		mobs.clear();
	}
}
