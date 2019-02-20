package l2j.gameserver.instancemanager.zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.zone.type.BossZone;

/**
 * @author fissban
 */
public class ZoneGrandBossManager
{
	private static final Logger LOG = Logger.getLogger(ZoneGrandBossManager.class.getName());
	// SQL
	private static final String SELECT = "SELECT * FROM grandboss_list ORDER BY player_id";
	private static final String INSERT = "INSERT INTO grandboss_list (player_id,zone) VALUES (?,?)";
	private static final String DELETE_ALL = "DELETE FROM grandboss_list";
	//
	private static final List<BossZone> grandBossZones = new ArrayList<>();
	
	public ZoneGrandBossManager()
	{
		//
	}
	
	public void initZones()
	{
		var zonesAux = new HashMap<Integer, List<Integer>>();
		
		for (var z : grandBossZones)
		{
			if (z != null)
			{
				zonesAux.put(z.getId(), new ArrayList<Integer>());
			}
		}
		
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement(SELECT);
			var rset = ps.executeQuery())
		{
			while (rset.next())
			{
				var id = rset.getInt("player_id");
				var zoneId = rset.getInt("zone");
				zonesAux.get(zoneId).add(id);
			}
			
			LOG.info("ZoneGrandBossManager: Initialized " + zonesAux.size() + " Grand Boss Zone(s)");
		}
		catch (Exception e)
		{
			LOG.warning("ZoneGrandBossManager: Could not load grandboss_list table");
		}
		
		grandBossZones.stream().filter(zone -> zone != null).forEach(zone -> zone.setAllowedPlayers(zonesAux.get(zone.getId())));
	}
	
	/**
	 * Store players allowed in zone for GrandBoss.
	 */
	public static void storeToDb()
	{
		try (var con = DatabaseManager.getConnection())
		{
			// clear table first
			try (var ps = con.prepareStatement(DELETE_ALL))
			{
				ps.executeUpdate();
			}
			
			// insert values
			try (var ps = con.prepareStatement(INSERT))
			{
				var cont = 0;
				for (var zone : grandBossZones)
				{
					if (zone == null)
					{
						continue;
					}
					
					for (var player : zone.getAllowedPlayers())
					{
						ps.setInt(1, player);
						ps.setInt(2, zone.getId());
						ps.addBatch();// save values
						cont++;
					}
				}
				
				if (cont > 0)
				{
					ps.executeBatch(); // execute querys
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning("ZoneGrandBossManager: Could not store Grand Bosses to database:" + e);
		}
	}
	
	/**
	 * Add new GrandBoss zone
	 * @param zone
	 */
	public static void add(BossZone zone)
	{
		grandBossZones.add(zone);
	}
	
	public static BossZone isInsideInZone(L2Character character)
	{
		return grandBossZones.stream().filter(zone -> zone.isInsideZone(character)).findFirst().orElse(null);
	}
	
	/**
	 * Get GrandBoss zone by loc
	 * @param  loc
	 * @return
	 */
	public static BossZone getZone(LocationHolder loc)
	{
		return grandBossZones.stream().filter(zone -> zone.isInsideZone(loc.getX(), loc.getY(), loc.getZ())).findFirst().orElse(null);
	}
	
	/**
	 * Get GrandBoss zone by loc
	 * @param  x
	 * @param  y
	 * @param  z
	 * @return
	 */
	public static BossZone getZone(int x, int y, int z)
	{
		return grandBossZones.stream().filter(zone -> zone.isInsideZone(x, y, z)).findFirst().orElse(null);
	}
	
	/**
	 * Get grand boss zone by id
	 * @param  id
	 * @return
	 */
	public static BossZone getZone(int id)
	{
		return grandBossZones.stream().filter(zone -> zone.getId() == id).findFirst().orElse(null);
	}
	
	public static boolean checkIfInZone(String zoneType, L2Object obj)
	{
		var temp = getZone(obj.getWorldPosition());
		if (temp == null)
		{
			return false;
		}
		
		return temp.getZoneName().equalsIgnoreCase(zoneType);
	}
	
	/**
	 * Clear grandBossZones
	 */
	public static void clearAllZone()
	{
		grandBossZones.clear();
	}
	
	public static ZoneGrandBossManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneGrandBossManager INSTANCE = new ZoneGrandBossManager();
	}
}
