package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.instancemanager.spawn.DayNightSpawnManager;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.spawn.Spawn;
import l2j.util.Rnd;
import l2j.util.UtilPrint;

/**
 * @author godson
 */
public class RaidBossSpawnData
{
	private static final Logger LOG = Logger.getLogger(RaidBossSpawnData.class.getName());
	// SQL
	private static final String LOAD = "SELECT * from raidboss_spawnlist ORDER BY boss_id";
	private static final String INSERT = "INSERT INTO raidboss_spawnlist (boss_id,amount,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE = "DELETE FROM raidboss_spawnlist WHERE boss_id=?";
	private static final String UPDATE = "UPDATE raidboss_spawnlist set respawn_time=?,currentHP=?,currentMP=? WHERE boss_id=?";
	//
	protected final Map<Integer, L2RaidBossInstance> bosses = new HashMap<>();
	protected final Map<Integer, Spawn> spawns = new HashMap<>();
	protected final Map<Integer, StatsSet> storedInfo = new HashMap<>();
	protected final Map<Integer, ScheduledFuture<?>> schedules = new HashMap<>();
	
	public static enum StatusEnum
	{
		ALIVE,
		DEAD,
		UNDEFINED
	}
	
	public RaidBossSpawnData()
	{
		//
	}
	
	public void reload()
	{
		bosses.clear();
		spawns.clear();
		storedInfo.clear();
		schedules.clear();
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				NpcTemplate template = getValidTemplate(rs.getInt("boss_id"));
				if (template != null)
				{
					Spawn spawnDat;
					spawnDat = new Spawn(template);
					spawnDat.setX(rs.getInt("loc_x"));
					spawnDat.setY(rs.getInt("loc_y"));
					spawnDat.setZ(rs.getInt("loc_z"));
					spawnDat.setAmount(rs.getInt("amount"));
					spawnDat.setHeading(rs.getInt("heading"));
					spawnDat.setRespawnMinDelay(rs.getInt("respawn_min_delay"));
					spawnDat.setRespawnMaxDelay(rs.getInt("respawn_max_delay"));
					
					long respawnTime = rs.getLong("respawn_time");
					
					addNewSpawn(spawnDat, respawnTime, rs.getDouble("currentHP"), rs.getDouble("currentMP"), false);
				}
				else
				{
					LOG.warning("RaidBossSpawnData: Could not load raidboss #" + rs.getInt("boss_id") + " from DB");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("RaidBossSpawnData: Couldnt load raidboss_spawnlist table");
		}
		
		UtilPrint.result("RaidBossSpawnData", "Loaded boss instances", bosses.size());
		UtilPrint.result("RaidBossSpawnData", "Loaded boss scheduled instances", schedules.size());
	}
	
	private class spawnSchedule implements Runnable
	{
		private final int bossId;
		
		public spawnSchedule(int npcId)
		{
			bossId = npcId;
		}
		
		@Override
		public void run()
		{
			L2RaidBossInstance raidboss = null;
			
			if (bossId == 10328)
			{
				raidboss = DayNightSpawnManager.getInstance().handleBoss(spawns.get(bossId));
			}
			else
			{
				raidboss = (L2RaidBossInstance) spawns.get(bossId).doSpawn();
			}
			
			if (raidboss != null)
			{
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				StatsSet info = new StatsSet();
				info.set("currentHP", raidboss.getCurrentHp());
				info.set("currentMP", raidboss.getCurrentMp());
				info.set("respawnTime", 0L);
				
				storedInfo.put(bossId, info);
				bosses.put(bossId, raidboss);
				GmListData.getInstance().broadcastMessageToGMs("RaidBossSpawnData: Spawning Raid Boss " + raidboss.getName());
			}
			
			schedules.remove(bossId);
		}
	}
	
	public void updateStatus(L2RaidBossInstance boss, boolean isBossDead)
	{
		StatsSet info = storedInfo.get(boss.getId());
		
		if (info == null)
		{
			return;
		}
		
		if (isBossDead)
		{
			boss.setRaidStatus(StatusEnum.DEAD);
			
			final int respawnMinDelay = boss.getSpawn().getRespawnMinDelay();
			final int respawnMaxDelay = boss.getSpawn().getRespawnMaxDelay();
			final long respawnDelay = Rnd.get((int) (respawnMinDelay * 1000 * Config.RAID_MIN_RESPAWN_MULTIPLIER), (int) (respawnMaxDelay * 1000 * Config.RAID_MAX_RESPAWN_MULTIPLIER));
			final long respawnTime = Calendar.getInstance().getTimeInMillis() + respawnDelay;
			
			info.set("currentHP", boss.getStat().getMaxHp());
			info.set("currentMP", boss.getStat().getMaxMp());
			info.set("respawnTime", respawnTime);
			
			LOG.info("RaidBossSpawnData: Updated " + boss.getName() + " respawn time to " + respawnTime);
			
			if (!schedules.containsKey(boss.getId()))
			{
				schedules.put(boss.getId(), ThreadPoolManager.getInstance().schedule(new spawnSchedule(boss.getId()), respawnDelay));
				// To update immediately Database uncomment on the following line, to post the hour of respawn raid boss on your site for example or to envisage a crash landing of the waiter.
				updateDb(boss.getId());
			}
		}
		else
		{
			boss.setRaidStatus(StatusEnum.ALIVE);
			
			info.set("currentHP", boss.getCurrentHp());
			info.set("currentMP", boss.getCurrentMp());
			info.set("respawnTime", 0L);
		}
		
		storedInfo.put(boss.getId(), info);
	}
	
	public void addNewSpawn(Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null)
		{
			return;
		}
		if (spawns.containsKey(spawnDat.getNpcId()))
		{
			return;
		}
		
