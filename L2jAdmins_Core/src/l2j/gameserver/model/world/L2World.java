package l2j.gameserver.model.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.21.2.5.2.7 $ $Date: 2005/03/27 15:29:32 $
 */
public final class L2World
{
	private static final Logger LOG = Logger.getLogger(L2World.class.getName());
	
	// Geodata min/max tiles
	public static final int TILE_X_MIN = 16;
	public static final int TILE_X_MAX = 26;
	public static final int TILE_Y_MIN = 10;
	public static final int TILE_Y_MAX = 25;
	
	// Map dimensions
	public static final int TILE_SIZE = 32768;
	public static final int WORLD_X_MIN = (TILE_X_MIN - 20) * TILE_SIZE;
	public static final int WORLD_X_MAX = (TILE_X_MAX - 19) * TILE_SIZE;
	public static final int WORLD_Y_MIN = (TILE_Y_MIN - 18) * TILE_SIZE;
	public static final int WORLD_Y_MAX = (TILE_Y_MAX - 17) * TILE_SIZE;
	
	// Regions and offsets
	private static final int REGION_SIZE = 4096;
	private static final int REGIONS_X = (WORLD_X_MAX - WORLD_X_MIN) / REGION_SIZE;
	private static final int REGIONS_Y = (WORLD_Y_MAX - WORLD_Y_MIN) / REGION_SIZE;
	private static final int REGION_X_OFFSET = Math.abs(WORLD_X_MIN / REGION_SIZE);
	private static final int REGION_Y_OFFSET = Math.abs(WORLD_Y_MIN / REGION_SIZE);
	
	private final Map<Integer, L2PcInstance> players;
	private final Map<Integer, L2Object> objects;
	private final Map<Integer, L2PetInstance> pets;
	
	private L2WorldRegion[][] worldRegions;
	
	public L2World()
	{
		players = new ConcurrentHashMap<>();
		pets = new ConcurrentHashMap<>();
		objects = new ConcurrentHashMap<>();
		
		initRegions();
	}
	
	/**
	 * Add L2Object object in allObjects.<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Withdraw an item from the warehouse, create an item</li>
	 * <li>Spawn a L2Character (PC, NPC, Pet)</li>
	 * @param object
	 */
	public void addObject(L2Object object)
	{
		objects.put(object.getObjectId(), object);
	}
	
	/**
	 * Remove L2Object object from allObjects of L2World.<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Delete item from inventory, transfer Item from inventory to warehouse</li>
	 * <li>Crystallize item</li>
	 * <li>Remove NPC/PC/Pet from the world</li>
	 * @param object L2Object to remove from allObjects of L2World
	 */
	public void removeObject(L2Object object)
	{
		objects.remove(object.getObjectId());
	}
	
	/**
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Client packets : Action, AttackRequest, RequestJoinParty, RequestJoinPledge...</li>
	 * @param  oID Identifier of the L2Object
	 * @return     the L2Object object that belongs to an ID or null if no object found.
	 */
	public L2Object getObject(int oID)
	{
		return objects.get(oID);
	}
	
	/**
	 * Allows easy retrieval of all <b>visible</b> objects in world.
	 * @return
	 */
	public final Collection<L2Object> getAllObjects()
	{
		return objects.values();
	}
	
	/**
	 * Get the count of all visible objects in world.<br>
	 * @return count off all L2World objects
	 */
	public final int getAllVisibleObjectsCount()
	{
		return objects.size();
	}
	
	/**
	 * @return a table containing all GMs.
	 */
	public Collection<L2PcInstance> getAllGMs()
	{
		return GmListData.getInstance().getAllGms();
	}
	
	/**
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Read-only, please! </B></FONT>
	 * @return a collection containing all players in game.
	 */
	public Collection<L2PcInstance> getAllPlayers()
	{
		return players.values();
	}
	
	/**
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Read-only, please! </B></FONT>
	 * @return a Array containing all players in game.
	 */
	public final L2PcInstance[] getAllPlayersArray()
	{
		return players.values().toArray(new L2PcInstance[players.size()]);
	}
	
	/**
	 * @param  objectId objectId of the player to get Instance
	 * @return          the player instance corresponding to the given name
	 */
	public L2PcInstance getPlayer(int objectId)
	{
		return players.get(objectId);
	}
	
	/**
	 * @param  name Name of the player to get Instance
	 * @return      the player instance corresponding to the given name
	 */
	public L2PcInstance getPlayer(String name)
	{
		if (name == null)
		{
			return null;
		}
		
		return players.get(CharNameData.getInstance().getIdByName(name));
	}
	
