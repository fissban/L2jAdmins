package l2j.gameserver.model.entity.castle.siege.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.spawn.Spawn;

public class SiegeGuardManager
{
	private static final Logger LOG = Logger.getLogger(SiegeGuardManager.class.getName());
	
	private Castle castle;
	private final List<Spawn> siegeGuardSpawn = new ArrayList<>();
	
	public SiegeGuardManager(Castle castle)
	{
		this.castle = castle;
	}
	
	/**
	 * Add guard.
	 * @param activeChar
	 * @param npcId
	 */
	public void addGuard(L2PcInstance activeChar, int npcId)
	{
		if (activeChar == null)
		{
			return;
		}
		addGuard(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
	}
	
	/**
	 * Add guard.
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param npcId
	 */
	public void addGuard(int x, int y, int z, int heading, int npcId)
	{
		saveGuard(x, y, z, heading, npcId, 0);
	}
	
	/**
	 * Hire merc.
	 * @param activeChar
	 * @param npcId
	 */
	public void hireMerc(L2PcInstance activeChar, int npcId)
	{
		if (activeChar == null)
		{
			return;
		}
		hireMerc(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
	}
	
	/**
	 * Hire merc.
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param npcId
	 */
	public void hireMerc(int x, int y, int z, int heading, int npcId)
	{
		saveGuard(x, y, z, heading, npcId, 1);
	}
	
	/**
	 * Remove a single mercenary, identified by the npcId and location. Presumably, this is used when a castle lord picks up a previously dropped ticket
	 * @param npcId
	 * @param x
	 * @param y
	 * @param z
	 */
	public void removeMerc(int npcId, int x, int y, int z)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM castle_siege_guards Where npcId = ? And x = ? AND y = ? AND z = ? AND isHired = 1"))
		{
			ps.setInt(1, npcId);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("SiegeGuardManager: Error deleting hired siege guard at " + x + ',' + y + ',' + z + " " + e);
		}
	}
	
	/**
	 * Remove mercs.
	 */
	public void removeMercs()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM castle_siege_guards Where castleId = ? And isHired = 1"))
		{
			ps.setInt(1, castle.getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("SiegeGuardManager: Error deleting hired siege guard for castle " + castle.getName() + " " + e);
		}
	}
	
	/**
	 * Spawn guards.
	 */
	public void spawnAllGuards()
	{
		loadGuards();
		for (Spawn spawn : siegeGuardSpawn)
		{
			if (spawn != null)
			{
				spawn.init();
			}
		}
	}
	
	/**
	 * Unspawn guards.
	 */
	public void unspawnAllGuards()
	{
		for (Spawn spawn : siegeGuardSpawn)
		{
			if (spawn == null)
			{
				continue;
			}
			
			spawn.stopRespawn();
			spawn.getLastSpawn().doDie(spawn.getLastSpawn());
		}
		
		siegeGuardSpawn.clear();
	}
	
	/**
	 * Load guards.
	 */
	private void loadGuards()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_siege_guards WHERE castleId=? AND isHired=?");)
		{
			ps.setInt(1, castle.getId());
			if (castle.getOwnerId() > 0)
			{
				ps.setInt(2, 1);
			}
			else
			{
				ps.setInt(2, 0);
			}
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					NpcTemplate template = NpcData.getInstance().getTemplate(rs.getInt("npcId"));
					if (template != null)
					{
						Spawn spawn = new Spawn(template);
						// spawn.setId(rs.getInt("id"));
						spawn.setAmount(1);
						spawn.setX(rs.getInt("x"));
						spawn.setY(rs.getInt("y"));
						spawn.setZ(rs.getInt("z"));
						spawn.setHeading(rs.getInt("heading"));
						spawn.setRespawnDelay(rs.getInt("respawnDelay"));
						spawn.setSpawnLocation(0);
						
						siegeGuardSpawn.add(spawn);
					}
					else
					{
						LOG.warning("SiegeGuardManager: Missing npc data in npc table for id: " + rs.getInt("npcId"));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("SiegeGuardManager: Error loading siege guard for castle " + castle.getName() + " " + e);
		}
	}
	
	/**
	 * Save guards.
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param npcId
	 * @param isHire
	 */
	private void saveGuard(int x, int y, int z, int heading, int npcId, int isHire)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO castle_siege_guards (castleId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)"))
		{
			ps.setInt(1, castle.getId());
			ps.setInt(2, npcId);
			ps.setInt(3, x);
			ps.setInt(4, y);
			ps.setInt(5, z);
			ps.setInt(6, heading);
			if (isHire == 1)
			{
				ps.setInt(7, 0);
			}
			else
			{
				ps.setInt(7, 600);
			}
			ps.setInt(8, isHire);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("SiegeGuardManager:  Error adding siege guard for castle " + castle.getName() + ":" + e);
		}
	}
	
	public final List<Spawn> getGuardSpawns()
	{
		return siegeGuardSpawn;
	}
	
	public final int getGuardSpawnsCount()
	{
		return siegeGuardSpawn.size();
	}
}
