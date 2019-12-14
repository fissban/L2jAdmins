
package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.instancemanager.spawn.DayNightSpawnManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.UtilPrint;
import main.data.memory.ObjectData;

/**
 * @author Nightmare, fissban
 */
public class SpawnData
{
	// Log
	private static final Logger LOG = Logger.getLogger(SpawnData.class.getName());
	// Query
	private static String SPAWN_QUERY = "SELECT count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id,periodOfDay FROM spawnlist";
	private static String SPAWN_CUSTOM_QUERY = "SELECT count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id,periodOfDay FROM custom_spawnlist";
	// Instances
	private final Set<Spawn> spawnList = ConcurrentHashMap.newKeySet();
	
	/**
	 * Tables are loaded.
	 * <li>spawnlist.sql</li>
	 * <li>custom_spawnlist.sql</li>
	 */
	public void load()
	{
		readTable();
	}
	
	/**
	 * Tables are re-loaded.
	 * <li>spawnlist.sql</li>
	 * <li>custom_spawnlist.sql</li>
	 */
	public void reloadAll()
	{
		spawnList.clear();
		readTable();
	}
	
	/**
	 * A collection is obtained with spawnlist..
	 * @return Collection<Spawn>
	 */
	public Collection<Spawn> getSpawnTable()
	{
		return spawnList;
	}
	
	/**
	 * Load the spawnlist.
	 */
	private void readTable()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			loadSpawns(con, SPAWN_QUERY, false);
			
			if (Config.CUSTOM_SPAWNLIST_TABLE)
			{
				loadSpawns(con, SPAWN_CUSTOM_QUERY, true);
			}
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			LOG.warning(SpawnData.class.getSimpleName() + ": Spawn could not be initialized: " + e);
		}
		
		UtilPrint.result("SpawnData", "Loaded spawn", spawnList.size());
	}
	
	private void loadSpawns(Connection con, String query, boolean custom)
	{
		try (PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				NpcTemplate template = NpcData.getInstance().getTemplate(rs.getInt("npc_templateid"));
				if (template != null)
				{
					if (template.isType("L2SiegeGuard"))
					{
						// Don't spawn siege guard
					}
					else if (template.isType("L2RaidBoss"))
					{
						// Don't spawn raid boss
					}
					else
					{
						Spawn spawn = new Spawn(template);
						spawn.setCustom(custom);
						spawn.setAmount(rs.getInt("count"));
						spawn.setX(rs.getInt("locx"));
						spawn.setY(rs.getInt("locy"));
						spawn.setZ(rs.getInt("locz"));
						spawn.setHeading(rs.getInt("heading"));
						spawn.setRespawnDelay(rs.getInt("respawn_delay"));
						spawn.setSpawnLocation(rs.getInt("loc_id"));
						
						switch (rs.getInt("periodOfDay"))
						{
							case 0: // default
								spawn.init();
								break;
							case 1: // Day
								DayNightSpawnManager.getInstance().addDayCreature(spawn);
								break;
							case 2: // Night
								DayNightSpawnManager.getInstance().addNightCreature(spawn);
								break;
						}
						
						spawnList.add(spawn);
					}
				}
				else
				{
					LOG.warning(SpawnData.class.getSimpleName() + ": Data missing in NPC table for ID: " + rs.getInt("npc_templateid") + ".");
				}
			}
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			LOG.warning(SpawnData.class.getSimpleName() + ": Spawn could not be initialized: " + e);
		}
	}
	
	/**
	 * Add new spawn
	 * @param spawn
	 * @param storeInDb
	 */
	public void addNewSpawn(Spawn spawn, boolean storeInDb)
	{
		spawnList.add(spawn);
		
		if (storeInDb)
		{
			try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement("INSERT INTO " + (spawn.isCustom() ? "custom_spawnlist" : "spawnlist") + " (count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id) values(?,?,?,?,?,?,?,?)");)
			{
				// statement.setInt(1, -1);
				ps.setInt(1, spawn.getAmount());
				ps.setInt(2, spawn.getNpcId());
				ps.setInt(3, spawn.getX());
				ps.setInt(4, spawn.getY());
				ps.setInt(5, spawn.getZ());
				ps.setInt(6, spawn.getHeading());
				ps.setInt(7, spawn.getRespawnDelay() / 1000);
				ps.setInt(8, spawn.getSpawnLocation());
				ps.execute();
			}
			catch (Exception e)
			{
				// problem with storing spawn
				LOG.warning(SpawnData.class.getSimpleName() + ": Could not store spawn in the DB: " + e);
			}
		}
	}
	
	/**
	 * Delete spawn
	 * @param spawn
	 * @param updateDb
	 */
	public void deleteSpawn(Spawn spawn, boolean updateDb)
	{
		if (!spawnList.remove(spawn))
		{
			return;
		}
		
		ObjectData.removeObject(spawn.getLastSpawn());
		
		if (updateDb)
		{
			try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM " + (spawn.isCustom() ? "custom_spawnlist" : "spawnlist") + " WHERE locx=? AND locy=? AND locz=? AND npc_templateid=? AND heading=?"))
			{
				ps.setInt(1, spawn.getX());
				ps.setInt(2, spawn.getY());
				ps.setInt(3, spawn.getZ());
				ps.setInt(4, spawn.getNpcId());
				ps.setInt(5, spawn.getHeading());
				ps.execute();
			}
			catch (Exception e)
			{
				// problem with deleting spawn
				LOG.warning(SpawnData.class.getSimpleName() + ": Spawn " + spawn.getNpcId() + " could not be removed from DB: " + e);
			}
		}
	}
	
	/**
	 * Get all spawns a npc
	 * @param activeChar
	 * @param npcId         : Id the npc
	 * @param teleportIndex true = 1st teleports to spawn we found.
	 */
	public void findNPCInstances(L2PcInstance activeChar, int npcId, int teleportIndex)
	{
		int index = 0;
		for (Spawn spawn : spawnList)
		{
			if (npcId == spawn.getNpcId())
			{
				index++;
				
				if (teleportIndex > -1)
				{
					if (teleportIndex == index)
					{
						activeChar.teleToLocation(spawn.getX(), spawn.getY(), spawn.getZ(), true);
					}
				}
				else
				{
					activeChar.sendMessage(index + " - " + spawn.getTemplate().getName() + " (" + spawn.getNpcId() + "): " + spawn.getX() + " " + spawn.getY() + " " + spawn.getZ());
				}
			}
		}
		
		if (index == 0)
		{
			activeChar.sendMessage("No current spawns found.");
		}
	}
	
	public static SpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SpawnData INSTANCE = new SpawnData();
	}
}
