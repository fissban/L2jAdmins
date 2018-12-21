package l2j.gameserver.instancemanager.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.task.continuous.GameTimeTaskManager;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @author godson
 */
public class DayNightSpawnManager
{
	public enum ModeType
	{
		DAY,
		NIGHT,
	}
	
	private static final Logger LOG = Logger.getLogger(DayNightSpawnManager.class.getName());
	
	private static List<Spawn> dayCreatures = new ArrayList<>();
	private static List<Spawn> nightCreatures = new ArrayList<>();
	private static Map<Spawn, L2RaidBossInstance> bosses = new HashMap<>();
	
	private static boolean init = false;
	
	public void cleanUp()
	{
		nightCreatures.clear();
		dayCreatures.clear();
		bosses.clear();
	}
	
	public void notifyChangeMode()
	{
		try
		{
			if (GameTimeTaskManager.getInstance().isNight())
			{
				changeMode(ModeType.NIGHT);
			}
			else
			{
				changeMode(ModeType.DAY);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addDayCreature(Spawn spawnDat)
	{
		dayCreatures.add(spawnDat);
	}
	
	public void addNightCreature(Spawn spawnDat)
	{
		nightCreatures.add(spawnDat);
	}
	
	public void spawnDayCreatures()
	{
		spawnCreatures(nightCreatures, dayCreatures, "night", "day");
	}
	
	public void spawnNightCreatures()
	{
		spawnCreatures(dayCreatures, nightCreatures, "day", "night");
	}
	
	/**
	 * Manage Spawn/Respawn.
	 * @param unSpawnCreatures List with L2Npc must be unspawned
	 * @param spawnCreatures   List with L2Npc must be spawned
	 * @param unspawnLogInfo   String for log info for unspawned L2Npc
	 * @param spawnLogInfo     String for log info for spawned L2Npc
	 */
	private static void spawnCreatures(List<Spawn> unSpawnCreatures, List<Spawn> spawnCreatures, String unspawnLogInfo, String spawnLogInfo)
	{
		try
		{
			if (!unSpawnCreatures.isEmpty())
			{
				int i = 0;
				for (Spawn spawn : unSpawnCreatures)
				{
					if (spawn == null)
					{
						continue;
					}
					
					spawn.stopRespawn();
					L2Npc last = spawn.getLastSpawn();
					if (last != null)
					{
						last.deleteMe();
						i++;
					}
				}
				UtilPrint.result("DayNightSpawnManager", "Removed creatures " + unspawnLogInfo, i);
			}
			
			int i = 0;
			for (Spawn spawnDat : spawnCreatures)
			{
				if (spawnDat == null)
				{
					continue;
				}
				spawnDat.startRespawn();
				spawnDat.doSpawn();
				i++;
			}
			
			if (init)
			{
				UtilPrint.result("DayNightSpawnManager", "Spawned creatures " + spawnLogInfo, i);
			}
			else
			{
				UtilPrint.result("DayNightSpawnManager", "Spawned creatures " + spawnLogInfo, i);
				init = true;
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Error while spawning creatures: " + e.getMessage(), e);
		}
	}
	
	/**
	 * @param mode ModeType
	 */
	private void changeMode(ModeType mode)
	{
		if ((nightCreatures.isEmpty()) && (dayCreatures.isEmpty()))
		{
			return;
		}
		
		specialBoss(mode);
		
		switch (mode)
		{
			case DAY:
				spawnDayCreatures();
				break;
			case NIGHT:
				spawnNightCreatures();
				break;
		}
	}
	
	private void specialBoss(ModeType mode)
	{
		try
		{
			for (Spawn spawn : bosses.keySet())
			{
				L2RaidBossInstance boss = bosses.get(spawn);
				
				if (boss == null)
				{
					switch (mode)
					{
						case DAY:
							// no actions
							continue;
						case NIGHT:
							boss = (L2RaidBossInstance) spawn.doSpawn();
							RaidBossSpawnData.getInstance().notifySpawnNightBoss(boss);
							bosses.remove(spawn);
							bosses.put(spawn, boss);
							continue;
					}
				}
				else
				{
					if ((boss.getId() == 10328) && boss.getRaidStatus().equals(RaidBossSpawnData.StatusEnum.ALIVE))
					{
						handleHellmans(boss, mode);
					}
				}
				
				return;
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleHellmans(L2RaidBossInstance boss, ModeType mode)
	{
		switch (mode)
		{
			case DAY:
				boss.deleteMe();
				LOG.info("DayNightSpawnManager: Deleting Hellman raidboss");
				break;
			case NIGHT:
				boss.spawnMe();
				LOG.info("DayNightSpawnManager: Spawning Hellman raidboss");
				break;
		}
	}
	
	public L2RaidBossInstance handleBoss(Spawn spawnDat)
	{
		if (bosses.containsKey(spawnDat))
		{
			return bosses.get(spawnDat);
		}
		
		if (GameTimeTaskManager.getInstance().isNight())
		{
			L2RaidBossInstance raidboss = (L2RaidBossInstance) spawnDat.doSpawn();
			bosses.put(spawnDat, raidboss);
			
			return raidboss;
		}
		bosses.put(spawnDat, null);
		
		return null;
	}
	
	public static DayNightSpawnManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DayNightSpawnManager INSTANCE = new DayNightSpawnManager();
	}
}
