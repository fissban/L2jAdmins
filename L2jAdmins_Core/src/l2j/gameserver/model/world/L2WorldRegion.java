package l2j.gameserver.model.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.ai.AttackableAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.model.zone.Zone;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:33 $
 */
public final class L2WorldRegion
{
	private static final Logger LOG = Logger.getLogger(L2WorldRegion.class.getName());
	
	/** ConcurrentHashMap(L2PlayableInstance) containing L2PlayableInstance of all player & summon in game in this L2WorldRegion */
	private final Map<Integer, L2Playable> allPlayable = new ConcurrentHashMap<>();
	
	/** ConcurrentHashMap(L2Object) containing L2Object visible in this L2WorldRegion */
	private final Map<Integer, L2Object> visibleObjects = new ConcurrentHashMap<>();
	
	private final List<L2WorldRegion> surroundingRegions = new ArrayList<>();
	private final int tileX, tileY;
	private Boolean active = false;
	private ScheduledFuture<?> neighborsTask = null;
	
	private final List<Zone> zones = new ArrayList<>();
	
	public L2WorldRegion(int pTileX, int pTileY)
	{
		tileX = pTileX;
		tileY = pTileY;
		
		// default a newly initialized region to inactive, unless always on is specified
		active = Config.GRIDS_ALWAYS_ON;
	}
	
	public List<Zone> getZones()
	{
		return zones;
	}
	
	public void addZone(Zone zone)
	{
		zones.add(zone);
	}
	
	public void removeZone(Zone zone)
	{
		zones.remove(zone);
	}
	
	public void revalidateZones(L2Character character)
	{
		// Do NOT update the world region while the character is still in the process of teleporting
		if (character.isTeleporting())
		{
			return;
		}
		getZones().stream().filter(z -> z != null).forEach(z -> z.revalidateInZone(character));
	}
	
	public void removeFromZones(L2Character character)
	{
		getZones().stream().filter(z -> z != null).forEach(z -> z.removeCharacter(character));
	}
	
	/** Task of AI notification */
	public class NeighborsTask implements Runnable
	{
		private final boolean isActivating;
		
		public NeighborsTask(boolean isActivating)
		{
			this.isActivating = isActivating;
		}
		
		@Override
		public void run()
		{
			if (isActivating)
			{
				// for each neighbor, if it's not active, activate.
				for (L2WorldRegion neighbor : getSurroundingRegions())
				{
					neighbor.setActive(true);
				}
			}
			else
			{
				if (areNeighborsEmpty())
				{
					setActive(false);
				}
				
				// check and deactivate
				for (L2WorldRegion neighbor : getSurroundingRegions())
				{
					if (neighbor.areNeighborsEmpty())
					{
						neighbor.setActive(false);
					}
				}
			}
		}
	}
	
	private void switchAI(Boolean isOn)
	{
		int c = 0;
		if (!isOn)
		{
			for (L2Object o : visibleObjects.values())
			{
				if (o instanceof L2Attackable)
				{
					c++;
					L2Attackable mob = (L2Attackable) o;
					
					// Set target to null and cancel Attack or Cast
					mob.setTarget(null);
					
					// Stop movement
					mob.stopMove(null);
					
					// Stop all active skills effects in progress on the L2Character
					mob.stopAllEffects();
					
					mob.clearAggroList();
					mob.getAttackByList().clear();
					mob.getKnownList().removeAllObjects();
					
					if (mob.hasAI())
					{
						mob.getAI().setIntention(CtrlIntentionType.IDLE);
						((AttackableAI) mob.getAI()).stopAITask();
					}
				}
			}
			LOG.fine(c + " mobs were turned off");
		}
		else
		{
			for (L2Object o : visibleObjects.values())
			{
				if (o instanceof L2Attackable)
				{
					c++;
					// Start HP/MP/CP Regeneration task
					((L2Attackable) o).getStatus().startHpMpRegeneration();
				}
				else if (o instanceof L2Npc)
				{
					// Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it
					// L2Monsterinstance/L2Attackable socials are handled by AI (TODO: check the instances)
					((L2Npc) o).startRandomAnimationTimer();
				}
			}
			
			LOG.fine(c + " mobs were turned on");
		}
	}
	
	public Boolean isActive()
	{
		return active;
	}
	
	// check if all 9 neighbors (including self) are inactive or active but with no players.
	// returns true if the above condition is met.
	public Boolean areNeighborsEmpty()
	{
		// if this region is occupied, return false.
		if (isActive() && (!allPlayable.isEmpty()))
		{
			return false;
		}
		
		// if any one of the neighbors is occupied, return false
		for (L2WorldRegion neighbor : surroundingRegions)
		{
			if (neighbor.isActive() && (!neighbor.allPlayable.isEmpty()))
			{
				return false;
			}
		}
		
		// in all other cases, return true.
		return true;
	}
	
