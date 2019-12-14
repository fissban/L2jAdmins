
package l2j.gameserver.model.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.HeroData;
import l2j.gameserver.data.ZoneData;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;
import l2j.util.UtilPrint;

/**
 * @author godson
 */
public class Olympiad
{
	protected static final Logger LOG = Logger.getLogger(Olympiad.class.getName());
	
	private static final Map<Integer, StatsSet> nobles = new HashMap<>();
	private static final Map<Integer, Integer> noblesEom = new HashMap<>();
	
	protected static final List<StatsSet> heroesToBe = new ArrayList<>();
	
	public static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
	
	private static final String OLYMPIAD_LOAD_DATA = "SELECT current_cycle, period, olympiad_end, validation_end, next_weekly_change FROM olympiad_data WHERE id = 0";
	private static final String OLYMPIAD_SAVE_DATA = "INSERT INTO olympiad_data (id, current_cycle, period, olympiad_end, validation_end, next_weekly_change) VALUES (0,?,?,?,?,?) ON DUPLICATE KEY UPDATE current_cycle=?, period=?, olympiad_end=?, validation_end=?, next_weekly_change=?";
	
	private static final String OLYMPIAD_LOAD_NOBLES = "SELECT olympiad_nobles.char_id, olympiad_nobles.class_id, characters.char_name, olympiad_nobles.olympiad_points, olympiad_nobles.competitions_done, olympiad_nobles.competitions_won, olympiad_nobles.competitions_lost, olympiad_nobles.competitions_drawn FROM olympiad_nobles, characters WHERE characters.obj_Id = olympiad_nobles.char_id";
	private static final String OLYMPIAD_SAVE_NOBLES = "INSERT INTO olympiad_nobles (`char_id`,`class_id`,`olympiad_points`,`competitions_done`,`competitions_won`,`competitions_lost`, `competitions_drawn`) VALUES (?,?,?,?,?,?,?)";
	private static final String OLYMPIAD_UPDATE_NOBLES = "UPDATE olympiad_nobles SET olympiad_points = ?, competitions_done = ?, competitions_won = ?, competitions_lost = ?, competitions_drawn = ? WHERE char_id = ?";
	private static final String OLYMPIAD_GET_HEROS = "SELECT olympiad_nobles.char_id, characters.char_name FROM olympiad_nobles, characters WHERE characters.obj_Id = olympiad_nobles.char_id AND olympiad_nobles.class_id = ? AND olympiad_nobles.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES
		+ " AND olympiad_nobles.competitions_won > 0 ORDER BY olympiad_nobles.olympiad_points DESC, olympiad_nobles.competitions_done DESC, olympiad_nobles.competitions_won DESC";
	private static final String GET_ALL_CLASSIFIED_NOBLESS_EOM = "SELECT char_id, olympiad_points from olympiad_nobles_eom WHERE competitions_done >= " + Config.ALT_OLY_MIN_MATCHES;
	private static final String REMOVE_CLASSIFIED_NOBLESS_EOM = "DELETE FROM olympiad_nobles_eom WHERE char_id=?";
	private static final String GET_EACH_CLASS_LEADER = "SELECT characters.char_name from olympiad_nobles, characters WHERE characters.obj_Id = olympiad_nobles.char_id AND olympiad_nobles.class_id = ? AND olympiad_nobles.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES
		+ " ORDER BY olympiad_nobles.olympiad_points DESC, olympiad_nobles.competitions_done DESC, olympiad_nobles.competitions_won DESC LIMIT 10";
	
	private static final String OLYMPIAD_DELETE_ALL = "TRUNCATE olympiad_nobles";
	private static final String OLYMPIAD_MONTH_CLEAR = "TRUNCATE olympiad_nobles_eom";
	private static final String OLYMPIAD_MONTH_CREATE = "INSERT INTO olympiad_nobles_eom SELECT char_id, olympiad_points FROM olympiad_nobles";
	