		int bossId = spawnDat.getNpcId();
		long time = Calendar.getInstance().getTimeInMillis();
		
		SpawnData.getInstance().addNewSpawn(spawnDat, false);
		
		if ((respawnTime == 0L) || (time > respawnTime))
		{
			L2RaidBossInstance raidboss = null;
			
			if (bossId == 10328)
			{
				raidboss = DayNightSpawnManager.getInstance().handleBoss(spawnDat);
			}
			else
			{
				raidboss = (L2RaidBossInstance) spawnDat.doSpawn();
			}
			
			if (raidboss != null)
			{
				raidboss.setCurrentHp(currentHP);
				raidboss.setCurrentMp(currentMP);
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				bosses.put(bossId, raidboss);
				
				StatsSet info = new StatsSet();
				info.set("currentHP", currentHP);
				info.set("currentMP", currentMP);
				info.set("respawnTime", 0L);
				
				storedInfo.put(bossId, info);
			}
		}
		else
		{
			ScheduledFuture<?> futureSpawn = ThreadPoolManager.getInstance().schedule(new spawnSchedule(bossId), respawnTime - Calendar.getInstance().getTimeInMillis());
			schedules.put(bossId, futureSpawn);
		}
		
		spawns.put(bossId, spawnDat);
		
		if (storeInDb)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT))
			{
				ps.setInt(1, spawnDat.getNpcId());
				ps.setInt(2, spawnDat.getAmount());
				ps.setInt(3, spawnDat.getX());
				ps.setInt(4, spawnDat.getY());
				ps.setInt(5, spawnDat.getZ());
				ps.setInt(6, spawnDat.getHeading());
				ps.setLong(7, respawnTime);
				ps.setDouble(8, currentHP);
				ps.setDouble(9, currentMP);
				ps.executeUpdate();
				ps.clearParameters();
			}
			catch (Exception e)
			{
				LOG.warning("RaidBossSpawnData: Could not store raidboss #" + bossId + " in the DB:" + e);
			}
		}
	}
	
	public void deleteSpawn(Spawn spawnDat, boolean updateDb)
	{
		if (spawnDat == null)
		{
			return;
		}
		if (!spawns.containsKey(spawnDat.getNpcId()))
		{
			return;
		}
		
		int bossId = spawnDat.getNpcId();
		
		SpawnData.getInstance().deleteSpawn(spawnDat, false);
		spawns.remove(bossId);
		
		if (bosses.containsKey(bossId))
		{
			bosses.remove(bossId);
		}
		
		if (schedules.containsKey(bossId))
		{
			schedules.get(bossId).cancel(true);
			schedules.remove(bossId);
		}
		
		if (storedInfo.containsKey(bossId))
		{
			storedInfo.remove(bossId);
		}
		
		if (updateDb)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(DELETE))
			{
				ps.setInt(1, bossId);
				ps.executeUpdate();
				ps.clearParameters();
			}
			catch (Exception e)
			{
				LOG.warning("RaidBossSpawnData: Could not remove raidboss #" + bossId + " from DB: " + e);
			}
		}
	}
	
	private void updateDb(int bossId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE))
		{
			final L2RaidBossInstance boss = bosses.get(bossId);
			if (boss == null)
			{
				return;
			}
			
			final StatsSet info = storedInfo.get(bossId);
			if (info == null)
			{
				return;
			}
			
			ps.setLong(1, info.getLong("respawnTime"));
			ps.setDouble(2, info.getDouble("currentHP"));
			ps.setDouble(3, info.getDouble("currentMP"));
			ps.setInt(4, bossId);
			ps.executeUpdate();
			ps.clearParameters();
		}
		catch (SQLException e)
		{
			LOG.warning("RaidBossSpawnData: Couldnt update raidboss_spawnlist table");
		}
	}
	
	public StatusEnum getRaidBossStatusId(int bossId)
	{
		if (bosses.containsKey(bossId))
		{
			return bosses.get(bossId).getRaidStatus();
		}
		else if (schedules.containsKey(bossId))
		{
			return StatusEnum.DEAD;
		}
		else
		{
			return StatusEnum.UNDEFINED;
		}
	}
	
	public NpcTemplate getValidTemplate(int bossId)
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(bossId);
		if (template == null)
		{
			return null;
		}
		if (!template.isType("L2RaidBoss"))
		{
			return null;
		}
		return template;
	}
	
	public void notifySpawnNightBoss(L2RaidBossInstance raidboss)
	{
		StatsSet info = new StatsSet();
		info.set("currentHP", raidboss.getCurrentHp());
		info.set("currentMP", raidboss.getCurrentMp());
		info.set("respawnTime", 0L);
		
		raidboss.setRaidStatus(StatusEnum.ALIVE);
		
		storedInfo.put(raidboss.getId(), info);
		bosses.put(raidboss.getId(), raidboss);
		GmListData.getInstance().broadcastMessageToGMs("Spawning Raid Boss " + raidboss.getName());
	}
	
	public boolean isDefined(int bossId)
	{
		return spawns.containsKey(bossId);
	}
	
	public Map<Integer, L2RaidBossInstance> getBosses()
	{
		return bosses;
	}
	
	public Map<Integer, Spawn> getSpawns()
	{
		return spawns;
	}
	
	/**
	 * Saves all raidboss status and then clears all info from memory, including all schedules.
	 */
	public void saveAllBoss()
	{
		bosses.keySet().forEach(id -> updateDb(id));
		bosses.clear();
		
		schedules.values().forEach(f -> f.cancel(true));
		schedules.clear();
		
		storedInfo.clear();
		spawns.clear();
	}
	
	public static RaidBossSpawnData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossSpawnData INSTANCE = new RaidBossSpawnData();
	}
}
