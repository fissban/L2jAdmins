package l2j.gameserver.instancemanager.sevensigns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.PeriodType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.instancemanager.spawn.AutoSpawnManager;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.AutoSpawnHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SignsSky;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;

/**
 * Seven Signs Engine TODO: - Implementation of the Seal of Strife for sieges.
 * @author Tempy
 */
public class SevenSignsManager
{
	protected static final Logger LOG = Logger.getLogger(SevenSignsManager.class.getName());
	
	// Sql
	private static final String RESTORE_SEVEN_SIGN_DATA = "SELECT char_obj_id, cabal, seal, red_stones, green_stones, blue_stones, ancient_adena_amount, contribution_score FROM seven_signs";
	private static final String UPDATE_SEVEN_SIGN_DATA = "UPDATE seven_signs SET cabal=?, seal=?, red_stones=?, green_stones=?, blue_stones=?, ancient_adena_amount=?, contribution_score=? WHERE char_obj_id=?";
	private static final String UPDATE_CHAR_SEVEN_SIGN = "INSERT INTO seven_signs (char_obj_id, cabal, seal) VALUES (?,?,?)";
	
	private static final String RESTORE_SEVEN_SIGN_STATUS = "SELECT * FROM seven_signs_status WHERE id=0";
	private static final String UPDATE_SEVEN_SIGN_STATUS = "UPDATE seven_signs_status SET date=? WHERE id=0";
	
	// Basic Seven Signs Constants
	public static final String SEVEN_SIGNS_HTML_PATH = "data/html/sevenSigns/priest/";
	
	public static final int PERIOD_START_HOUR = 18;
	public static final int PERIOD_START_MINS = 00;
	public static final int PERIOD_START_DAY = Calendar.MONDAY;
	
	// The quest event and seal validation periods last for approximately one week
	// with a 15-minute "interval" period sandwiched between them.
	public static final int PERIOD_MINOR_LENGTH = 900000;
	public static final int PERIOD_MAJOR_LENGTH = 604800000 - PERIOD_MINOR_LENGTH;
	
	public static final int RECORD_SEVEN_SIGNS_ID = 5707;
	public static final int CERTIFICATE_OF_APPROVAL_ID = 6388;
	public static final int RECORD_SEVEN_SIGNS_COST = 500;
	public static final int ADENA_JOIN_DAWN_COST = 50000;
	
	// NPC Related Constants
	public static final int ORATOR_NPC_ID = 8094;
	public static final int PREACHER_NPC_ID = 8093;
	public static final int MAMMON_MERCHANT_ID = 8113;
	public static final int MAMMON_BLACKSMITH_ID = 8126;
	public static final int MAMMON_MARKETEER_ID = 8092;
	public static final int SPIRIT_IN_ID = 8111;
	public static final int SPIRIT_OUT_ID = 8112;
	public static final int LILITH_NPC_ID = 10283;
	public static final int ANAKIM_NPC_ID = 10286;
	
	public static final int CREST_OF_DAWN_ID = 8170;
	public static final int CREST_OF_DUSK_ID = 8171;
	
	// Seal Stone Related Constants
	public static final int SEAL_STONE_BLUE_ID = 6360;
	public static final int SEAL_STONE_GREEN_ID = 6361;
	public static final int SEAL_STONE_RED_ID = 6362;
	
	public static final byte SEAL_STONE_BLUE_VALUE = 3;
	public static final byte SEAL_STONE_GREEN_VALUE = 5;
	public static final byte SEAL_STONE_RED_VALUE = 10;
	
	public static final byte BLUE_CONTRIB_POINTS = 3;
	public static final byte GREEN_CONTRIB_POINTS = 5;
	public static final byte RED_CONTRIB_POINTS = 10;
	
	private final Calendar calendar = Calendar.getInstance();
	
	protected PeriodType activePeriod;
	protected int currentCycle;
	
	protected double dawnStoneScore;
	protected double duskStoneScore;
	
	protected int dawnFestivalScore;
	protected int duskFestivalScore;
	
	protected int compWinner;
	protected CabalType previousWinner;
	
	private final Map<Integer, StatsSet> signsPlayerData = new HashMap<>();
	private final Map<SealType, CabalType> signsSealOwners = new HashMap<>();
	
	private final Map<SealType, Integer> signsDuskSealTotals = new HashMap<>();
	private final Map<SealType, Integer> signsDawnSealTotals = new HashMap<>();
	
	// Spawns
	private static AutoSpawnHolder merchantSpawn;
	private static AutoSpawnHolder blacksmithSpawn;
	private static AutoSpawnHolder spiritInSpawn;
	private static AutoSpawnHolder spiritOutSpawn;
	private static AutoSpawnHolder lilithSpawn;
	private static AutoSpawnHolder anakimSpawn;
	private static AutoSpawnHolder crestOfDawnSpawn;
	private static AutoSpawnHolder crestOfDuskSpawn;
	private static Map<Integer, AutoSpawnHolder> oratorSpawns;
	private static Map<Integer, AutoSpawnHolder> preacherSpawns;
	private static Map<Integer, AutoSpawnHolder> marketeerSpawns;
	