	/**
	 * Remove the L2PcInstance from allPlayers of L2World.<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Remove a player fom the visible objects</li>
	 * @param cha
	 */
	public void removePlayer(L2PcInstance cha)
	{
		players.remove(cha.getObjectId());
	}
	
	/**
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Read-only, please! </B></FONT>
	 * @return a collection containing all pets in game.
	 */
	public Collection<L2PetInstance> getAllPets()
	{
		return pets.values();
	}
	
	/**
	 * @param  ownerId ID of the owner
	 * @return         the pet instance from the given ownerId.
	 */
	public L2PetInstance getPet(int ownerId)
	{
		return pets.get(ownerId);
	}
	
	/**
	 * Add the given pet instance from the given ownerId.
	 * @param  ownerId ID of the owner
	 * @param  pet     L2PetInstance of the pet
	 * @return
	 */
	public L2PetInstance addPet(int ownerId, L2PetInstance pet)
	{
		return pets.put(ownerId, pet);
	}
	
	/**
	 * Remove the given pet instance.
	 * @param ownerId ID of the owner
	 */
	public void removePet(int ownerId)
	{
		pets.remove(ownerId);
	}
	
	/**
	 * Add a L2Object in the world.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2Object (including L2PcInstance) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
	 * L2PcInstance are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Add the L2Object object in allPlayers* of L2World</li>
	 * <li>Add the L2Object object in gmList** of GmListTable</li>
	 * <li>Add object in knownObjects and knownPlayer* of all surrounding L2WorldRegion L2Characters</li>
	 * <li>If object is a L2Character, add all surrounding L2Object in its knownObjects and all surrounding L2PcInstance in its knownPlayer</li> <I>* only if object is a L2PcInstance</I><BR>
	 * <I>** only if object is a GM L2PcInstance</I><BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object in visibleObjects and allPlayers* of L2WorldRegion (need synchronisation)</B></FONT><BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to allObjects and allPlayers* of L2World (need synchronisation)</B></FONT><BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Drop an Item</li>
	 * <li>Spawn a L2Character</li>
	 * <li>Apply Death Penalty of a L2PcInstance</li>
	 * @param object    L2object to add in the world
	 * @param newRegion L2WorldRegion in wich the object will be add (not used)
	 */
	public void addVisibleObject(L2Object object, L2WorldRegion newRegion)
	{
		// If selected L2Object is a L2PcIntance, add it in L2ObjectHashSet(L2PcInstance) allPlayers of L2World
		// XXX TODO: this code should be obsoleted by protection in putObject func...
		if (object instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) object;
			
			if (!player.isTeleporting())
			{
				L2PcInstance tmp = players.get(player.getObjectId());
				if (tmp != null)
				{
					LOG.warning("Duplicate character!? Closing both characters (" + player.getName() + ")");
					player.closeConnection();
					tmp.closeConnection();
					return;
				}
				players.put(player.getObjectId(), player);
			}
		}
		
		if (!newRegion.isActive())
		{
			return;
		}
		
