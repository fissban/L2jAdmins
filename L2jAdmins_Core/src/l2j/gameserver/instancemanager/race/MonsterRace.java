package l2j.gameserver.instancemanager.race;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.DeleteObject;
import l2j.gameserver.network.external.server.MonRaceInfo;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

public class MonsterRace
{
	protected static final Logger LOG = Logger.getLogger(MonsterRace.class.getName());
	
	protected static final List<Integer> npcTemplates = new ArrayList<>(); // List holding npc templates, shuffled on a new race.
	protected static final List<MonsterRaceHistoryInfo> history = new ArrayList<>(); // List holding old race records.
	protected static final Map<Integer, Long> betsPerLane = new ConcurrentHashMap<>(); // Map holding all bets for each lane ; values setted to 0 after every race.
	protected static final List<Double> odds = new ArrayList<>(); // List holding sorted odds per lane ; cleared at new odds calculation.
	
	protected static final int[][] CODE =
	{
		{
			-1,
			0
		},
		{
			0,
			15322
		},
		{
			13765,
			-1
		}
	};
	
	protected static int raceNumber = 0;
	protected static int finalCountdown = 0;
	protected static MonsterRaceState state = MonsterRaceState.RACE_END;
	
	protected static MonRaceInfo packet;
	
	private final L2Npc[] monsters;
	private Constructor<?> constructor;
	private int[][] speeds;
	private final int[] first, second;
	