	/**
	 * this function turns this region's AI and geodata on or off
	 * @param value
	 */
	public void setActive(Boolean value)
	{
		if (active == value)
		{
			return;
		}
		
		active = value;
		
		// turn the AI on or off to match the region's activation.
		switchAI(value);
		
		// TODO
		// turn the geodata on or off to match the region's activation.
		if (value)
		{
			LOG.fine("Starting Grid " + tileX + "," + tileY);
		}
		else
		{
			LOG.fine("Stoping Grid " + tileX + "," + tileY);
		}
	}
	
	/**
	 * Immediately sets self as active and starts a timer to set neighbors as active this timer is to avoid turning on neighbors in the case when a person just teleported into a region and then teleported out immediately...there is no reason to activate all the neighbors in that case.
	 */
	private void startActivation()
	{
		// first set self to active and do self-tasks...
		setActive(true);
		
		// if the timer to deactivate neighbors is running, cancel it.
		synchronized (this)
		{
			if (neighborsTask != null)
			{
				neighborsTask.cancel(true);
				neighborsTask = null;
			}
			
			// then, set a timer to activate the neighbors
			neighborsTask = ThreadPoolManager.getInstance().schedule(new NeighborsTask(true), 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
		}
	}
	
	/**
	 * starts a timer to set neighbors (including self) as inactive this timer is to avoid turning off neighbors in the case when a person just moved out of a region that he may very soon return to. There is no reason to turn self & neighbors off in that case.
	 */
	private void startDeactivation()
	{
		// if the timer to activate neighbors is running, cancel it.
		synchronized (this)
		{
			if (neighborsTask != null)
			{
				neighborsTask.cancel(true);
				neighborsTask = null;
			}
			
			// start a timer to "suggest" a deactivate to self and neighbors.
			// suggest means: first check if a neighbor has L2PcInstances in it. If not, deactivate.
			neighborsTask = ThreadPoolManager.getInstance().schedule(new NeighborsTask(false), 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
		}
	}
	
	/**
	 * Add the L2Object in the L2ObjectHashSet(L2Object) visibleObjects containing L2Object visible in this L2WorldRegion <BR>
	 * If L2Object is a L2PcInstance, Add the L2PcInstance in the L2ObjectHashSet(L2PcInstance) allPlayable containing L2PcInstance of all player in game in this L2WorldRegion <BR>
	 * Assert : object.getCurrentWorldRegion() == this
	 * @param object
	 */
	public void addVisibleObject(L2Object object)
	{
		if (object == null)
		{
			return;
		}
		
		assert object.getWorldRegion() == this;
		
		visibleObjects.put(object.getObjectId(), object);
		
		if (object instanceof L2Playable)
		{
			allPlayable.put(object.getObjectId(), (L2Playable) object);
			
			// if this is the first player to enter the region, activate self & neighbors
			if ((allPlayable.size() == 1) && (!Config.GRIDS_ALWAYS_ON))
			{
				startActivation();
			}
		}
	}
	
	/**
	 * Remove the L2Object from the L2ObjectHashSet(L2Object) visibleObjects in this L2WorldRegion <BR>
	 * If L2Object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) allPlayable of this L2WorldRegion <BR>
	 * Assert : object.getCurrentWorldRegion() == this || object.getCurrentWorldRegion() == null
	 * @param object
	 */
	public void removeVisibleObject(L2Object object)
	{
		if (object == null)
		{
			return;
		}
		
		assert (object.getWorldRegion() == this) || (object.getWorldRegion() == null);
		
		visibleObjects.remove(object.getObjectId());
		
		if (object instanceof L2Playable)
		{
			allPlayable.remove(object.getObjectId());
			
			if ((allPlayable.isEmpty()) && (!Config.GRIDS_ALWAYS_ON))
			{
				startDeactivation();
			}
		}
	}
	
	public void addSurroundingRegion(L2WorldRegion region)
	{
		surroundingRegions.add(region);
	}
	
	/**
	 * Return the List surroundingRegions containing all L2WorldRegion around the current L2WorldRegion
	 * @return
	 */
	public List<L2WorldRegion> getSurroundingRegions()
	{
		// change to return L2WorldRegion[] ?
		// this should not change after initialization, so maybe changes are not necessary
		
		return surroundingRegions;
	}
	
	public Map<Integer, L2Object> getVisibleObjects()
	{
		return visibleObjects;
	}
	
	public Map<Integer, L2Playable> getVisiblePlayable()
	{
		return allPlayable;
	}
	
	public String getName()
	{
		return "(" + tileX + ", " + tileY + ")";
	}
	
	/**
	 * Deleted all spawns in the world.
	 */
	public synchronized void deleteVisibleNpcSpawns()
	{
		for (L2Object obj : visibleObjects.values())
		{
			if (obj instanceof L2Npc)
			{
				L2Npc target = (L2Npc) obj;
				target.deleteMe();
				Spawn spawn = target.getSpawn();
				if (spawn != null)
				{
					spawn.stopRespawn();
					SpawnData.getInstance().deleteSpawn(spawn, false);
				}
				LOG.finest("Removed NPC " + target.getObjectId());
			}
		}
		LOG.info("All visible NPC's deleted in Region: " + getName());
	}
}