	public SevenSignsManager()
	{
		signsPlayerData.clear();
		signsSealOwners.clear();
		signsDuskSealTotals.clear();
		signsDawnSealTotals.clear();
		
		try
		{
			restoreSevenSignsData();
		}
		catch (Exception e)
		{
			LOG.severe("SevenSigns: Failed to load configuration: " + e);
		}
		
		LOG.info("SevenSigns: Currently in the " + activePeriod.getName() + " period!");
		initializeSeals();
		
		if (isSealValidationPeriod())
		{
			if (getCabalHighestScore() == CabalType.NULL)
			{
				LOG.info("SevenSigns: The competition ended with a tie last week.");
			}
			else
			{
				LOG.info("SevenSigns: The " + getCabalHighestScore().getName() + " were victorious last week.");
			}
		}
		else if (getCabalHighestScore() == CabalType.NULL)
		{
			LOG.info("SevenSigns: The competition, if the current trend continues, will end in a tie this week.");
		}
		else
		{
			LOG.info("SevenSigns: The " + getCabalHighestScore().getName() + " are in the lead this week.");
		}
		
		setCalendarForNextPeriodChange();
		long milliToChange = getMilliToPeriodChange();
		
		// Schedule a time for the next period change.
		ThreadPoolManager.schedule(new SevenSignsPeriodChange(), milliToChange);
		
		// Thanks to http://rainbow.arch.scriptmania.com/scripts/timezone_countdown.html for help with this.
		double numSecs = (milliToChange / 1000) % 60;
		double countDown = ((milliToChange / 1000) - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);
		
		LOG.info("SevenSigns: Next period begins in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		
		spawnSevenSignsNpc();
	}
	
	/**
	 * Registers all random spawns and auto-chats for Seven Signs NPCs, along with spawns for the Preachers of Doom and Orators of Revelations at the beginning of the Seal Validation period.
	 */
	public void spawnSevenSignsNpc()
	{
		merchantSpawn = AutoSpawnManager.getInstance().getSpawns(MAMMON_MERCHANT_ID, false);
		blacksmithSpawn = AutoSpawnManager.getInstance().getSpawns(MAMMON_BLACKSMITH_ID, false);
		spiritInSpawn = AutoSpawnManager.getInstance().getSpawns(SPIRIT_IN_ID, false);
		spiritOutSpawn = AutoSpawnManager.getInstance().getSpawns(SPIRIT_OUT_ID, false);
		lilithSpawn = AutoSpawnManager.getInstance().getSpawns(LILITH_NPC_ID, false);
		anakimSpawn = AutoSpawnManager.getInstance().getSpawns(ANAKIM_NPC_ID, false);
		crestOfDawnSpawn = AutoSpawnManager.getInstance().getSpawns(CREST_OF_DAWN_ID, false);
		crestOfDuskSpawn = AutoSpawnManager.getInstance().getSpawns(CREST_OF_DUSK_ID, false);
		oratorSpawns = AutoSpawnManager.getInstance().getSpawns(ORATOR_NPC_ID);
		preacherSpawns = AutoSpawnManager.getInstance().getSpawns(PREACHER_NPC_ID);
		marketeerSpawns = AutoSpawnManager.getInstance().getSpawns(MAMMON_MARKETEER_ID);
		
		AutoSpawnManager.getInstance().setSpawn(marketeerSpawns, true);
		
		if (isSealValidationPeriod() || isCompResultsPeriod())
		{
			if ((getSealOwner(SealType.GNOSIS) == getCabalHighestScore()) && (getSealOwner(SealType.GNOSIS) != CabalType.NULL))
			{
				AutoSpawnManager.getInstance().setSpawn(blacksmithSpawn, true);
				AutoSpawnManager.getInstance().setSpawn(oratorSpawns, true);
				AutoSpawnManager.getInstance().setSpawn(preacherSpawns, true);
				
				if (!Config.ANNOUNCE_MAMMON_SPAWN)
				{
					blacksmithSpawn.setBroadcast(false);
				}
			}
			else
			{
				AutoSpawnManager.getInstance().setSpawn(blacksmithSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(oratorSpawns, false);
				AutoSpawnManager.getInstance().setSpawn(preacherSpawns, false);
			}
			
			if ((getSealOwner(SealType.AVARICE) == getCabalHighestScore()) && (getSealOwner(SealType.AVARICE) != CabalType.NULL))
			{
				AutoSpawnManager.getInstance().setSpawn(merchantSpawn, true);
				AutoSpawnManager.getInstance().setSpawn(spiritInSpawn, true);
				AutoSpawnManager.getInstance().setSpawn(spiritOutSpawn, true);
				
				if (!Config.ANNOUNCE_MAMMON_SPAWN)
				{
					merchantSpawn.setBroadcast(false);
				}
				
				switch (getCabalHighestScore())
				{
					case DAWN:
						AutoSpawnManager.getInstance().setSpawn(lilithSpawn, true);
						AutoSpawnManager.getInstance().setSpawn(anakimSpawn, false);
						AutoSpawnManager.getInstance().setSpawn(crestOfDawnSpawn, true);
						AutoSpawnManager.getInstance().setSpawn(crestOfDuskSpawn, false);
						break;
					
					case DUSK:
						AutoSpawnManager.getInstance().setSpawn(anakimSpawn, true);
						AutoSpawnManager.getInstance().setSpawn(lilithSpawn, false);
						AutoSpawnManager.getInstance().setSpawn(crestOfDuskSpawn, true);
						AutoSpawnManager.getInstance().setSpawn(crestOfDawnSpawn, false);
						break;
				}
			}
			else
			{
				AutoSpawnManager.getInstance().setSpawn(merchantSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(lilithSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(anakimSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(crestOfDawnSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(crestOfDuskSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(spiritInSpawn, false);
				AutoSpawnManager.getInstance().setSpawn(spiritOutSpawn, false);
			}
		}
		else
		{
			AutoSpawnManager.getInstance().setSpawn(merchantSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(lilithSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(anakimSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(crestOfDawnSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(crestOfDuskSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(spiritInSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(spiritOutSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(blacksmithSpawn, false);
			AutoSpawnManager.getInstance().setSpawn(oratorSpawns, false);
			AutoSpawnManager.getInstance().setSpawn(preacherSpawns, false);
		}
	}
	
	private static int calcContributionScore(int blueCount, int greenCount, int redCount)
	{
		return (blueCount * BLUE_CONTRIB_POINTS) + (greenCount * GREEN_CONTRIB_POINTS) + (redCount * RED_CONTRIB_POINTS);
	}
	
	public static int calcAncientAdenaReward(int blueCount, int greenCount, int redCount)
	{
		return (blueCount * SEAL_STONE_BLUE_VALUE) + (greenCount * SEAL_STONE_GREEN_VALUE) + (redCount * SEAL_STONE_RED_VALUE);
	}
	
	public final int getCurrentCycle()
	{
		return currentCycle;
	}
	
	public final PeriodType getCurrentPeriod()
	{
		return activePeriod;
	}
	
	public void setCurrentPeriod(PeriodType period)
	{
		activePeriod = period;
	}
	
	private final int getDaysToPeriodChange()
	{
		int numDays = calendar.get(Calendar.DAY_OF_WEEK) - PERIOD_START_DAY;
		
		if (numDays < 0)
		{
			return 0 - numDays;
		}
		
		return 7 - numDays;
	}
	
	public final long getMilliToPeriodChange()
	{
		long currTimeMillis = System.currentTimeMillis();
		long changeTimeMillis = calendar.getTimeInMillis();
		
		return (changeTimeMillis - currTimeMillis);
	}
	
	protected void setCalendarForNextPeriodChange()
	{
		// Calculate the number of days until the next period
		// A period starts at 18:00 pm (local time), like on official servers.
		switch (getCurrentPeriod())
		{
			case SEAL_VALIDATION:
			case COMPETITION:
				int daysToChange = getDaysToPeriodChange();
				
				if (daysToChange == 7)
				{
					if (calendar.get(Calendar.HOUR_OF_DAY) < PERIOD_START_HOUR)
					{
						daysToChange = 0;
					}
					else if ((calendar.get(Calendar.HOUR_OF_DAY) == PERIOD_START_HOUR) && (calendar.get(Calendar.MINUTE) < PERIOD_START_MINS))
					{
						daysToChange = 0;
					}
				}
				
				// Otherwise...
				if (daysToChange > 0)
				{
					calendar.add(Calendar.DATE, daysToChange);
				}
				
				calendar.set(Calendar.HOUR_OF_DAY, PERIOD_START_HOUR);
				calendar.set(Calendar.MINUTE, PERIOD_START_MINS);
				break;
			case RECRUITING:
			case RESULTS:
				calendar.add(Calendar.MILLISECOND, PERIOD_MINOR_LENGTH);
				break;
		}
	}
	
	public final boolean isSealValidationPeriod()
	{
		return (activePeriod == PeriodType.SEAL_VALIDATION);
	}
	
	public final boolean isCompResultsPeriod()
	{
		return (activePeriod == PeriodType.RESULTS);
	}
	
	public final int getCurrentScore(CabalType cabal)
	{
		double totalStoneScore = dawnStoneScore + duskStoneScore;
		
		switch (cabal)
		{
			case DAWN:
				return Math.round((float) (dawnStoneScore / ((float) totalStoneScore == 0 ? 1 : totalStoneScore)) * 500) + dawnFestivalScore;
			case DUSK:
				return Math.round((float) (duskStoneScore / ((float) totalStoneScore == 0 ? 1 : totalStoneScore)) * 500) + duskFestivalScore;
		}
		
		return 0;
	}
	
	public final double getCurrentStoneScore(CabalType cabal)
	{
		switch (cabal)
		{
			case DAWN:
				return dawnStoneScore;
			case DUSK:
				return duskStoneScore;
		}
		
		return 0;
	}
	
	public final int getCurrentFestivalScore(CabalType cabal)
	{
		switch (cabal)
		{
			case DAWN:
				return dawnFestivalScore;
			case DUSK:
				return duskFestivalScore;
		}
		
		return 0;
	}
	
	public final CabalType getCabalHighestScore()
	{
		if (getCurrentScore(CabalType.DUSK) == getCurrentScore(CabalType.DAWN))
		{
			return CabalType.NULL;
		}
		else if (getCurrentScore(CabalType.DUSK) > getCurrentScore(CabalType.DAWN))
		{
			return CabalType.DUSK;
		}
		else
		{
			return CabalType.DAWN;
		}
	}
	
	public final CabalType getSealOwner(SealType seal)
	{
		return signsSealOwners.get(seal);
	}
	
	public final int getSealProportion(SealType currSeal, CabalType cabal)
	{
		switch (cabal)
		{
			case DUSK:
				return signsDuskSealTotals.get(currSeal);
			case DAWN:
				return signsDawnSealTotals.get(currSeal);
		}
		
		return 0;
	}
	
	public final int getTotalMembers(CabalType cabal)
	{
		int cabalMembers = 0;
		for (StatsSet sevenDat : signsPlayerData.values())
		{
			if (sevenDat.getString("cabal").equals(cabal.getShortName()))
			{
				cabalMembers++;
			}
		}
		
		return cabalMembers;
	}
	
	public final StatsSet getPlayerData(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return null;
		}
		
		return signsPlayerData.get(player.getObjectId());
	}
	
	public int getPlayerStoneContrib(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return 0;
		}
		
		StatsSet currPlayer = getPlayerData(player);
		
		int stoneCount = 0;
		stoneCount += currPlayer.getInteger("red_stones");
		stoneCount += currPlayer.getInteger("green_stones");
		stoneCount += currPlayer.getInteger("blue_stones");
		
		return stoneCount;
	}
	
	public int getPlayerContribScore(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return 0;
		}
		
		StatsSet currPlayer = getPlayerData(player);
		
		return currPlayer.getInteger("contribution_score");
	}
	
	public int getPlayerAdenaCollect(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return 0;
		}
		
		return signsPlayerData.get(player.getObjectId()).getInteger("ancient_adena_amount");
	}
	
	public SealType getPlayerSeal(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return SealType.NULL;
		}
		
		return getPlayerData(player).getEnum("seal", SealType.class);
	}
	
	public CabalType getPlayerCabal(L2PcInstance player)
	{
		if (!hasRegisteredBefore(player))
		{
			return CabalType.NULL;
		}
		
		switch (getPlayerData(player).getString("cabal"))
		{
			case "dawn":
				return CabalType.DAWN;
			case "dusk":
				return CabalType.DUSK;
			default:
				return CabalType.NULL;
		}
	}
	
	/**
	 * Restores all Seven Signs data and settings, usually called at server startup.
	 */
	protected void restoreSevenSignsData()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(RESTORE_SEVEN_SIGN_DATA);
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int charObjId = rset.getInt("char_obj_id");
					
					StatsSet sevenDat = new StatsSet();
					sevenDat.set("char_obj_id", charObjId);
					sevenDat.set("cabal", rset.getString("cabal"));
					sevenDat.set("seal", SealType.values()[rset.getInt("seal")]);
					sevenDat.set("red_stones", rset.getInt("red_stones"));
					sevenDat.set("green_stones", rset.getInt("green_stones"));
					sevenDat.set("blue_stones", rset.getInt("blue_stones"));
					sevenDat.set("ancient_adena_amount", rset.getDouble("ancient_adena_amount"));
					sevenDat.set("contribution_score", rset.getDouble("contribution_score"));
					
					if (Config.DEBUG)
					{
						LOG.info("SevenSigns: Loaded data from DB for char ID " + charObjId + " (" + sevenDat.getString("cabal") + ")");
					}
					
					signsPlayerData.put(charObjId, sevenDat);
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(RESTORE_SEVEN_SIGN_STATUS);
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					// TODO se podrian ajustar los sql para q cargen los enums en lugar de usar "int"
					
					currentCycle = rset.getInt("current_cycle");
					activePeriod = PeriodType.values()[rset.getInt("active_period")];
					previousWinner = CabalType.values()[rset.getInt("previous_winner")];
					
					dawnStoneScore = rset.getDouble("dawn_stone_score");
					dawnFestivalScore = rset.getInt("dawn_festival_score");
					duskStoneScore = rset.getDouble("dusk_stone_score");
					duskFestivalScore = rset.getInt("dusk_festival_score");
					
					signsSealOwners.put(SealType.AVARICE, CabalType.values()[rset.getInt("avarice_owner")]);
					signsSealOwners.put(SealType.GNOSIS, CabalType.values()[rset.getInt("gnosis_owner")]);
					signsSealOwners.put(SealType.STRIFE, CabalType.values()[rset.getInt("strife_owner")]);
					
					signsDawnSealTotals.put(SealType.AVARICE, rset.getInt("avarice_dawn_score"));
					signsDawnSealTotals.put(SealType.GNOSIS, rset.getInt("gnosis_dawn_score"));
					signsDawnSealTotals.put(SealType.STRIFE, rset.getInt("strife_dawn_score"));
					signsDuskSealTotals.put(SealType.AVARICE, rset.getInt("avarice_dusk_score"));
					signsDuskSealTotals.put(SealType.GNOSIS, rset.getInt("gnosis_dusk_score"));
					signsDuskSealTotals.put(SealType.STRIFE, rset.getInt("strife_dusk_score"));
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(UPDATE_SEVEN_SIGN_STATUS))
			{
				ps.setInt(1, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				ps.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.severe("SevenSigns: Unable to load Seven Signs data from database: " + e);
		}
		
		// Festival data is loaded now after the Seven Signs engine data.
	}
	
	/**
	 * Saves all Seven Signs data, both to the database and properties file (if updateSettings = True). Often called to preserve data integrity and synchronization with DB, in case of errors. <BR>
	 * If player != null, just that player's data is updated in the database, otherwise all player's data is sequentially updated.
	 * @param player
	 * @param updateSettings
	 */
	public void saveSevenSignsData(L2PcInstance player, boolean updateSettings)
	{
		if (Config.DEBUG)
		{
			LOG.info("SevenSigns: Saving data to disk.");
		}
		
		try (Connection con = DatabaseManager.getConnection())
		{
			for (StatsSet sevenDat : signsPlayerData.values())
			{
				if (player != null)
				{
					if (sevenDat.getInteger("char_obj_id") != player.getObjectId())
					{
						continue;
					}
				}
				
				try (PreparedStatement ps = con.prepareStatement(UPDATE_SEVEN_SIGN_DATA))
				{
					ps.setString(1, sevenDat.getString("cabal"));
					ps.setInt(2, sevenDat.getEnum("seal", SealType.class).ordinal());
					ps.setInt(3, sevenDat.getInteger("red_stones"));
					ps.setInt(4, sevenDat.getInteger("green_stones"));
					ps.setInt(5, sevenDat.getInteger("blue_stones"));
					ps.setDouble(6, sevenDat.getDouble("ancient_adena_amount"));
					ps.setDouble(7, sevenDat.getDouble("contribution_score"));
					ps.setInt(8, sevenDat.getInteger("char_obj_id"));
					ps.execute();
				}
				
				if (Config.DEBUG)
				{
					LOG.info("SevenSigns: Updated data in database for char ID " + sevenDat.getInteger("char_obj_id") + " (" + sevenDat.getString("cabal") + ")");
				}
			}
			
			if (updateSettings)
			{
				String sqlQuery = "UPDATE seven_signs_status SET current_cycle=?, active_period=?, previous_winner=?, dawn_stone_score=?, dawn_festival_score=?, dusk_stone_score=?, dusk_festival_score=?, avarice_owner=?, gnosis_owner=?, strife_owner=?, avarice_dawn_score=?, gnosis_dawn_score=?, strife_dawn_score=?, avarice_dusk_score=?, gnosis_dusk_score=?, strife_dusk_score=?, festival_cycle=?, ";
				
				for (int i = 0; i < (SevenSignsFestival.FESTIVAL_COUNT); i++)
				{
					sqlQuery += "accumulated_bonus" + String.valueOf(i) + "=?, ";
				}
				
				sqlQuery += "date=? WHERE id=0";
				
				try (PreparedStatement ps = con.prepareStatement(sqlQuery))
				{
					ps.setInt(1, currentCycle);
					ps.setInt(2, activePeriod.ordinal());
					ps.setInt(3, previousWinner.ordinal());
					ps.setDouble(4, dawnStoneScore);
					ps.setInt(5, dawnFestivalScore);
					ps.setDouble(6, duskStoneScore);
					ps.setInt(7, duskFestivalScore);
					ps.setInt(8, signsSealOwners.get(SealType.AVARICE).ordinal());
					ps.setInt(9, signsSealOwners.get(SealType.GNOSIS).ordinal());
					ps.setInt(10, signsSealOwners.get(SealType.STRIFE).ordinal());
					ps.setInt(11, signsDawnSealTotals.get(SealType.AVARICE));
					ps.setInt(12, signsDawnSealTotals.get(SealType.GNOSIS));
					ps.setInt(13, signsDawnSealTotals.get(SealType.STRIFE));
					ps.setInt(14, signsDuskSealTotals.get(SealType.AVARICE));
					ps.setInt(15, signsDuskSealTotals.get(SealType.GNOSIS));
					ps.setInt(16, signsDuskSealTotals.get(SealType.STRIFE));
					ps.setInt(17, SevenSignsFestival.getInstance().getCurrentFestivalCycle());
					
					for (int i = 0; i < SevenSignsFestival.FESTIVAL_COUNT; i++)
					{
						ps.setInt(18 + i, SevenSignsFestival.getInstance().getAccumulatedBonus(i));
					}
					
					ps.setInt(18 + SevenSignsFestival.FESTIVAL_COUNT, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
					ps.execute();
				}
				
				if (Config.DEBUG)
				{
					LOG.info("SevenSigns: Updated data in database.");
				}
			}
		}
		catch (Exception e)
		{
			LOG.severe("SevenSigns: Unable to save data to database: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Used to reset the cabal details of all players, and update the database.<BR>
	 * Primarily used when beginning a new cycle, and should otherwise never be called.
	 */
	protected void resetPlayerData()
	{
		if (Config.DEBUG)
		{
			LOG.info("SevenSigns: Resetting player data for new event period.");
		}
		
		// Reset each player's contribution data as well as seal and cabal.
		for (StatsSet sevenDat : signsPlayerData.values())
		{
			int charObjId = sevenDat.getInteger("char_obj_id");
			
			// Reset the player's cabal and seal information
			sevenDat.set("cabal", "");
			sevenDat.set("seal", SealType.NULL);
			sevenDat.set("contribution_score", 0);
			
			signsPlayerData.put(charObjId, sevenDat);
		}
	}
	
	/**
	 * Tests whether the specified player has joined a cabal in the past.
	 * @param  player
	 * @return        boolean hasRegistered
	 */
	private boolean hasRegisteredBefore(L2PcInstance player)
	{
		return signsPlayerData.containsKey(player.getObjectId());
	}
	
	/**
	 * Used to specify cabal-related details for the specified player. This method checks to see if the player has registered before and will update the database if necessary. <BR>
	 * @param  player
	 * @param  chosenCabal
	 * @param  chosenSeal
	 * @return             s the cabal ID the player has joined.
	 */
	public CabalType setPlayerInfo(L2PcInstance player, CabalType chosenCabal, SealType chosenSeal)
	{
		final int charObjId = player.getObjectId();
		
		StatsSet currPlayerData = getPlayerData(player);
		
		if (currPlayerData != null)
		{
			// If the seal validation period has passed,
			// cabal information was removed and so "re-register" player
			currPlayerData.set("cabal", chosenCabal.getShortName());
			currPlayerData.set("seal", chosenSeal);
			
			signsPlayerData.put(charObjId, currPlayerData);
		}
		else
		{
			currPlayerData = new StatsSet();
			currPlayerData.set("char_obj_id", charObjId);
			currPlayerData.set("cabal", chosenCabal.getShortName());
			currPlayerData.set("seal", chosenSeal);
			currPlayerData.set("red_stones", 0);
			currPlayerData.set("green_stones", 0);
			currPlayerData.set("blue_stones", 0);
			currPlayerData.set("ancient_adena_amount", 0);
			currPlayerData.set("contribution_score", 0);
			
			signsPlayerData.put(charObjId, currPlayerData);
			
			// Update data in database, as we have a new player signing up.
			try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_CHAR_SEVEN_SIGN))
			{
				ps.setInt(1, charObjId);
				ps.setString(2, chosenCabal.getShortName());
				ps.setInt(3, chosenSeal.ordinal()); // TODO seguimos almacenando en la DB un valor de tipo int, podria ajustarse en un futuro
				ps.execute();
				
				if (Config.DEBUG)
				{
					LOG.info("SevenSigns: Inserted data in DB for char ID " + currPlayerData.getInteger("char_obj_id") + " (" + currPlayerData.getString("cabal") + ")");
				}
			}
			catch (SQLException e)
			{
				LOG.severe("SevenSigns: Failed to save data: " + e);
			}
		}
		
		// Increasing Seal total score for the player chosen Seal.
		if (currPlayerData.getString("cabal").equals("dawn"))
		{
			signsDawnSealTotals.put(chosenSeal, signsDawnSealTotals.get(chosenSeal) + 1);
		}
		else
		{
			signsDuskSealTotals.put(chosenSeal, signsDuskSealTotals.get(chosenSeal) + 1);
		}
		
		saveSevenSignsData(player, true);
		
		if (Config.DEBUG)
		{
			LOG.info("SevenSigns: " + player.getName() + " has joined the " + chosenCabal.getName() + " for the " + chosenSeal.getName() + "!");
		}
		
		return chosenCabal;
	}
	
	/**
	 * If removeReward = True, all the ancient adena owed to them is removed, then DB is updated.
	 * @param  player
	 * @param  removeReward
	 * @return              the amount of ancient adena the specified player can claim, if any.
	 */
	public int getAncientAdenaReward(L2PcInstance player, boolean removeReward)
	{
		StatsSet currPlayer = getPlayerData(player);
		int rewardAmount = currPlayer.getInteger("ancient_adena_amount");
		
		currPlayer.set("red_stones", 0);
		currPlayer.set("green_stones", 0);
		currPlayer.set("blue_stones", 0);
		currPlayer.set("ancient_adena_amount", 0);
		
		if (removeReward)
		{
			signsPlayerData.put(player.getObjectId(), currPlayer);
			saveSevenSignsData(player, true);
		}
		
		return rewardAmount;
	}
	
	/**
	 * Used to add the specified player's seal stone contribution points to the current total for their cabal. Returns the point score the contribution was worth. Each stone count <B>must be</B> broken down and specified by the stone's color.
	 * @param  player
	 * @param  blueCount
	 * @param  greenCount
	 * @param  redCount
	 * @return            int contribScore
	 */
	public int addPlayerStoneContrib(L2PcInstance player, int blueCount, int greenCount, int redCount)
	{
		StatsSet currPlayer = getPlayerData(player);
		
		int contribScore = calcContributionScore(blueCount, greenCount, redCount);
		int totalAncientAdena = currPlayer.getInteger("ancient_adena_amount") + calcAncientAdenaReward(blueCount, greenCount, redCount);
		int totalContribScore = currPlayer.getInteger("contribution_score") + contribScore;
		
		if (totalContribScore > Config.ALT_MAXIMUM_PLAYER_CONTRIB)
		{
			return -1;
		}
		
		currPlayer.set("red_stones", currPlayer.getInteger("red_stones") + redCount);
		currPlayer.set("green_stones", currPlayer.getInteger("green_stones") + greenCount);
		currPlayer.set("blue_stones", currPlayer.getInteger("blue_stones") + blueCount);
		currPlayer.set("ancient_adena_amount", totalAncientAdena);
		currPlayer.set("contribution_score", totalContribScore);
		signsPlayerData.put(player.getObjectId(), currPlayer);
		
		switch (getPlayerCabal(player))
		{
			case DAWN:
				dawnStoneScore += contribScore;
				break;
			case DUSK:
				duskStoneScore += contribScore;
				break;
		}
		
		saveSevenSignsData(player, true);
		
		if (Config.DEBUG)
		{
			LOG.info("SevenSigns: " + player.getName() + " contributed " + contribScore + " seal stone points to their cabal.");
		}
		
		return contribScore;
	}
	
	/**
	 * Adds the specified number of festival points to the specified cabal. Remember, the same number of points are <B>deducted from the rival cabal</B> to maintain proportionality.
	 * @param cabal
	 * @param amount
	 * @param subtractPoints
	 */
	public void addFestivalScore(CabalType cabal, int amount, int subtractPoints)
	{
		if (cabal == CabalType.DUSK)
		{
			duskFestivalScore += amount;
			
			// To prevent negative scores!
			// if (dawnFestivalScore >= amount)
			// {
			dawnFestivalScore -= subtractPoints;
			// }
		}
		else
		{
			dawnFestivalScore += amount;
			
			// if (duskFestivalScore >= amount)
			// {
			duskFestivalScore -= subtractPoints;
			// }
		}
	}
	
	/**
	 * Send info on the current Seven Signs period to the specified player.
	 * @param player
	 */
	public void sendCurrentPeriodMsg(L2PcInstance player)
	{
		SystemMessage sm = null;
		
		switch (getCurrentPeriod())
		{
			case RECRUITING:
				sm = new SystemMessage(SystemMessage.PREPARATIONS_PERIOD_BEGUN);
				break;
			case COMPETITION:
				sm = new SystemMessage(SystemMessage.COMPETITION_PERIOD_BEGUN);
				break;
			case RESULTS:
				sm = new SystemMessage(SystemMessage.RESULTS_PERIOD_BEGUN);
				break;
			case SEAL_VALIDATION:
				sm = new SystemMessage(SystemMessage.VALIDATION_PERIOD_BEGUN);
				break;
		}
		
		player.sendPacket(sm);
	}
	
	/**
	 * Used to initialize the seals for each cabal. (Used at startup or at beginning of a new cycle). This method should be called after <B>resetSeals()</B> and <B>calcNewSealOwners()</B> on a new cycle.
	 */
	public void initializeSeals()
	{
		for (SealType currSeal : signsSealOwners.keySet())
		{
			final CabalType sealOwner = signsSealOwners.get(currSeal);
			
			if (sealOwner != CabalType.NULL)
			{
				if (isSealValidationPeriod())
				{
					LOG.info("SevenSigns: The " + sealOwner.getName() + " have won the " + currSeal.getName() + ".");
				}
				else
				{
					LOG.info("SevenSigns: The " + currSeal.getName() + " is currently owned by " + sealOwner.getName() + ".");
				}
			}
			else
			{
				LOG.info("SevenSigns: The " + currSeal.getName() + " remains unclaimed.");
			}
		}
	}
	
	/**
	 * Only really used at the beginning of a new cycle, this method resets all seal-related data.
	 */
	protected void resetSeals()
	{
		signsDawnSealTotals.put(SealType.AVARICE, 0);
		signsDawnSealTotals.put(SealType.GNOSIS, 0);
		signsDawnSealTotals.put(SealType.STRIFE, 0);
		signsDuskSealTotals.put(SealType.AVARICE, 0);
		signsDuskSealTotals.put(SealType.GNOSIS, 0);
		signsDuskSealTotals.put(SealType.STRIFE, 0);
	}
	
	/**
	 * Calculates the ownership of the three Seals of the Seven Signs, based on various criterion. Should only ever called at the beginning of a new cycle.
	 */
	public void calcNewSealOwners()
	{
		for (SealType currSeal : signsDawnSealTotals.keySet())
		{
			CabalType prevSealOwner = signsSealOwners.get(currSeal);
			CabalType newSealOwner = CabalType.NULL;
			int dawnProportion = getSealProportion(currSeal, CabalType.DAWN);
			int totalDawnMembers = getTotalMembers(CabalType.DAWN) == 0 ? 1 : getTotalMembers(CabalType.DAWN);
			int dawnPercent = Math.round(((float) dawnProportion / (float) totalDawnMembers) * 100);
			int duskProportion = getSealProportion(currSeal, CabalType.DUSK);
			int totalDuskMembers = getTotalMembers(CabalType.DUSK) == 0 ? 1 : getTotalMembers(CabalType.DUSK);
			int duskPercent = Math.round(((float) duskProportion / (float) totalDuskMembers) * 100);
			
			/*
			 * - If a Seal was already closed or owned by the opponent and the new winner wants to assume ownership of the Seal, 35% or more of the members of the Cabal must have chosen the Seal. If they chose less than 35%, they cannot own the Seal. - If the Seal was owned by the winner in the
			 * previous Seven Signs, they can retain that seal if 10% or more members have chosen it. If they want to possess a new Seal, at least 35% of the members of the Cabal must have chosen the new Seal.
			 */
			switch (prevSealOwner)
			{
				case NULL:
					switch (getCabalHighestScore())
					{
						case NULL:
							newSealOwner = CabalType.NULL;
							break;
						case DAWN:
							if (dawnPercent >= 35)
							{
								newSealOwner = CabalType.DAWN;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
						case DUSK:
							if (duskPercent >= 35)
							{
								newSealOwner = CabalType.DUSK;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
					}
					break;
				case DAWN:
					switch (getCabalHighestScore())
					{
						case NULL:
							if (dawnPercent >= 10)
							{
								newSealOwner = CabalType.DAWN;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
						case DAWN:
							if (dawnPercent >= 10)
							{
								newSealOwner = CabalType.DAWN;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
						case DUSK:
							if (duskPercent >= 35)
							{
								newSealOwner = CabalType.DUSK;
							}
							else if (dawnPercent >= 10)
							{
								newSealOwner = CabalType.DAWN;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
					}
					break;
				case DUSK:
					switch (getCabalHighestScore())
					{
						case NULL:
							if (duskPercent >= 10)
							{
								newSealOwner = CabalType.DUSK;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
						case DAWN:
							if (dawnPercent >= 35)
							{
								newSealOwner = CabalType.DAWN;
							}
							else if (duskPercent >= 10)
							{
								newSealOwner = CabalType.DUSK;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
						case DUSK:
							if (duskPercent >= 10)
							{
								newSealOwner = CabalType.DUSK;
							}
							else
							{
								newSealOwner = CabalType.NULL;
							}
							break;
					}
					break;
			}
			
			signsSealOwners.put(currSeal, newSealOwner);
			
			// Alert all online players to new seal status.
			switch (currSeal)
			{
				case AVARICE:
					if (newSealOwner == CabalType.DAWN)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DAWN_OBTAINED_AVARICE);
					}
					else if (newSealOwner == CabalType.DUSK)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DUSK_OBTAINED_AVARICE);
					}
					break;
				case GNOSIS:
					if (newSealOwner == CabalType.DAWN)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DAWN_OBTAINED_GNOSIS);
					}
					else if (newSealOwner == CabalType.DUSK)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DUSK_OBTAINED_GNOSIS);
					}
					break;
				case STRIFE:
					if (newSealOwner == CabalType.DAWN)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DAWN_OBTAINED_STRIFE);
					}
					else if (newSealOwner == CabalType.DUSK)
					{
						Broadcast.toAllOnlinePlayers(SystemMessage.DUSK_OBTAINED_STRIFE);
					}
					
					CastleData.getInstance().validateTaxes(newSealOwner);
					break;
			}
		}
	}
	
	/**
	 * The primary controller of period change of the Seven Signs system. This runs all related tasks depending on the period that is about to begin.
	 * @author Tempy
	 */
	public class SevenSignsPeriodChange implements Runnable
	{
		public SevenSignsPeriodChange()
		{
			//
		}
		
		@Override
		public void run()
		{
			switch (getCurrentPeriod())
			{
				case RECRUITING: // Initialization
					// The period increases
					activePeriod = PeriodType.COMPETITION;
					// Start the Festival of Darkness cycle.
					SevenSignsFestival.getInstance().startFestivalManager();
					// Send message that Competition has begun.
					Broadcast.toAllOnlinePlayers(SystemMessage.QUEST_EVENT_PERIOD_BEGUN);
					break;
				
				case COMPETITION: // Results Calculation
					// The period increases
					activePeriod = PeriodType.RESULTS;
					// Send message that Competition has ended.
					Broadcast.toAllOnlinePlayers(SystemMessage.QUEST_EVENT_PERIOD_ENDED);
					// Schedule a stop of the festival engine.
					SevenSignsFestival.getInstance().getFestivalManagerSchedule().cancel(false);
					calcNewSealOwners();
					
					CabalType compWinner = getCabalHighestScore();
					switch (compWinner)
					{
						case DAWN:
							Broadcast.toAllOnlinePlayers(SystemMessage.DAWN_WON);
							break;
						
						case DUSK:
							Broadcast.toAllOnlinePlayers(SystemMessage.DUSK_WON);
							break;
					}
					
					previousWinner = compWinner;
					break;
				
				case RESULTS: // Seal Validation
					// The period increases
					activePeriod = PeriodType.SEAL_VALIDATION;
					// Perform initial Seal Validation set up.
					initializeSeals();
					// Send message that Seal Validation has begun.
					Broadcast.toAllOnlinePlayers(SystemMessage.SEAL_VALIDATION_PERIOD_BEGUN);
					LOG.info("SevenSigns: The " + previousWinner.getName() + " have won the competition with " + getCurrentScore(previousWinner) + " points!");
					break;
				
				case SEAL_VALIDATION: // Reset for New Cycle
					// Ensure a cycle restart when this period ends.
					activePeriod = PeriodType.RECRUITING;
					// Send message that Seal Validation has ended.
					Broadcast.toAllOnlinePlayers(SystemMessage.SEAL_VALIDATION_PERIOD_ENDED);
					// Reset all data
					resetPlayerData();
					resetSeals();
					// Reset all Festival-related data and remove any unused blood offerings.
					// NOTE: A full update of Festival data in the database is also performed.
					SevenSignsFestival.getInstance().resetFestivalData(false);
					
					dawnStoneScore = 0;
					duskStoneScore = 0;
					dawnFestivalScore = 0;
					duskFestivalScore = 0;
					currentCycle++;
					break;
			}
			
			// Make sure all Seven Signs data is saved for future use.
			saveSevenSignsData(null, true);
			
			teleLosingCabalFromDungeons(getCabalHighestScore().getShortName());
			
			// Spawns NPCs and change sky color.
			Broadcast.toAllOnlinePlayers(new SignsSky());
			spawnSevenSignsNpc();
			
			LOG.info("SevenSigns: The " + activePeriod.getName() + " period has begun!");
			
			setCalendarForNextPeriodChange();
			
			ThreadPoolManager.schedule(new SevenSignsPeriodChange(), getMilliToPeriodChange());
		}
	}
	
	/**
	 * This method is called to remove all players from catacombs and necropolises, who belong to the losing cabal.<BR>
	 * <b>Should only ever called at the beginning of Seal Validation.</b>
	 * @param compWinner
	 */
	public void teleLosingCabalFromDungeons(String compWinner)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player == null)
			{
				continue;
			}
			
			StatsSet currPlayer = signsPlayerData.get(player.getObjectId());
			
			if (isSealValidationPeriod() || isCompResultsPeriod())
			{
				if (!player.isGM() && player.isIn7sDungeon() && ((currPlayer == null) || !currPlayer.getString("cabal").equals(compWinner)))
				{
					player.teleToLocation(TeleportWhereType.TOWN);
					player.setIsIn7sDungeon(false);
					player.sendMessage("You have been teleported to the nearest town due to the beginning of the Seal Validation period.");
				}
			}
			else
			{
				if (!player.isGM() && player.isIn7sDungeon() && ((currPlayer == null) || !currPlayer.getString("cabal").isEmpty()))
				{
					player.teleToLocation(TeleportWhereType.TOWN);
					player.setIsIn7sDungeon(false);
					player.sendMessage("You have been teleported to the nearest town because you have not signed for any cabal.");
				}
			}
		}
	}
	
	public static SevenSignsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SevenSignsManager INSTANCE = new SevenSignsManager();
	}
}