	protected MonsterRace()
	{
		// Feed history with previous race results.
		loadHistory();
		
		// Feed betsPerLane with stored informations on bets.
		loadBets();
		
		// Feed npcTemplates, we will only have to shuffle it when needed.
		for (int i = 8003; i < 8027; i++)
		{
			npcTemplates.add(i);
		}
		
		monsters = new L2Npc[8];
		speeds = new int[8][20];
		first = new int[2];
		second = new int[2];
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Announcement(), 0, 1000);
	}
	
	private class Announcement implements Runnable
	{
		public Announcement()
		{
		}
		
		@Override
		public void run()
		{
			if (finalCountdown > 1200)
			{
				finalCountdown = 0;
			}
			
			try
			{
				switch (finalCountdown)
				{
					case 0:
						newRace();
						newSpeeds();
						
						state = MonsterRaceState.ACCEPTING_BETS;
						packet = new MonRaceInfo(CODE[0][0], CODE[0][1], getMonsters(), getSpeeds());
						
						toAllPlayersInZone(packet);
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						break;
					
					case 30: // 30 sec
					case 60: // 1 min
					case 90: // 1 min 30 sec
					case 120: // 2 min
					case 150: // 2 min 30
					case 180: // 3 min
					case 210: // 3 min 30
					case 240: // 4 min
					case 270: // 4 min 30 sec
					case 330: // 5 min 30 sec
					case 360: // 6 min
					case 390: // 6 min 30 sec
					case 420: // 7 min
					case 450: // 7 min 30
					case 480: // 8 min
					case 510: // 8 min 30
					case 540: // 9 min
					case 570: // 9 min 30 sec
					case 630: // 10 min 30 sec
					case 660: // 11 min
					case 690: // 11 min 30 sec
					case 720: // 12 min
					case 750: // 12 min 30
					case 780: // 13 min
					case 810: // 13 min 30
					case 870: // 14 min 30 sec
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						break;
					
					case 300: // 5 min
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(10));
						break;
					
					case 600: // 10 min
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(5));
						break;
					
					case 840: // 14 min
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_STOP_IN_S1_MINUTES).addNumber(1));
						break;
					
					case 900: // 15 min
						state = MonsterRaceState.WAITING;
						
						calculateOdds();
						
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE).addNumber(raceNumber));
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_S1_TICKET_SALES_CLOSED));
						break;
					
					case 960: // 16 min
					case 1020: // 17 min
						final int minutes = (finalCountdown == 960) ? 2 : 1;
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_S2_BEGINS_IN_S1_MINUTES).addNumber(minutes));
						break;
					
					case 1050: // 17 min 30 sec
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_S1_BEGINS_IN_30_SECONDS));
						break;
					
					case 1070: // 17 min 50 sec
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_S1_COUNTDOWN_IN_FIVE_SECONDS));
						break;
					
					case 1075: // 17 min 55 sec
					case 1076: // 17 min 56 sec
					case 1077: // 17 min 57 sec
					case 1078: // 17 min 58 sec
					case 1079: // 17 min 59 sec
						final int seconds = 1080 - finalCountdown;
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_BEGINS_IN_S1_SECONDS).addNumber(seconds));
						break;
					
					case 1080: // 18 min
						state = MonsterRaceState.STARTING_RACE;
						packet = new MonRaceInfo(CODE[1][0], CODE[1][1], getMonsters(), getSpeeds());
						
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_RACE_START));
						toAllPlayersInZone(new PlaySound(PlaySoundType.MUSIC_S_RACE));
						toAllPlayersInZone(new PlaySound(PlaySoundType.RACE_START));
						toAllPlayersInZone(packet);
						break;
					
					case 1085: // 18 min 5 sec
						packet = new MonRaceInfo(CODE[2][0], CODE[2][1], getMonsters(), getSpeeds());
						
						toAllPlayersInZone(packet);
						break;
					
					case 1115: // 18 min 35 sec
						state = MonsterRaceState.RACE_END;
						
						// Populate history info with data, stores it in database.
						final MonsterRaceHistoryInfo info = history.get(history.size() - 1);
						info.setFirst(getFirstPlace());
						info.setSecond(getSecondPlace());
						info.setOddRate(odds.get(getFirstPlace() - 1));
						
						saveHistory(info);
						clearBets();
						
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_FIRST_PLACE_S1_SECOND_S2).addNumber(getFirstPlace()).addNumber(getSecondPlace()));
						toAllPlayersInZone(new SystemMessage(SystemMessage.MONSRACE_S1_RACE_END).addNumber(raceNumber));
						break;
					
					case 1140: // 19 min
						toAllPlayersInZone(new DeleteObject(getMonsters()[0]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[1]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[2]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[3]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[4]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[5]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[6]));
						toAllPlayersInZone(new DeleteObject(getMonsters()[7]));
						break;
				}
				finalCountdown += 1;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void newRace()
	{
		raceNumber++;
		// Edit history.
		history.add(new MonsterRaceHistoryInfo(raceNumber, 0, 0, 0));
		// Randomize npcTemplates.
		Collections.shuffle(npcTemplates);
		
		// Setup 8 new creatures ; pickup the first 8 from npcTemplates.
		for (int i = 0; i < 8; i++)
		{
			try
			{
				NpcTemplate template = NpcData.getInstance().getTemplate(npcTemplates.get(i));
				constructor = Class.forName("l2j.gameserver.model.actor.instance." + template.getType() + "Instance").getConstructors()[0];
				int objectId = IdFactory.getInstance().getNextId();
				monsters[i] = (L2Npc) constructor.newInstance(objectId, template);
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "", e);
			}
		}
	}
	
	public void newSpeeds()
	{
		speeds = new int[8][20];
		int total = 0;
		first[1] = 0;
		second[1] = 0;
		
		for (int i = 0; i < 8; i++)
		{
			total = 0;
			for (int j = 0; j < 20; j++)
			{
				if (j == 19)
				{
					speeds[i][j] = 100;
				}
				else
				{
					speeds[i][j] = Rnd.get(60) + 65;
				}
				total += speeds[i][j];
			}
			
			if (total >= first[1])
			{
				second[0] = first[0];
				second[1] = first[1];
				first[0] = 8 - i;
				first[1] = total;
			}
			else if (total >= second[1])
			{
				second[0] = 8 - i;
				second[1] = total;
			}
		}
	}
	
	/**
	 * Load past races informations, feeding history arrayList.<br>
	 * Also sets raceNumber, based on latest HistoryInfo loaded.
	 */
	protected static void loadHistory()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM mdt_history");
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				int raceId = rset.getInt("race_id");
				history.add(new MonsterRaceHistoryInfo(rset.getInt("race_id"), rset.getInt("first"), rset.getInt("second"), rset.getDouble("odd_rate")));
				raceNumber = raceId;
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, "MonsterRace: can't load history: " + e.getMessage(), e);
		}
		LOG.info("MonsterRace : loaded " + raceNumber + " records, currently on race #" + raceNumber + 1);
	}
	
	/**
	 * Save an history record into database.
	 * @param history The infos to store.
	 */
	protected static void saveHistory(MonsterRaceHistoryInfo history)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO mdt_history (race_id, first, second, odd_rate) VALUES (?,?,?,?)"))
		{
			ps.setInt(1, history.getRaceId());
			ps.setInt(2, history.getFirst());
			ps.setInt(3, history.getSecond());
			ps.setDouble(4, history.getOddRate());
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, "MonsterRace: can't save history: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Load current bets per lane ; initialize the map keys.
	 */
	protected static void loadBets()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM mdt_bets");
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				setBetOnLane(rset.getInt("lane_id"), rset.getLong("bet"), false);
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, "MonsterRace: can't load bets: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Save the current lane bet into database.
	 * @param lane : The lane to affect.
	 * @param sum  : The sum to set.
	 */
	protected static void saveBet(int lane, long sum)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO mdt_bets (lane_id, bet) VALUES (?,?)"))
		{
			ps.setInt(1, lane);
			ps.setLong(2, sum);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, "MonsterRace: can't save bet: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Clear all lanes bets, either on database or Map.
	 */
	protected static void clearBets()
	{
		for (int key : betsPerLane.keySet())
		{
			betsPerLane.put(key, 0L);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE mdt_bets SET bet = 0"))
		{
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, "MonsterRace: can't clear bets: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Setup lane bet, based on previous value (if any).
	 * @param lane     : The lane to edit.
	 * @param amount   : The amount to add.
	 * @param saveOnDb : Should it be saved on db or not.
	 */
	public static void setBetOnLane(int lane, long amount, boolean saveOnDb)
	{
		final long sum = (betsPerLane.containsKey(lane)) ? betsPerLane.get(lane) + amount : amount;
		
		betsPerLane.put(lane, sum);
		
		if (saveOnDb)
		{
			saveBet(lane, sum);
		}
	}
	
	/**
	 * Calculate odds for every lane, based on others lanes.
	 */
	protected static void calculateOdds()
	{
		// Clear previous List holding old odds.
		odds.clear();
		
		// Sort bets lanes per lane.
		final Map<Integer, Long> sortedLanes = new TreeMap<>(betsPerLane);
		
		// Pass a first loop in order to calculate total sum of all lanes.
		long sumOfAllLanes = 0;
		for (long amount : sortedLanes.values())
		{
			sumOfAllLanes += amount;
		}
		
		// As we get the sum, we can now calculate the odd rate of each lane.
		for (long amount : sortedLanes.values())
		{
			odds.add((amount == 0) ? 0D : Math.max(1.25, (sumOfAllLanes * 0.7) / amount));
		}
	}
	
	public L2Npc[] getMonsters()
	{
		return monsters;
	}
	
	public int[][] getSpeeds()
	{
		return speeds;
	}
	
	public int getFirstPlace()
	{
		return first[0];
	}
	
	public int getSecondPlace()
	{
		return second[0];
	}
	
	public MonRaceInfo getRacePacket()
	{
		return packet;
	}
	
	public MonsterRaceState getCurrentRaceState()
	{
		return state;
	}
	
	public int getRaceNumber()
	{
		return raceNumber;
	}
	
	public List<MonsterRaceHistoryInfo> getHistory()
	{
		return history;
	}
	
	public List<Double> getOdds()
	{
		return odds;
	}
	
	/**
	 * Send a packet to all players in a specific zone type.
	 * @param packet : The packets to send.
	 */
	public void toAllPlayersInZone(AServerPacket packet)
	{
		L2World.getInstance().getAllPlayers().stream().filter(p -> p.isInsideZone(ZoneType.MONSTERTRACK)).forEach(p -> p.sendPacket(packet));
	}
	
	public static MonsterRace getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MonsterRace INSTANCE = new MonsterRace();
	}
}