	private static final int COMP_START = Config.ALT_OLY_START_TIME; // 6PM
	private static final int COMP_MIN = Config.ALT_OLY_MIN; // 00 mins
	private static final long COMP_PERIOD = Config.ALT_OLY_CPERIOD; // 6 hours
	protected static final long WEEKLY_PERIOD = Config.ALT_OLY_WPERIOD; // 1 week
	protected static final long VALIDATION_PERIOD = Config.ALT_OLY_VPERIOD; // 24 hours
	
	protected static final int DEFAULT_POINTS = Config.ALT_OLY_START_POINTS;
	protected static final int WEEKLY_POINTS = Config.ALT_OLY_WEEKLY_POINTS;
	
	public static final String CHAR_ID = "char_id";
	public static final String CLASS_ID = "class_id";
	public static final String CHAR_NAME = "char_name";
	public static final String POINTS = "olympiad_points";
	public static final String COMP_DONE = "competitions_done";
	public static final String COMP_WON = "competitions_won";
	public static final String COMP_LOST = "competitions_lost";
	public static final String COMP_DRAWN = "competitions_drawn";
	
	protected long olympiadEnd;
	protected long validationEnd;
	
	/**
	 * The current period of the olympiad.<br>
	 * <b>0 -</b> Competition period<br>
	 * <b>1 -</b> Validation Period
	 */
	protected int period;
	protected long nextWeeklyChange;
	protected int currentCycle;
	private long compEnd;
	private Calendar compStart;
	protected static boolean inCompPeriod;
	protected static boolean compStarted = false;
	
	protected ScheduledFuture<?> scheduledCompStart;
	protected ScheduledFuture<?> scheduledCompEnd;
	protected ScheduledFuture<?> scheduledOlympiadEnd;
	protected ScheduledFuture<?> scheduledWeeklyTask;
	protected ScheduledFuture<?> scheduledValdationTask;
	protected ScheduledFuture<?> gameManager = null;
	protected ScheduledFuture<?> gameAnnouncer = null;
	
	public static Olympiad getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	protected Olympiad()
	{
		load();
		
		init();
	}
	
	private void load()
	{
		boolean loaded = false;
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(OLYMPIAD_LOAD_DATA);
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				currentCycle = rset.getInt("current_cycle");
				period = rset.getInt("period");
				olympiadEnd = rset.getLong("olympiad_end");
				validationEnd = rset.getLong("validation_end");
				nextWeeklyChange = rset.getLong("next_weekly_change");
				loaded = true;
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Olympiad: Error loading olympiad data from database: ", e);
		}
		
		if (!loaded)
		{
			LOG.log(Level.INFO, "Olympiad: failed to load data from database, default values are used.");
			
			currentCycle = 1;
			period = 0;
			olympiadEnd = 0;
			validationEnd = 0;
			nextWeeklyChange = 0;
		}
		
		switch (period)
		{
			case 0:
				if ((olympiadEnd == 0) || (olympiadEnd < Calendar.getInstance().getTimeInMillis()))
				{
					setNewOlympiadEnd();
				}
				else
				{
					scheduleWeeklyChange();
				}
				break;
			case 1:
				if (validationEnd > Calendar.getInstance().getTimeInMillis())
				{
					loadNoblesPointsEom();
					
					scheduledValdationTask = ThreadPoolManager.schedule(new ValidationEndTask(), getMillisToValidationEnd());
				}
				else
				{
					currentCycle++;
					period = 0;
					deleteNobles();
					setNewOlympiadEnd();
				}
				break;
			default:
				LOG.warning("Olympiad: something went wrong loading period: " + period);
				return;
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(OLYMPIAD_LOAD_NOBLES);
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				StatsSet statData = new StatsSet();
				statData.set(CLASS_ID, rset.getInt(CLASS_ID));
				statData.set(CHAR_NAME, rset.getString(CHAR_NAME));
				statData.set(POINTS, rset.getInt(POINTS));
				statData.set(COMP_DONE, rset.getInt(COMP_DONE));
				statData.set(COMP_WON, rset.getInt(COMP_WON));
				statData.set(COMP_LOST, rset.getInt(COMP_LOST));
				statData.set(COMP_DRAWN, rset.getInt(COMP_DRAWN));
				statData.set("to_save", false);
				
				addNobleStats(rset.getInt(CHAR_ID), statData);
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Olympiad: Error loading noblesse data from database: ", e);
		}
		