		// tell the player about the surroundings
		// Go through the visible objects contained in the circular area
		for (L2Object visible : getVisibleObjects(object, 2000))
		{
			if (visible == null)
			{
				continue;
			}
			// Add the object in L2ObjectHashSet(L2Object) knownObjects of the visible L2Character according to conditions :
			// - L2Character is visible
			// - object is not already known
			// - object is in the watch distance
			// If L2Object is a L2PcInstance, add L2Object in L2ObjectHashSet(L2PcInstance) knownPlayer of the visible L2Character
			
			if (visible.getKnownList() != null)
			{
				visible.getKnownList().addObject(object);
			}
			// Add the visible L2Object in L2ObjectHashSet(L2Object) knownObjects of the object according to conditions
			// If visible L2Object is a L2PcInstance, add visible L2Object in L2ObjectHashSet(L2PcInstance) knownPlayer of the object
			if (object.getKnownList() != null)
			{
				object.getKnownList().addObject(visible);
			}
		}
	}
	
	/**
	 * Remove a L2Object from the world.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * L2Object (including L2PcInstance) are identified in <B>_visibleObjects</B> of his current L2WorldRegion and in <B>_knownObjects</B> of other surrounding L2Characters <BR>
	 * L2PcInstance are identified in <B>_allPlayers</B> of L2World, in <B>_allPlayers</B> of his current L2WorldRegion and in <B>_knownPlayer</B> of other surrounding L2Characters <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Remove the L2Object object from allPlayers* of L2World</li>
	 * <li>Remove the L2Object object from visibleObjects and allPlayers* of L2WorldRegion</li>
	 * <li>Remove the L2Object object from gmList** of GmListTable</li>
	 * <li>Remove object from knownObjects and knownPlayer* of all surrounding L2WorldRegion L2Characters</li>
	 * <li>If object is a L2Character, remove all L2Object from its knownObjects and all L2PcInstance from its knownPlayer</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from allObjects of L2World</B></FONT><BR>
	 * <I>* only if object is a L2PcInstance</I><BR>
	 * <I>** only if object is a GM L2PcInstance</I><BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Pickup an Item</li>
	 * <li>Decay a L2Character</li>
	 * @param object    L2object to remove from the world
	 * @param oldRegion L2WorldRegion in wich the object was before removing
	 */
	public void removeVisibleObject(L2Object object, L2WorldRegion oldRegion)
	{
		if (object == null)
		{
			return;
		}
		
		if (oldRegion != null)
		{
			// Remove the object from the L2ObjectHashSet(L2Object) visibleObjects of L2WorldRegion
			// If object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) allPlayers of this L2WorldRegion
			oldRegion.removeVisibleObject(object);
			
			// Go through all surrounding L2WorldRegion L2Characters
			for (L2WorldRegion reg : oldRegion.getSurroundingRegions())
			{
				for (L2Object obj : reg.getVisibleObjects().values())
				{
					// Remove the L2Object from the L2ObjectHashSet(L2Object) knownObjects of the surrounding L2WorldRegion L2Characters
					// If object is a L2PcInstance, remove the L2Object from the L2ObjectHashSet(L2PcInstance) knownPlayer of the surrounding L2WorldRegion L2Characters
					// If object is targeted by one of the surrounding L2WorldRegion L2Characters, cancel ATTACK and cast
					if (obj.getKnownList() != null)
					{
						obj.getKnownList().removeObject(object);
					}
				}
			}
			
			// If object is a L2Character :
			// Remove all L2Object from ConcurrentHashMap(L2Object) containing all L2Object detected by the L2Character
			// Remove all L2PcInstance from ConcurrentHashMap(L2PcInstance) containing all player ingame detected by the L2Character
			if (object.getKnownList() != null)
			{
				object.getKnownList().removeAllObjects();
			}
			
			// If selected L2Object is a L2PcIntance, remove it from ConcurrentHashMap(L2PcInstance) allPlayers of L2World
			if (object instanceof L2PcInstance)
			{
				if (!((L2PcInstance) object).isTeleporting())
				{
					removePlayer((L2PcInstance) object);
				}
			}
		}
	}
	
	/**
	 * Return all visible objects of the L2WorldRegions in the circular area (radius) centered on the object.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All visible object are identified in <B>_visibleObjects</B> of their current L2WorldRegion <BR>
	 * All surrounding L2WorldRegion are identified in <B>_surroundingRegions</B> of the selected L2WorldRegion in order to scan a large area around a L2Object<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Define the aggrolist of monster</li>
	 * <li>Define visible objects of a L2Object</li>
	 * <li>Skill : Confusion...</li>
	 * @param  object L2object that determine the center of the circular area
	 * @param  radius Radius of the circular area
	 * @return
	 */
	public List<L2Object> getVisibleObjects(L2Object object, int radius)
	{
		if ((object == null) || !object.isVisible())
		{
			return Collections.emptyList();
		}
		
		int x = object.getX();
		int y = object.getY();
		int sqRadius = radius * radius;
		// Create an ArrayList in order to contain all visible L2Object
		List<L2Object> result = new ArrayList<>();
		
		// Go through the ArrayList of region
		for (L2WorldRegion region : object.getWorldRegion().getSurroundingRegions())
		{
			// Go through visible objects of the selected region
			for (L2Object o : region.getVisibleObjects().values())
			{
				if ((o == null) || o.equals(o))
				{
					continue; // skip null or our own character
				}
				
				int x1 = o.getX();
				int y1 = o.getY();
				
				double dx = x1 - x;
				double dy = y1 - y;
				
				// If the visible object is inside the circular area add the object to the ArrayList result
				if (((dx * dx) + (dy * dy)) < sqRadius)
				{
					result.add(o);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Return all visible objects of the L2WorldRegions in the spheric area (radius) centered on the object.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All visible object are identified in <B>_visibleObjects</B> of their current L2WorldRegion <BR>
	 * All surrounding L2WorldRegion are identified in <B>_surroundingRegions</B> of the selected L2WorldRegion in order to scan a large area around a L2Object<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Define the target list of a skill</li>
	 * <li>Define the target list of a polearme attack</li>
	 * @param  object L2object that determine the center of the circular area
	 * @param  radius Radius of the spheric area
	 * @return
	 */
	public List<L2Object> getVisibleObjects3D(L2Object object, int radius)
	{
		if ((object == null) || !object.isVisible())
		{
			return Collections.emptyList();
		}
		
		int x = object.getX();
		int y = object.getY();
		int z = object.getZ();
		int sqRadius = radius * radius;
		
		// Create an ArrayList in order to contain all visible L2Object
		List<L2Object> result = new ArrayList<>();
		
		// Go through visible object of the selected region
		for (L2WorldRegion region : object.getWorldRegion().getSurroundingRegions())
		{
			for (L2Object o : region.getVisibleObjects().values())
			{
				if (o.equals(o))
				{
					continue; // skip our own character
				}
				
				int x1 = o.getX();
				int y1 = o.getY();
				int z1 = o.getZ();
				
				long dx = x1 - x;
				// if (dx > radius || -dx > radius)
				// continue;
				long dy = y1 - y;
				// if (dy > radius || -dy > radius)
				// continue;
				long dz = z1 - z;
				
				if (((dx * dx) + (dy * dy) + (dz * dz)) < sqRadius)
				{
					result.add(o);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @param  regionX
	 * @return         World X of given region X coordinate.
	 */
	public static final int getRegionX(int regionX)
	{
		return (regionX - REGION_X_OFFSET) * REGION_SIZE;
	}
	
	/**
	 * @param  regionY
	 * @return         World Y of given region Y coordinate.
	 */
	public static final int getRegionY(int regionY)
	{
		return (regionY - REGION_Y_OFFSET) * REGION_SIZE;
	}
	
	public L2WorldRegion[][] getAllWorldRegions()
	{
		return worldRegions;
	}
	
	/**
	 * @param  point position of the object.
	 * @return       the current L2WorldRegion of the object according to its position (x,y).
	 */
	public L2WorldRegion getRegion(LocationHolder point)
	{
		return getRegion(point.getX(), point.getY());
	}
	
	public L2WorldRegion getRegion(int x, int y)
	{
		return worldRegions[(x - WORLD_X_MIN) / REGION_SIZE][(y - WORLD_Y_MIN) / REGION_SIZE];
	}
	
	/**
	 * Check if the current L2WorldRegions of the object is valid according to its position (x,y).<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Init L2WorldRegions</li>
	 * @param  x X position of the object
	 * @param  y Y position of the object
	 * @return   True if the L2WorldRegion is valid
	 */
	private static boolean validRegion(int x, int y)
	{
		return ((x >= 0) && (x <= REGIONS_X) && (y >= 0) && (y <= REGIONS_Y));
	}
	
	/**
	 * Init each L2WorldRegion and their surrounding table.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All surrounding L2WorldRegion are identified in <B>_surroundingRegions</B> of the selected L2WorldRegion in order to scan a large area around a L2Object<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Constructor of L2World</li>
	 */
	private void initRegions()
	{
		worldRegions = new L2WorldRegion[REGIONS_X + 1][REGIONS_Y + 1];
		
		for (int i = 0; i <= REGIONS_X; i++)
		{
			for (int j = 0; j <= REGIONS_Y; j++)
			{
				worldRegions[i][j] = new L2WorldRegion(i, j);
			}
		}
		
		for (int x = 0; x <= REGIONS_X; x++)
		{
			for (int y = 0; y <= REGIONS_Y; y++)
			{
				for (int a = -1; a <= 1; a++)
				{
					for (int b = -1; b <= 1; b++)
					{
						if (validRegion(x + a, y + b))
						{
							worldRegions[x + a][y + b].addSurroundingRegion(worldRegions[x][y]);
						}
					}
				}
			}
		}
		UtilPrint.result("L2World", "WorldRegion grid x", REGIONS_X);
		UtilPrint.result("L2World", "WorldRegion grid y", REGIONS_Y);
	}
	
	/**
	 * Deleted all spawns in the world.
	 */
	public synchronized void deleteVisibleNpcSpawns()
	{
		for (int i = 0; i <= REGIONS_X; i++)
		{
			for (int j = 0; j <= REGIONS_Y; j++)
			{
				worldRegions[i][j].deleteVisibleNpcSpawns();
			}
		}
		LOG.info("All visible NPC's deleted.");
	}
	
	public static L2World getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final L2World INSTANCE = new L2World();
	}
}
