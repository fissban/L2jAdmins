package l2j.gameserver.instancemanager.spawn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.AutoSpawnHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.Rnd;

/**
 * @author Tempy, fissban
 */
public class AutoSpawnManager
{
	protected static final Logger LOG = Logger.getLogger(AutoSpawnManager.class.getName());
	//
	private final Map<Integer, AutoSpawnHolder> registeredSpawns = new HashMap<>();
	//
	private final Map<Integer, ScheduledFuture<?>> runningSpawns = new HashMap<>();
	
	public AutoSpawnManager()
	{
		// any action
	}
	
	/**
	 * Registers a spawn with the given parameters with the spawner, and marks it as active if required. Returns a AutoSpawnHolder containing info about the spawn.
	 * @param newSpawn
	 * @param isSpawn
	 */
	public void registerSpawn(AutoSpawnHolder newSpawn, boolean isSpawn)
	{
		// register new auto spawn
		registeredSpawns.put(newSpawn.getObjectId(), newSpawn);
		
		// Not all spawns must be activated immediately.
		if (isSpawn)
		{
			setSpawn(newSpawn, true);
		}
	}
	
	/**
	 * Sets the active state of the specified spawn.
	 * @param spawns
	 * @param isActive
	 */
	public void setSpawn(Map<Integer, AutoSpawnHolder> spawns, boolean isActive)
	{
		for (AutoSpawnHolder spawn : spawns.values())
		{
			setSpawn(spawn, isActive);
		}
	}
	