		synchronized (this)
		{
			if (period == 0)
			{
				UtilPrint.result("Olympiad", "Loaded in period", "Competition");
			}
			else
			{
				UtilPrint.result("Olympiad", "Loaded stadiums", "Validation");
			}
			
			long milliToEnd;
			if (period == 0)
			{
				milliToEnd = getMillisToOlympiadEnd();
			}
			else
			{
				milliToEnd = getMillisToValidationEnd();
			}
			
			UtilPrint.result("Olympiad", "minutes until period ends", Math.round(milliToEnd / 60000));
			
			if (period == 0)
			{
				milliToEnd = getMillisToWeekChange();
				UtilPrint.result("Olympiad", "Next weekly change is in (minutes)", Math.round(milliToEnd / 60000));
			}
		}
		
		UtilPrint.result("Olympiad", "Loaded nobles", nobles.size());
	}
	
	protected static void loadNoblesPointsEom()
	{
		noblesEom.clear();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(GET_ALL_CLASSIFIED_NOBLESS_EOM);
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				noblesEom.put(rset.getInt(CHAR_ID), rset.getInt(POINTS));
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Olympiad: Error loading noblesse data from database for Ranking: ", e);
		}
	}
	
	protected void init()
	{
		if (period == 1)
		{
			return;
		}
		
		compStart = Calendar.getInstance();
		compStart.set(Calendar.HOUR_OF_DAY, COMP_START);
		compStart.set(Calendar.MINUTE, COMP_MIN);
		compEnd = compStart.getTimeInMillis() + COMP_PERIOD;
		
		if (scheduledOlympiadEnd != null)
		{
			scheduledOlympiadEnd.cancel(true);
		}
		
		scheduledOlympiadEnd = ThreadPoolManager.schedule(new OlympiadEndTask(), getMillisToOlympiadEnd());
		
		updateCompStatus();
	}
	
	protected class OlympiadEndTask implements Runnable
	{
		@Override
		public void run()
		{
			Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_ENDED).addNumber(currentCycle));
			
			if (scheduledWeeklyTask != null)
			{
				scheduledWeeklyTask.cancel(true);
			}
			
			saveNobleData();
			
			period = 1;
			sortHeroesToBe();
			// Hero.getInstance().resetData();
			HeroData.computeNewHeroes(heroesToBe);
			
			saveOlympiadStatus();
			updateMonthlyData();
			
			validationEnd = Calendar.getInstance().getTimeInMillis() + VALIDATION_PERIOD;
			
			loadNoblesPointsEom();
			scheduledValdationTask = ThreadPoolManager.schedule(new ValidationEndTask(), getMillisToValidationEnd());
		}
	}
	
	protected class ValidationEndTask implements Runnable
	{
		@Override
		public void run()
		{
			period = 0;
			currentCycle++;
			
			deleteNobles();
			setNewOlympiadEnd();
			init();
		}
	}
	
	protected static int getNobleCount()
	{
		return nobles.size();
	}
	
	protected static StatsSet getNobleStats(int playerId)
	{
		return nobles.get(playerId);
	}
	
	private void updateCompStatus()
	{
		synchronized (this)
		{
			long milliToStart = getMillisToCompBegin();
			
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis() + milliToStart);
			UtilPrint.result("Olympiad", "Competition period starts", c.getTime() + "");
			UtilPrint.result("Olympiad", "Event starts/started", compStart.getTime() + "");
		}
		
		scheduledCompStart = ThreadPoolManager.schedule(() ->
		{
			if (isOlympiadEnd())
			{
				return;
			}
			
			inCompPeriod = true;
			
			Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_STARTED));
			LOG.info("Olympiad: Olympiad game started.");
			
			gameManager = ThreadPoolManager.scheduleAtFixedRate(OlympiadGameManager.getInstance(), 30000, 30000);
			if (Config.ALT_OLY_ANNOUNCE_GAMES)
			{
				gameAnnouncer = ThreadPoolManager.scheduleAtFixedRate(new OlympiadAnnouncer(), 30000, 500);
			}
			
			long regEnd = getMillisToCompEnd() - 600000;
			if (regEnd > 0)
			{
				ThreadPoolManager.schedule(() -> Broadcast.toAllOnlinePlayers("The Grand Olympiad registration period has ended"), regEnd);
			}
			
			scheduledCompEnd = ThreadPoolManager.schedule(() ->
			{
				if (isOlympiadEnd())
				{
					return;
				}
				
				inCompPeriod = false;
				Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessage.THE_OLYMPIAD_GAME_HAS_ENDED));
				LOG.info("Olympiad: Olympiad game ended.");
				
				while (OlympiadGameManager.getInstance().isBattleStarted()) // cleared in game manager
				{
					// wait 1 minutes for end of pendings games
					try
					{
						Thread.sleep(60000);
					}
					catch (InterruptedException e)
					{
					}
				}
				
				if (gameManager != null)
				{
					gameManager.cancel(false);
					gameManager = null;
				}
				
				if (gameAnnouncer != null)
				{
					gameAnnouncer.cancel(false);
					gameAnnouncer = null;
				}
				
				saveOlympiadStatus();
				
				init();
			}, getMillisToCompEnd());
		}, getMillisToCompBegin());
	}
	
	private long getMillisToOlympiadEnd()
	{
		return (olympiadEnd - Calendar.getInstance().getTimeInMillis());
	}
	
	public void manualSelectHeroes()
	{
		if (scheduledOlympiadEnd != null)
		{
			scheduledOlympiadEnd.cancel(true);
		}
		
		scheduledOlympiadEnd = ThreadPoolManager.schedule(new OlympiadEndTask(), 0);
	}
	
	protected long getMillisToValidationEnd()
	{
		if (validationEnd > Calendar.getInstance().getTimeInMillis())
		{
			return (validationEnd - Calendar.getInstance().getTimeInMillis());
		}
		
		return 10L;
	}
	
	public boolean isOlympiadEnd()
	{
		return (period != 0);
	}
	
	protected void setNewOlympiadEnd()
	{
		Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_STARTED).addNumber(currentCycle));
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.add(Calendar.MONTH, 1);
		currentTime.set(Calendar.DAY_OF_MONTH, 1);
		currentTime.set(Calendar.AM_PM, Calendar.AM);
		currentTime.set(Calendar.HOUR, 12);
		currentTime.set(Calendar.MINUTE, 0);
		currentTime.set(Calendar.SECOND, 0);
		olympiadEnd = currentTime.getTimeInMillis();
		
		Calendar nextChange = Calendar.getInstance();
		nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
		scheduleWeeklyChange();
	}
	
	public boolean inCompPeriod()
	{
		return inCompPeriod;
	}
	
	private long getMillisToCompBegin()
	{
		if ((compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) && (compEnd > Calendar.getInstance().getTimeInMillis()))
		{
			return 10L;
		}
		
		if (compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
		{
			return (compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
		}
		
		return setNewCompBegin();
	}
	
	private long setNewCompBegin()
	{
		compStart = Calendar.getInstance();
		compStart.set(Calendar.HOUR_OF_DAY, COMP_START);
		compStart.set(Calendar.MINUTE, COMP_MIN);
		compStart.add(Calendar.HOUR_OF_DAY, 24);
		compEnd = compStart.getTimeInMillis() + COMP_PERIOD;
		
		LOG.info("Olympiad: New schedule @ " + compStart.getTime());
		
		return (compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	protected long getMillisToCompEnd()
	{
		return (compEnd - Calendar.getInstance().getTimeInMillis());
	}
	
	private long getMillisToWeekChange()
	{
		if (nextWeeklyChange > Calendar.getInstance().getTimeInMillis())
		{
			return (nextWeeklyChange - Calendar.getInstance().getTimeInMillis());
		}
		
		return 10L;
	}
	
	private void scheduleWeeklyChange()
	{
		scheduledWeeklyTask = ThreadPoolManager.scheduleAtFixedRate(() ->
		{
			addWeeklyPoints();
			LOG.info("Olympiad: Added weekly points to nobles.");
			
			Calendar nextChange = Calendar.getInstance();
			nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
		}, getMillisToWeekChange(), WEEKLY_PERIOD);
	}
	
	protected synchronized void addWeeklyPoints()
	{
		if (period == 1)
		{
			return;
		}
		
		int currentPoints;
		for (StatsSet nobleInfo : nobles.values())
		{
			currentPoints = nobleInfo.getInteger(POINTS);
			currentPoints += WEEKLY_POINTS;
			nobleInfo.set(POINTS, currentPoints);
		}
	}
	
	public int getCurrentCycle()
	{
		return currentCycle;
	}
	
	public boolean playerInStadia(L2PcInstance player)
	{
		for (Zone temp : ZoneData.getInstance().getZones(player.getX(), player.getY()))
		{
			if ((temp instanceof OlympiadStadiumZone) && temp.isCharacterInZone(player))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Save noblesse data to database
	 */
	protected static synchronized void saveNobleData()
	{
		if (nobles.isEmpty())
		{
			return;
		}
		
		try (Connection con = DatabaseManager.getConnection())
		{
			PreparedStatement ps;
			for (Entry<Integer, StatsSet> nobleEntry : nobles.entrySet())
			{
				final StatsSet nobleInfo = nobleEntry.getValue();
				if (nobleInfo == null)
				{
					continue;
				}
				
				int charId = nobleEntry.getKey();
				int classId = nobleInfo.getInteger(CLASS_ID);
				int points = nobleInfo.getInteger(POINTS);
				int compDone = nobleInfo.getInteger(COMP_DONE);
				int compWon = nobleInfo.getInteger(COMP_WON);
				int compLost = nobleInfo.getInteger(COMP_LOST);
				int compDrawn = nobleInfo.getInteger(COMP_DRAWN);
				boolean toSave = nobleInfo.getBool("to_save");
				
				if (toSave)
				{
					ps = con.prepareStatement(OLYMPIAD_SAVE_NOBLES);
					ps.setInt(1, charId);
					ps.setInt(2, classId);
					ps.setInt(3, points);
					ps.setInt(4, compDone);
					ps.setInt(5, compWon);
					ps.setInt(6, compLost);
					ps.setInt(7, compDrawn);
					
					nobleInfo.set("to_save", false);
				}
				else
				{
					ps = con.prepareStatement(OLYMPIAD_UPDATE_NOBLES);
					ps.setInt(1, points);
					ps.setInt(2, compDone);
					ps.setInt(3, compWon);
					ps.setInt(4, compLost);
					ps.setInt(5, compDrawn);
					ps.setInt(6, charId);
				}
				ps.execute();
				ps.close();
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.SEVERE, "Olympiad: Failed to save noblesse data to database: ", e);
		}
	}
	
	/**
	 * Save current olympiad status and update noblesse table in database
	 */
	public void saveOlympiadStatus()
	{
		saveNobleData();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(OLYMPIAD_SAVE_DATA))
		{
			ps.setInt(1, currentCycle);
			ps.setInt(2, period);
			ps.setLong(3, olympiadEnd);
			ps.setLong(4, validationEnd);
			ps.setLong(5, nextWeeklyChange);
			ps.setInt(6, currentCycle);
			ps.setInt(7, period);
			ps.setLong(8, olympiadEnd);
			ps.setLong(9, validationEnd);
			ps.setLong(10, nextWeeklyChange);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.log(Level.SEVERE, "Olympiad: Failed to save olympiad data to database: ", e);
		}
	}
	
	protected void updateMonthlyData()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(OLYMPIAD_MONTH_CLEAR))
			{
				ps.execute();
			}
			try (PreparedStatement ps = con.prepareStatement(OLYMPIAD_MONTH_CREATE))
			{
				ps.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.SEVERE, "Olympiad: Failed to update monthly noblese data: ", e);
		}
	}
	
	protected void sortHeroesToBe()
	{
		heroesToBe.clear();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(OLYMPIAD_GET_HEROS))
		{
			for (ClassId id : ClassId.values())
			{
				if (id.level() != 3)
				{
					continue;
				}
				
				ps.setInt(1, id.getId());
				try (ResultSet rset = ps.executeQuery())
				{
					ps.clearParameters();
					
					if (rset.next())
					{
						StatsSet hero = new StatsSet();
						hero.set(CLASS_ID, id.getId());
						hero.set(CHAR_ID, rset.getInt(CHAR_ID));
						hero.set(CHAR_NAME, rset.getString(CHAR_NAME));
						
						heroesToBe.add(hero);
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning("Olympiad: Couldnt load heroes to be from DB");
		}
	}
	
	public List<String> getClassLeaderBoard(int classId)
	{
		List<String> names = new ArrayList<>();
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(GET_EACH_CLASS_LEADER))
		{
			ps.setInt(1, classId);
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					names.add(rset.getString(CHAR_NAME));
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning("Olympiad: Couldn't load olympiad leaders from DB!");
		}
		return names;
	}
	
	public int getNoblessePointEom(L2PcInstance player, boolean clear)
	{
		if ((player == null) || (period != 1) || noblesEom.isEmpty())
		{
			return 0;
		}
		
		final int objId = player.getObjectId();
		if (!noblesEom.containsKey(objId))
		{
			return 0;
		}
		
		int points = noblesEom.get(objId);
		
		if (clear)
		{
			// clear points in memory
			noblesEom.put(objId, 0);
			// clear points in DB
			try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(REMOVE_CLASSIFIED_NOBLESS_EOM))
			{
				ps.setInt(1, objId);
				ps.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return points;
	}
	
	public int getNoblePoints(int objId)
	{
		if ((nobles == null) || !nobles.containsKey(objId))
		{
			return 0;
		}
		
		return nobles.get(objId).getInteger(POINTS);
	}
	
	public int getCompetitionDone(int objId)
	{
		if ((nobles == null) || !nobles.containsKey(objId))
		{
			return 0;
		}
		
		return nobles.get(objId).getInteger(COMP_DONE);
	}
	
	public int getCompetitionWon(int objId)
	{
		if ((nobles == null) || !nobles.containsKey(objId))
		{
			return 0;
		}
		
		return nobles.get(objId).getInteger(COMP_WON);
	}
	
	public int getCompetitionLost(int objId)
	{
		if ((nobles == null) || !nobles.containsKey(objId))
		{
			return 0;
		}
		
		return nobles.get(objId).getInteger(COMP_LOST);
	}
	
	protected void deleteNobles()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(OLYMPIAD_DELETE_ALL))
		{
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warning("Olympiad: Couldn't delete nobles from DB!");
		}
		nobles.clear();
	}
	
	/**
	 * @param  charId the noble object Id.
	 * @param  data   the stats set data to add.
	 * @return        the old stats set if the noble is already present, null otherwise.
	 */
	protected static StatsSet addNobleStats(int charId, StatsSet data)
	{
		return nobles.put(charId, data);
	}
	
	private static class SingletonHolder
	{
		protected static final Olympiad INSTANCE = new Olympiad();
	}
}
