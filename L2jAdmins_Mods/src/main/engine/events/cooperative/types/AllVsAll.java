package main.engine.events.cooperative.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.network.external.client.Say2.SayType;
import main.data.properties.ConfigData;
import main.engine.events.cooperative.AbstractCooperative;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilInventory;
import main.util.UtilMessage;
import main.util.builders.html.Html;

/**
 * @author fissban
 */
public class AllVsAll extends AbstractCooperative
{
	//
	private static final int RANGE_SPAWN = 800;
	/** Points of each player */
	private static Map<String, Integer> playerPoints = new ConcurrentHashMap<>();
	
	public AllVsAll()
	{
		super();
	}
	
	@Override
	public void onStart()
	{
		UtilMessage.sendAnnounceMsg("Prepared? Kill as many as you can!", getPlayersInEvent());
		// init all players in event
		getPlayersInEvent().forEach(ph -> playerPoints.put(ph.getName(), 0));
	}
	
	@Override
	public void onEnd()
	{
		var pointsOrdered = new LinkedHashMap<String, Integer>();
		
		// Send html showing the points of each player
		// Order the list according to your scores
		var LIMIT = playerPoints.size() / 2;
		playerPoints.entrySet().stream().sorted(Entry.<String, Integer> comparingByValue().reversed()).limit(LIMIT).forEach(e ->
		{
			pointsOrdered.put(e.getKey(), e.getValue());
		});
		
		// Generate the html of the ranking
		var hb = Html.eventRanking(pointsOrdered);
		// Send the html to each character in the event
		sendHtml(null, hb, getPlayersInEvent());
		
		// Clear
		playerPoints.clear();
	}
	
	@Override
	public void createTeams()
	{
		for (var ph : getPlayersInEvent())
		{
			// Save the character's location before being sent to the event.
			ph.setLastLoc(ph.getInstance().getX(), ph.getInstance().getY(), ph.getInstance().getZ());
			
			// player.setTeam(TeamType.BLUE.ordinal());
			// Teleport to event loc
			ph.getInstance().teleToLocation(ConfigData.AVA_SPAWN_POINT, RANGE_SPAWN);
		}
	}
	
	@Override
	public void giveRewards()
	{
		var pointsOrdered = new LinkedHashMap<String, Integer>();
		
		// Ordered the list according to your scores
		var LIMIT = playerPoints.size() / 2;
		
		playerPoints.entrySet().stream().sorted(Entry.<String, Integer> comparingByValue().reversed()).limit(LIMIT).forEach(e ->
		{
			pointsOrdered.put(e.getKey(), e.getValue());
		});
		
		for (var ph : getPlayersInEvent())
		{
			if (pointsOrdered.containsKey(ph.getName()))
			{
				// A message is sent to all participants informing the winning team.
				UtilMessage.sendCreatureMsg(ph, SayType.CHANNEL_LEADER, "[System]", "Congratulations winner!");
				// Prizes are awarded.
				ConfigData.AVA_REWARDS.forEach(rh -> UtilInventory.giveItems(ph, rh.getRewardId(), rh.getRewardCount(), 0));
			}
		}
	}
	
	// LISTENERS -----------------------------------------------------------------------------------------
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (Util.areObjectType(L2Playable.class, killer))
		{
			var killerPc = killer.getActingPlayer();
			var victimPc = victim.getActingPlayer();
			
			if (playerInEvent(killerPc, victimPc))
			{
				// Increase the points of each player
				increasePlayerPoint(killerPc);
			}
		}
	}
	
	@Override
	public boolean canAttack(CharacterHolder attacker, CharacterHolder victim)
	{
		return super.canAttack(attacker, victim);
	}
	
	@Override
	public void onDeath(CharacterHolder player)
	{
		// Check if participate in the event.
		if (!playerInEvent(player))
		{
			return;
		}
		
		var death = ThreadPoolManager.schedule(() ->
		{
			deathTasks.remove(player.getObjectId());
			
			PlayerHolder pc = player.getActingPlayer();
			// Revival
			revivePlayer(pc);
			// Heal
			healToMax(pc);
			// Buff
			giveBuff(pc);
			
		}, 10 * 1000);// 10 sec
		
		deathTasks.put(player.getObjectId(), death);
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
	
	/**
	 * Revive a character and send it to the spawn point
	 * @param ph
	 */
	private static void revivePlayer(PlayerHolder ph)
	{
		if (!ph.getInstance().isDead())
		{
			return;
		}
		
		ph.getInstance().doRevive();
		
		ph.getInstance().teleToLocation(ConfigData.AVA_SPAWN_POINT, RANGE_SPAWN);
	}
}
