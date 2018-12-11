package main.engine.events.daily.normal.types;

import main.data.ConfigData;
import main.engine.events.daily.AbstractEvent;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.UtilInventory;
import main.util.UtilSpawn;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.util.Broadcast;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class RandomBossSpawn extends AbstractEvent
{
	private static final String[] LOCATIONS =
	{
		"in the colliseum",
		"near the entrance of the Garden of Eva",
		"close to the western entrance of the Cemetary",
		"at Gludin's Harbor"
	};
	
	private static final LocationHolder[] SPAWNS =
	{
		new LocationHolder(150086, 46733, -3407),
		new LocationHolder(84805, 233832, -3669),
		new LocationHolder(161385, 21032, -3671),
		new LocationHolder(89199, 149962, -3581),
	};
	
	private static NpcHolder raid = null;
	
	/**
	 * Constructor
	 */
	public RandomBossSpawn()
	{
		registerMod(ConfigData.ENABLE_RandomBossSpawn);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				// Start random raid spawn
				startTimer("spawnRaids", ConfigData.RANDOM_BOSS_SPWNNED_TIME * 1000 * 60, null, null, true);
				break;
			case END:
				// Cancel random raid spawn
				cancelTimers("spawnRaids");
				break;
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder player)
	{
		switch (timerName)
		{
			case "spawnRaids":
				var rndLocation = Rnd.get(LOCATIONS.length - 1);
				var rndBoss = Rnd.get(ConfigData.RANDOM_BOSS_NPC_ID.size());
				// Spawn raid
				raid = UtilSpawn.npc(ConfigData.RANDOM_BOSS_NPC_ID.get(rndBoss), SPAWNS[rndLocation], 0, ConfigData.RANDOM_BOSS_SPWNNED_TIME * 1000 * 60, TeamType.NONE, 0);
				// Announcement spawn
				Broadcast.toAllOnlinePlayers("Raid " + raid.getInstance().getName() + " spawn " + LOCATIONS[rndLocation]);
				// Announcement lef time
				Broadcast.toAllOnlinePlayers("Have " + ConfigData.RANDOM_BOSS_SPWNNED_TIME + " minutes to kill");
				break;
		}
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		// Check if kill raid event
		if (victim == raid)
		{
			// Give reward and send message
			for (var reward : ConfigData.RANDOM_BOSS_REWARDS)
			{
				if (Rnd.get(100) <= reward.getRewardChance())
				{
					killer.getActingPlayer().getInstance().sendMessage("Have won " + reward.getRewardCount() + " " + ItemData.getInstance().getTemplate(reward.getRewardId()).getName());
					UtilInventory.giveItems(killer.getActingPlayer(), reward.getRewardId(), reward.getRewardCount(), 0);
				}
			}
		}
	}
}