	/**
	 * Sets the active state of the specified spawn.
	 * @param spawn
	 * @param isActive
	 */
	public void setSpawn(AutoSpawnHolder spawn, boolean isActive)
	{
		if (spawn == null)
		{
			return;
		}
		
		int objectId = spawn.getObjectId();
		
		if (!isSpawnRegistered(objectId))
		{
			return;
		}
		
		ScheduledFuture<?> spawnTask = null;
		
		if (isActive)
		{
			if (isSpawnActive(objectId))
			{
				return;
			}
			
			if (spawn.getDespawnDelay() > 0)
			{
				spawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TaskSpawn(objectId), spawn.getInitialDelay(), spawn.getRespawnDelay());
			}
			else
			{
				spawnTask = ThreadPoolManager.getInstance().schedule(new TaskSpawn(objectId), spawn.getInitialDelay());
			}
			runningSpawns.put(objectId, spawnTask);
		}
		else
		{
			spawnTask = runningSpawns.remove(objectId);
			
			if (spawnTask != null)
			{
				spawnTask.cancel(false);
				spawnTask = null;
				ThreadPoolManager.getInstance().schedule(new TaskDespawn(objectId), 0);
			}
		}
	}
	
	/**
	 * Returns the number of milliseconds until the next occurrence of the given spawn.
	 * @param  spawn
	 * @return
	 */
	public long getTimeToNextSpawn(AutoSpawnHolder spawn)
	{
		int objectId = spawn.getObjectId();
		
		if (!isSpawnRegistered(objectId))
		{
			return -1;
		}
		
		return runningSpawns.get(objectId).getDelay(TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Attempts to return the AutoSpawnHolder associated with the given NPC or Object ID type. Note: If isObjectId == false, returns first instance for the specified NPC ID.
	 * @param  id
	 * @param  isObjectId
	 * @return            AutoSpawnHolder
	 */
	public AutoSpawnHolder getSpawns(int id, boolean isObjectId)
	{
		if (isObjectId)
		{
			if (isSpawnRegistered(id))
			{
				return registeredSpawns.get(id);
			}
		}
		else
		{
			for (AutoSpawnHolder spawn : registeredSpawns.values())
			{
				if (spawn.getNpcId() == id)
				{
					return spawn;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Attempts to return the Map of AutoSpawnHolder associated with the given npc id.
	 * @param  npcId
	 * @return
	 */
	public Map<Integer, AutoSpawnHolder> getSpawns(int npcId)
	{
		Map<Integer, AutoSpawnHolder> spawnList = new HashMap<>();
		
		for (AutoSpawnHolder spawn : registeredSpawns.values())
		{
			if (spawn.getNpcId() == npcId)
			{
				spawnList.put(spawn.getObjectId(), spawn);
			}
		}
		
		return spawnList;
	}
	
	/**
	 * Is checked if the spawn is already being executed.
	 * @param  objectId
	 * @return
	 */
	public boolean isSpawnActive(int objectId)
	{
		return runningSpawns.containsKey(objectId);
	}
	
	/**
	 * If the specified object ID is assigned to an auto spawn.
	 * @param  objectId
	 * @return          boolean isAssigned
	 */
	public boolean isSpawnRegistered(int objectId)
	{
		return registeredSpawns.containsKey(objectId);
	}
	
	// Runnable class --------------------------------------------------------------------
	public class TaskDespawn implements Runnable
	{
		private final int objectId;
		
		public TaskDespawn(int objectId)
		{
			this.objectId = objectId;
		}
		
		@Override
		public void run()
		{
			try
			{
				AutoSpawnHolder spawn = getSpawns(objectId, true);
				
				for (L2Npc npcInst : spawn.getNpcInstanceList())
				{
					npcInst.deleteMe();
					spawn.removeNpcInstance(npcInst);
				}
			}
			catch (Exception e)
			{
				LOG.warning(getClass().getSimpleName() + ": An error occurred while despawning spawn (Object ID = " + objectId + "): " + e);
			}
		}
	}
	
	public class TaskSpawn implements Runnable
	{
		private final int objectId;
		
		public TaskSpawn(int objectId)
		{
			this.objectId = objectId;
		}
		
		@Override
		public void run()
		{
			try
			{
				// If the spawn is not scheduled to be active, cancel the spawn task.
				if (!isSpawnActive(objectId))
				{
					return;
				}
				
				// Retrieve the required spawn instance for this spawn task.
				AutoSpawnHolder spawn = getSpawns(objectId, true);
				
				List<LocationHolder> locationList = spawn.getLocationList();
				
				int locationCount = locationList.size();
				int locationIndex = 0;
				
				if (spawn.isRandomSpawn())
				{
					locationIndex = Rnd.nextInt(locationCount);
				}
				else
				{
					// If random spawning is disabled, the spawn at the next set of co-ordinates after the last.
					// If the index is greater than the number of possible spawns, reset the counter to zero.
					locationIndex = spawn.getLastLocIndex();
					locationIndex++;
					
					if (locationIndex == locationCount)
					{
						locationIndex = 0;
					}
				}
				
				spawn.setLastLocIndex(locationIndex);
				
				// Set the X, Y and Z co-ordinates, where this spawn will take place.
				final int x = locationList.get(locationIndex).getX();
				final int y = locationList.get(locationIndex).getY();
				final int z = locationList.get(locationIndex).getZ();
				final int heading = locationList.get(locationIndex).getHeading();
				
				// Fetch the template for this NPC ID and create a new spawn.
				NpcTemplate npcTemp = NpcData.getInstance().getTemplate(spawn.getNpcId());
				if (npcTemp == null)
				{
					LOG.warning("Couldnt find NPC id" + spawn.getNpcId() + " Try to update your DP");
					return;
				}
				
				Spawn newSpawn = new Spawn(npcTemp);
				
				newSpawn.setX(x);
				newSpawn.setY(y);
				newSpawn.setZ(z);
				if (heading != -1)
				{
					newSpawn.setHeading(heading);
				}
				newSpawn.setAmount(1);
				if (spawn.getDespawnDelay() == 0)
				{
					newSpawn.setRespawnDelay(spawn.getRespawnDelay());
				}
				
				// Add the new spawn information to the spawn table, but do not store it.
				SpawnData.getInstance().addNewSpawn(newSpawn, false);
				
				L2Npc npcInst = newSpawn.doSpawn();
				npcInst.setXYZ(npcInst.getX(), npcInst.getY(), npcInst.getZ());
				
				spawn.addNpcInstance(npcInst);
				
				// Announce to all players that the spawn has taken place, with the nearest town location.
				if (spawn.isBroadcasting())
				{
					String nearestTown = MapRegionData.getInstance().getClosestTownName(npcInst);
					AnnouncementsData.getInstance().announceToAll("The " + npcInst.getName() + " has spawned near " + nearestTown + "!");
				}
				
				// If there is no despawn time, do not create a despawn task.
				if (spawn.getDespawnDelay() > 0)
				{
					ThreadPoolManager.getInstance().schedule(new TaskDespawn(objectId), spawn.getDespawnDelay() - 1000);
				}
			}
			catch (Exception e)
			{
				LOG.warning(getClass().getSimpleName() + ": An error occurred while initializing spawn instance (Object ID = " + objectId + "): " + e);
				e.printStackTrace();
			}
		}
	}
	
	public static AutoSpawnManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public static class SingletonHolder
	{
		protected static final AutoSpawnManager INSTANCE = new AutoSpawnManager();
	}
}
