package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.util.UtilPrint;

/**
 * @author DaRkRaGe, Emperorc, fissban
 */
public class GrandBossSpawnData
{
	private static final Logger LOG = Logger.getLogger(GrandBossSpawnData.class.getName());
	// SQL
	private static final String SELECT = "SELECT * FROM grandboss_data ORDER BY boss_id";
	private static final String UPDATE_1 = "UPDATE grandboss_data SET status=? WHERE boss_id=?";
	private static final String UPDATE_2 = "UPDATE grandboss_data set loc_x=?,loc_y=?,loc_z=?,heading=?,respawn_time=?,currentHP=?,currentMP=?,status=? WHERE boss_id=?";
	//
	private static final Map<Integer, GrandBossHolder> bosses = new HashMap<>();
	
	public GrandBossSpawnData()
	{
		//
	}
	
	public void load()
	{
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement(SELECT);
			var rset = ps.executeQuery())
		{
			while (rset.next())
			{
				var gb = new GrandBossHolder();
				gb.setBoosId(rset.getInt("boss_id"));
				gb.setLoc(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_z"), rset.getInt("heading"));
				gb.setRespawnTime(rset.getLong("respawn_time"));
				gb.setCurrentHp(rset.getDouble("currentHP"));
				gb.setCurrentMp(rset.getDouble("currentMP"));
				gb.setStatus(rset.getInt("status"));
				
				bosses.put(gb.getBoosId(), gb);
			}
		}
		catch (Exception e)
		{
			LOG.warning(GrandBossSpawnData.class.getSimpleName() + ": Could not load grandboss_data table");
			e.printStackTrace();
		}
		
		UtilPrint.result("GrandBossSpawnData", "Loaded GrandBosses instances", bosses.size());
	}
	
	/**
	 * Store GrandBoss info in DB
	 * @param gb
	 */
	private static void storeToDb(GrandBossHolder gb)
	{
		ZoneGrandBossManager.storeToDb();
		
		try (var con = DatabaseManager.getConnection())
		{
			if (gb.getBoss() == null)
			{
				try (var ps = con.prepareStatement(UPDATE_1))
				{
					ps.setInt(1, gb.getStatus());
					ps.setInt(2, gb.getBoosId());
					ps.executeUpdate();
				}
			}
			else
			{
				try (var ps = con.prepareStatement(UPDATE_2))
				{
					ps.setInt(1, gb.getLoc().getX());
					ps.setInt(2, gb.getLoc().getY());
					ps.setInt(3, gb.getLoc().getZ());
					ps.setInt(4, gb.getLoc().getHeading());
					ps.setLong(5, gb.getRespawnTime());
					
					double hp;
					double mp;
					if (gb.getBoss().isDead())
					{
						hp = gb.getBoss().getStat().getMaxHp();
						mp = gb.getBoss().getStat().getMaxMp();
					}
					else
					{
						hp = gb.getCurrentHp();
						mp = gb.getCurrentMp();
					}
					
					ps.setDouble(6, hp);
					ps.setDouble(7, mp);
					ps.setInt(8, gb.getStatus());
					ps.setInt(9, gb.getBoosId());
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(GrandBossSpawnData.class.getSimpleName() + ": Could not store Grand Bosses to database:" + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * @param bossInstance
	 */
	public static void addBoss(L2GrandBossInstance bossInstance)
	{
		bosses.get(bossInstance.getId()).setBoss(bossInstance);
	}
	
	/**
	 * Get GrandBoss info
	 * @param  bossId
	 * @return
	 */
	public static GrandBossHolder getBossInfo(int bossId)
	{
		return bosses.get(bossId);
	}
	
	/**
	 * Store GrandBoss in DB
	 * @param bossId
	 */
	public static void saveBoss(int bossId)
	{
		storeToDb(bosses.get(bossId));
	}
	
	/**
	 * Saves all Grand Boss info and then clears all info from memory
	 */
	public static void saveAllBoss()
	{
		bosses.values().forEach(gb -> storeToDb(gb));
		bosses.clear();
	}
	
	public static GrandBossSpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GrandBossSpawnData INSTANCE = new GrandBossSpawnData();
	}
}
