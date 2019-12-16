/*
 * 
 */
package l2j.gameserver.model.spawn;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.util.Rnd;
import main.EngineModsManager;

/**
 * This class manages the spawn and respawn of a group of L2Npc that are in the same are and have the same type. <B><U> Concept</U> :</B><BR>
 * L2Npc can be spawned either in a random position into a location area (if Lox=0 and Locy=0), either at an exact position. The heading of the L2Npc can be a random heading if not defined (value= -1) or an exact heading (ex : merchant...).<BR>
 * @author  Nightmare
 * @version $Revision: 1.9.2.3.2.8 $ $Date: 2005/03/27 15:29:32 $
 */
public class Spawn
{
	protected static final Logger LOG = Logger.getLogger(Spawn.class.getName());
	
	/** The link on the L2NpcTemplate object containing generic and static properties of this spawn (ex : RewardExp, RewardSP, AggroRange...) */
	private final NpcTemplate template;
	/** The identifier of the location area where L2Npc can be spawned */
	private int location;
	/** The maximum number of L2Npc that can manage this Spawn */
	private int maximumCount;
	/** The current number of L2Npc managed by this Spawn */
	private int currentCount;
	/** The current number of SpawnTask in progress or stand by of this Spawn */
	protected int scheduledCount;
	/** The X position of the spawn point */
	private int locX;
	/** The Y position of the spawn point */
	private int locY;
	/** The Z position of the spawn point */
	private int locZ;
	/** The heading of L2Npc when they are spawned */
	private int heading;
	/** The delay between a L2Npc remove and its re-spawn */
	private int respawnDelay;
	/** Minimum delay RaidBoss */
	private int respawnMinDelay;
	/** Maximum delay RaidBoss */
	private int respawnMaxDelay;
	/** The generic constructor of L2Npc managed by this Spawn */
	private Constructor<?> constructor;
	/** If True a L2Npc is respawned each time that another is killed */
	boolean doRespawn;
	/** If true then spawn is custom */
	private boolean customSpawn;
	private L2Npc lastSpawn;
	
	/** The task launching the function doSpawn() */
	public class SpawnTask implements Runnable
	{
		L2Npc oldNpc;
		
		public SpawnTask(L2Npc pOldNpc)
		{
			oldNpc = pOldNpc;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (doRespawn)
				{
					respawnNpc(oldNpc);
				}
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "", e);
			}
			
			scheduledCount--;
		}
	}
	
	/**
	 * Constructor of Spawn.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * Each Spawn owns generic and static properties (ex : RewardExp, RewardSP, AggroRange...). All of those properties are stored in a different L2NpcTemplate for each type of Spawn. Each template is loaded once in the server cache memory (reduce memory use). When a new instance of Spawn is
	 * created, server just create a link between the instance and the template. This link is stored in <B>_template</B><BR>
	 * Each L2Npc is linked to a Spawn that manages its spawn and respawn (delay, location...). This link is stored in <B>_spawn</B> of the L2Npc<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Set the template of the Spawn</li>
	 * <li>Calculate the implementationName used to generate the generic constructor of L2Npc managed by this Spawn</li>
	 * <li>Create the generic constructor of L2Npc managed by this Spawn</li>
	 * @param  mobTemplate            The L2NpcTemplate to link to this Spawn
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	public Spawn(NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException, Exception
	{
		template = mobTemplate;
		
		// Create the generic constructor of NpcInstance managed by this Spawn
		Class<?>[] parameters =
		{
			int.class,
			Class.forName("l2j.gameserver.model.actor.manager.character.templates.NpcTemplate")
		};
		
		constructor = Class.forName("l2j.gameserver.model.actor.instance." + template.getType() + "Instance").getConstructor(parameters);
	}
	
	/**
	 * @return the maximum number of L2Npc that this Spawn can manage.
	 */
	public int getAmount()
	{
		return maximumCount;
	}
	
	/**
	 * @return the Identifier of the location area where L2Npc can be spawned.
	 */
	public int getSpawnLocation()
	{
		return location;
	}
	
	/**
	 * Set the Identifier of the location area where L2Npc can be spawned.
	 * @param location
	 */
	public void setSpawnLocation(int location)
	{
		this.location = location;
	}
	
	/**
	 * @return the X position of the spawn point.
	 */
	public int getX()
	{
		return locX;
	}
	
	/**
	 * @return the Y position of the spawn point.
	 */
	public int getY()
	{
		return locY;
	}
	
	/**
	 * @return the Z position of the spawn point.
	 */
	public int getZ()
	{
		return locZ;
	}
	
	/**
	 * @return the Identifier of the L2Npc manage by this L2Spwan contained in the L2NpcTemplate.
	 */
	public int getNpcId()
	{
		return template.getId();
	}
	
	/**
	 * @return the heading of L2Npc when they are spawned.
	 */
	public int getHeading()
	{
		return heading;
	}
	
	/**
	 * @return the delay between a L2Npc remove and its re-spawn.
	 */
	public int getRespawnDelay()
	{
		return respawnDelay;
	}
	
	/**
	 * @return Min RaidBoss Spawn delay.
	 */
	public int getRespawnMinDelay()
	{
		return respawnMinDelay;
	}
	
	/**
	 * @return Max RaidBoss Spawn delay.
	 */
	public int getRespawnMaxDelay()
	{
		return respawnMaxDelay;
	}
	
	/**
	 * Set the maximum number of L2Npc that this Spawn can manage.
	 * @param amount
	 */
	public void setAmount(int amount)
	{
		maximumCount = amount;
	}
	
	/**
	 * Set Minimum Respawn Delay.
	 * @param delay
	 */
	public void setRespawnMinDelay(int delay)
	{
		respawnMinDelay = delay;
	}
	
	/**
	 * Set Maximum Respawn Delay.
	 * @param delay
	 */
	public void setRespawnMaxDelay(int delay)
	{
		respawnMaxDelay = delay;
	}
	
	/**
	 * Set the X position of the spawn point.
	 * @param x
	 */
	public void setX(int x)
	{
		locX = x;
	}
	
	/**
	 * Set the Y position of the spawn point.
	 * @param y
	 */
	public void setY(int y)
	{
		locY = y;
	}
	
	/**
	 * Set the Z position of the spawn point.
	 * @param z
	 */
	public void setZ(int z)
	{
		locZ = z;
	}
	
	/**
	 * Set the heading of L2Npc when they are spawned.
	 * @param heading
	 */
	public void setHeading(int heading)
	{
		this.heading = heading;
	}
	
	/**
	 * Set the spawn as custom.
	 * @param custom
	 */
	public void setCustom(boolean custom)
	{
		customSpawn = custom;
	}
	
	/**
	 * Return type of spawn.
	 * @return
	 */
	public boolean isCustom()
	{
		return customSpawn;
	}
	
	/**
	 * Decrease the current number of L2Npc of this Spawn and if necessary create a SpawnTask to launch after the respawn Delay.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Decrease the current number of L2Npc of this Spawn</li>
	 * <li>Check if respawn is possible to prevent multiple respawning caused by lag</li>
	 * <li>Update the current number of SpawnTask in progress or stand by of this Spawn</li>
	 * <li>Create a new SpawnTask to launch after the respawn Delay</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : A respawn is possible ONLY if doRespawn=True and scheduledCount + currentCount < maximumCount</B></FONT><BR>
	 * @param oldNpc
	 */
	public void decreaseCount(L2Npc oldNpc)
	{
		// Sanity check
		if (currentCount <= 0)
		{
			return;
		}
		// Decrease the current number of L2Npc of this Spawn
		currentCount--;
		
		// Check if respawn is possible to prevent multiple respawning caused by lag
		if (doRespawn && ((scheduledCount + currentCount) < maximumCount))
		{
			// Update the current number of SpawnTask in progress or stand by of this Spawn
			scheduledCount++;
			
			// Create a new SpawnTask to launch after the respawn Delay
			ThreadPoolManager.schedule(new SpawnTask(oldNpc), respawnDelay);
		}
	}
	
	/**
	 * Create the initial spawning and set doRespawn to True.
	 */
	public void init()
	{
		while (currentCount < maximumCount)
		{
			doSpawn();
		}
		doRespawn = true;
	}
	
	/**
	 * Set doRespawn to False to stop respawn in this Spawn.
	 */
	public void stopRespawn()
	{
		doRespawn = false;
	}
	
	/**
	 * Set doRespawn to True to start or restart respawn in this Spawn.
	 */
	public void startRespawn()
	{
		doRespawn = true;
	}
	
	/**
	 * Create the L2Npc, add it to the world and lauch its onSpawn action.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2Npc can be spawned either in a random position into a location area (if Lox=0 and Locy=0), either at an exact position. The heading of the L2Npc can be a random heading if not defined (value= -1) or an exact heading (ex : merchant...).<BR>
	 * <B><U> Actions for an random spawn into location area</U> : <I>(if locX=0 and locY=0)</I></B><BR>
	 * <li>Get L2Npc Init parameters and its generate an Identifier</li>
	 * <li>Call the constructor of the L2Npc</li>
	 * <li>Calculate the random position in the location area (if locX=0 and locY=0) or get its exact position from the Spawn</li>
	 * <li>Set the position of the L2Npc</li>
	 * <li>Set the HP and MP of the L2Npc to the max</li>
	 * <li>Set the heading of the L2Npc (random heading if not defined : value=-1)</li>
	 * <li>Link the L2Npc to this Spawn</li>
	 * <li>Init other values of the L2Npc (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world</li>
	 * <li>Launch the action onSpawn for the L2Npc</li>
	 * <li>Increase the current number of L2Npc managed by this Spawn</li>
	 * @return
	 */
	public L2Npc doSpawn()
	{
		try
		{
			// Check if the Spawn is not a L2Pet, L2BabyPet, L2SiegeSummon or L2Minion spawn
			if (template.isType("L2Pet") || template.isType("L2BabyPet") || template.isType("L2SiegeSummon") || template.isType("L2Minion"))
			{
				currentCount++;
				return null;
			}
			
			// Get L2Npc Init parameters and its generate an Identifier
			Object[] parameters =
			{
				IdFactory.getInstance().getNextId(),
				template
			};
			
			// Call the constructor of the L2Npc
			// (can be a L2ArtefactInstance, L2FriendlyMobInstance, L2GuardInstance, L2MonsterInstance, L2SiegeGuardInstance, L2BoxInstance or L2FolkInstance)
			Object tmp = constructor.newInstance(parameters);
			
			// Check if the Instance is a L2Npc
			if (!(tmp instanceof L2Npc))
			{
				return null;
			}
			
			return initializeNpcInstance((L2Npc) tmp);
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "NPC " + getNpcId() + " class not found", e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Create a respawn task to be launched after the fixed + random delay. Respawn is only possible when respawn enabled.
	 */
	public void doRespawn()
	{
		// Schedule respawn of the NPC
		ThreadPoolManager.schedule(() -> init(), getRespawnDelay());
	}
	
	/**
	 * @param  mob
	 * @return
	 */
	public L2Npc initializeNpcInstance(L2Npc mob)
	{
		// If locX=0 or locY=0, there's a problem.
		if ((getX() == 0) || (getY() == 0))
		{
			LOG.warning("Spawn : the following npcID: " + getNpcId() + " misses X/Y informations.");
			return mob;
		}
		
		// The L2Npc is spawned at the exact position (x, y, z)
		int newLocX = getX();
		int newLocY = getY();
		int newLocZ = GeoEngine.getInstance().getHeight(newLocX, newLocY, getZ());
		
		// Temporarily fix: when the spawn Z and geo Z differs more than 200, use spawn Z coord
		if (Math.abs(newLocZ - getZ()) > 200)
		{
			newLocZ = getZ();
		}
		
		// Reset all effects
		mob.stopAllEffects();
		// Not dead
		mob.setIsDead(false);
		
		// Set the HP and MP of the L2Npc to the max
		mob.setCurrentHpMp(mob.getStat().getMaxHp(), mob.getStat().getMaxMp());
		
		// Set the heading of the L2Npc (random heading if not defined)
		if (getHeading() == -1)
		{
			mob.setHeading(Rnd.nextInt(61794));
		}
		else
		{
			mob.setHeading(getHeading());
		}
		
		// Link the L2Npc to this Spawn
		mob.setSpawn(this);
		
		EngineModsManager.onSpawn(mob);
		
		// Init other values of the L2Npc (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
		mob.spawnMe(newLocX, newLocY, newLocZ);
		
		lastSpawn = mob;
		
		// Increase the current number of L2Npc managed by this Spawn
		currentCount++;
		return mob;
	}
	
	/**
	 * @param i delay in seconds
	 */
	public void setRespawnDelay(int i)
	{
		if (i < 0)
		{
			LOG.warning("respawn delay is negative for spawnId:" + getNpcId());
		}
		
		if (i < 60)
		{
			i = 60;
		}
		
		respawnDelay = i * 1000;
	}
	
	public L2Npc getLastSpawn()
	{
		return lastSpawn;
	}
	
	/**
	 * @param oldNpc
	 */
	public void respawnNpc(L2Npc oldNpc)
	{
		oldNpc.refreshID();
		initializeNpcInstance(oldNpc);
	}
	
	public NpcTemplate getTemplate()
	{
		return template;
	}
	
	public Constructor<?> getConstructor()
	{
		return constructor;
	}
	
	public void setConstructor(Constructor<?> constructor)
	{
		this.constructor = constructor;
	}
}
