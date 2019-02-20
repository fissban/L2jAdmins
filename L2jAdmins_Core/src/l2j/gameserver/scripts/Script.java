package l2j.gameserver.scripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.util.Rnd;

/**
 * Quest main class.
 * @author Luis Arias
 */
public class Script
{
	protected static final Logger LOG = Logger.getLogger(Script.class.getName());
	
	private static final String HTML_NONE_AVAILABLE = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
	private static final String HTML_ALREADY_COMPLETED = "<html><body>This quest has already been completed.</body></html>";
	private static final String HTML_TOO_MUCH_QUESTS = "<html><body>You have already accepted the maximum number of quests. No more than 25 quests may be undertaken simultaneously.<br>For quest information, enter Alt+U.</body></html>";
	
	private final Map<Integer, List<ScripTask>> eventTimers = new ConcurrentHashMap<>();
	
	private final Set<Integer> scriptInvolvedNpcs = new HashSet<>();
	
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final WriteLock writeLock = rwLock.writeLock();
	private final ReadLock readLock = rwLock.readLock();
	
	private final int id;
	private final String descr;
	private boolean onEnterWorld = false;
	private int[] itemsIds;
	
	/**
	 * (Constructor)Add values to class variables and put the quest in HashMaps.
	 * @param id    : int pointing out the ID of the quest
	 * @param descr : String for the description of the quest
	 */
	public Script(int id, String descr)
	{
		this.id = id;
		this.descr = descr;
	}
	
	/**
	 * Return ID of the quest.
	 * @return int
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Return type of the quest.
	 * @return boolean : True for (live) quest, False for script, AI, etc.
	 */
	public boolean isRealQuest()
	{
		return id > 0;
	}
	
	/**
	 * Return name of the quest.
	 * @return String
	 */
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	/**
	 * Return description of the quest.
	 * @return String
	 */
	public String getDescr()
	{
		return descr;
	}
	
	public void setOnEnterWorld(boolean val)
	{
		onEnterWorld = val;
	}
	
	public boolean getOnEnterWorld()
	{
		return onEnterWorld;
	}
	
	/**
	 * Return registered quest items.
	 * @return int[]
	 */
	public int[] getRegisterItemsIds()
	{
		return itemsIds;
	}
	
	/**
	 * Registers all items that have to be destroyed in case player abort the quest or finish it.
	 * @param itemIds
	 */
	public void registerItems(int... itemIds)
	{
		itemsIds = itemIds;
	}
	
	/**
	 * Add a new QuestState to the database and return it.
	 * @param  player
	 * @return        QuestState : QuestState created
	 */
	public ScriptState newState(L2PcInstance player)
	{
		return new ScriptState(player, this, ScriptStateType.CREATED);
	}
	
	/**
	 * Add quests to the L2PcInstance of the player.<BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap quest of L2PcInstance
	 * @param player : Player who is entering the world
	 */
	public final static void playerEnter(L2PcInstance player)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id = ? AND name = ?");
			PreparedStatement invalidQuestDataVar = con.prepareStatement("DELETE FROM character_quests WHERE char_id = ? AND name = ? AND var = ?");
			PreparedStatement ps1 = con.prepareStatement("SELECT name, value FROM character_quests WHERE char_id = ? AND var = ?"))
		{
			// Get list of quests owned by the player from database
			
			ps1.setInt(1, player.getObjectId());
			ps1.setString(2, "<state>");
			try (ResultSet rs = ps1.executeQuery())
			{
				while (rs.next())
				{
					// Get name of the quest and Id of its state
					String questName = rs.getString("name");
					ScriptStateType state = ScriptStateType.values()[rs.getByte("value")];
					
					// Search quest associated with the Id
					Script s = ScriptsData.get(questName);
					if (s == null)
					{
						LOG.finer("Unknown quest " + questName + " for player " + player.getName());
						if (Config.AUTODELETE_INVALID_QUEST_DATA)
						{
							invalidQuestData.setInt(1, player.getObjectId());
							invalidQuestData.setString(2, questName);
							invalidQuestData.executeUpdate();
						}
						continue;
					}
					
					// Create a new QuestState for the player that will be added to the player's list of quests
					new ScriptState(player, s, state);
				}
			}
			
			// Get list of quests owned by the player from the DB in order to add variables used in the quest.
			try (PreparedStatement ps = con.prepareStatement("SELECT name, var, value FROM character_quests WHERE char_id = ? AND var <> ?"))
			{
				ps.setInt(1, player.getObjectId());
				ps.setString(2, "<state>");
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						String questName = rs.getString("name");
						String var = rs.getString("var");
						String value = rs.getString("value");
						// Get the QuestState saved in the loop before
						ScriptState qs = player.getScriptState(questName);
						if (qs == null)
						{
							LOG.finer("Lost variable " + var + " in quest " + questName + " for player " + player.getName());
							if (Config.AUTODELETE_INVALID_QUEST_DATA)
							{
								invalidQuestDataVar.setInt(1, player.getObjectId());
								invalidQuestDataVar.setString(2, questName);
								invalidQuestDataVar.setString(3, var);
								invalidQuestDataVar.executeUpdate();
							}
							continue;
						}
						// Add parameter to the quest
						qs.setInternal(var, value);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not insert char quest:", e);
		}
	}
	
	/**
	 * @param  player : The player to make checks on.
	 * @param  object : to take range reference from
	 * @return        A random party member or the passed player if he has no party.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Object object)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
		{
			return null;
		}
		
		// No party or no object, return player.
		if ((object == null) || !player.isInParty())
		{
			return player;
		}
		
		// Player's party.
		List<L2PcInstance> members = new ArrayList<>();
		for (L2PcInstance member : player.getParty().getMembers())
		{
			if (member.isInsideRadius(object, 1600, true, false)) // Config.ALT_PARTY_RANGE
			{
				members.add(member);
			}
		}
		
		// No party members, return. (note: player is party member too, in most cases he is included in members too)
		if (members.isEmpty())
		{
			return null;
		}
		
		// Random party member.
		return members.get(Rnd.get(members.size()));
	}
	
	/**
	 * Auxiliary function for party quests. Checks the player's condition. Player member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  var    : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param  value  : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return        QuestState : The QuestState of that player.
	 */
	public ScriptState checkPlayerCondition(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
		{
			return null;
		}
		
		// Check player's quest conditions.
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		// Condition exists? Condition has correct value?
		if ((st.get(var) == null) || !value.equalsIgnoreCase(st.get(var)))
		{
			return null;
		}
		
		// Invalid npc instance?
		if (npc == null)
		{
			return null;
		}
		
		// Player is in range?
		if (!player.isInsideRadius(npc, 1600, true, false)) // Config.ALT_PARTY_RANGE
		{
			return null;
		}
		
		return st;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  var    : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param  value  : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return        List<L2PcInstance> : List of party members that matches the specified condition, empty list if none matches. If the var is null, empty list is returned (i.e. no condition is applied). The party member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null,
	 *                distance condition is ignored.
	 */
	public List<L2PcInstance> getPartyMembers(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// Output list.
		List<L2PcInstance> candidates = new ArrayList<>();
		
		// Valid player instance is passed and player is in a party? Check party.
		if ((player != null) && player.isInParty())
		{
			// Filter candidates from player's party.
			for (L2PcInstance partyMember : player.getParty().getMembers())
			{
				if (partyMember == null)
				{
					continue;
				}
				
				// Check party members' quest condition.
				if (checkPlayerCondition(partyMember, npc, var, value) != null)
				{
					candidates.add(partyMember);
				}
			}
		}
		// Player is solo, check the player
		else if (checkPlayerCondition(player, npc, var, value) != null)
		{
			candidates.add(player);
		}
		
		return candidates;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  var    : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @param  value  : a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return        L2PcInstance : L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, null is returned (i.e. no condition is applied). The party member must be within 1500 distance from the npc. If npc is null, distance condition
	 *                is ignored.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc, String var, String value)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
		{
			return null;
		}
		
		// Get all candidates fulfilling the condition.
		final List<L2PcInstance> candidates = getPartyMembers(player, npc, var, value);
		
		// No candidate, return.
		if (candidates.isEmpty())
		{
			return null;
		}
		
		// Return random candidate.
		return candidates.get(Rnd.get(candidates.size()));
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  value  : the value of the "cond" variable that must be matched
	 * @return        L2PcInstance : L2PcInstance for a random party member that matches the specified condition, or null if no match.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc, int value)
	{
		return getRandomPartyMember(player, npc, "cond", String.valueOf(value));
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  value  : the value of the "cond" variable that must be matched
	 * @return        L2PcInstance : L2PcInstance for a random party member that matches the specified condition, or null if no match.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc, String value)
	{
		return getRandomPartyMember(player, npc, "cond", value);
	}
	
	/**
	 * Auxiliary function for party quests. Checks the player's condition. Player member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  state  : the state in which the party member's QuestState must be in order to be considered.
	 * @return        QuestState : The QuestState of that player.
	 */
	public ScriptState checkPlayerState(L2PcInstance player, L2Npc npc, ScriptStateType state)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
		{
			return null;
		}
		
		// Check player's quest conditions.
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		// State correct?
		if (st.getState() != state)
		{
			return null;
		}
		
		// Invalid npc instance?
		if (npc == null)
		{
			return null;
		}
		
		// Player is in range?
		if (!player.isInsideRadius(npc, 1600, true, false)) // Config.ALT_PARTY_RANGE
		{
			return null;
		}
		
		return st;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a L2Npc to compare distance
	 * @param  state  : the state in which the party member's QuestState must be in order to be considered.
	 * @return        List<L2PcInstance> : List of party members that matches the specified quest state, empty list if none matches. The party member must be within Config.ALT_PARTY_RANGE distance from the npc. If npc is null, distance condition is ignored.
	 */
	public List<L2PcInstance> getPartyMembersState(L2PcInstance player, L2Npc npc, ScriptStateType state)
	{
		// Output list.
		List<L2PcInstance> candidates = new ArrayList<>();
		
		// Valid player instance is passed and player is in a party? Check party.
		if ((player != null) && player.isInParty())
		{
			// Filter candidates from player's party.
			for (L2PcInstance partyMember : player.getParty().getMembers())
			{
				if (partyMember == null)
				{
					continue;
				}
				
				// Check party members' quest state.
				if (checkPlayerState(partyMember, npc, state) != null)
				{
					candidates.add(partyMember);
				}
			}
		}
		// Player is solo, check the player
		else if (checkPlayerState(player, npc, state) != null)
		{
			candidates.add(player);
		}
		
		return candidates;
	}
	
	/**
	 * Auxiliary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own.
	 * @param  player : the instance of a player whose party is to be searched
	 * @param  npc    : the instance of a monster to compare distance
	 * @param  state  : the state in which the party member's QuestState must be in order to be considered.
	 * @return        L2PcInstance: L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, any random party member is returned (i.e. no condition is applied).
	 */
	public L2PcInstance getRandomPartyMemberState(L2PcInstance player, L2Npc npc, ScriptStateType state)
	{
		// No valid player instance is passed, there is nothing to check.
		if (player == null)
		{
			return null;
		}
		
		// Get all candidates fulfilling the condition.
		final List<L2PcInstance> candidates = getPartyMembersState(player, npc, state);
		
		// No candidate, return.
		if (candidates.isEmpty())
		{
			return null;
		}
		
		// Return random candidate.
		return candidates.get(Rnd.get(candidates.size()));
	}
	
	/**
	 * Retrieves the clan leader quest state.
	 * @param  player : the player to test
	 * @param  npc    : the npc to test distance
	 * @return        the QuestState of the leader, or null if not found
	 */
	public ScriptState getClanLeaderQuestState(L2PcInstance player, L2Npc npc)
	{
		// If player is the leader, retrieves directly the qS and bypass others checks
		if (player.isClanLeader() && player.isInsideRadius(npc, 1600, true, false)) // Config.ALT_PARTY_RANGE
		{
			return player.getScriptState(getName());
		}
		
		// Verify if the player got a clan
		Clan clan = player.getClan();
		if (clan == null)
		{
			return null;
		}
		
		// Verify if the leader is online
		L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (leader == null)
		{
			return null;
		}
		
		// Verify if the player is on the radius of the leader. If true, send leader's quest state.
		if (leader.isInsideRadius(npc, 1600, true, false)) // Config.ALT_PARTY_RANGE
		{
			return leader.getScriptState(getName());
		}
		
		return null;
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time time in ms for when to fire the timer
	 */
	public void startTimer(String name, long time)
	{
		startTimer(name, time, null, null, false);
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time time in ms for when to fire the timer
	 * @param npc  npc associated with this timer (can be null)
	 */
	public void startTimer(String name, long time, L2Npc npc)
	{
		startTimer(name, time, npc, null, false);
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name   name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time   time in ms for when to fire the timer
	 * @param npc    npc associated with this timer (can be null)
	 * @param player player associated with this timer (can be null)
	 */
	public void startTimer(String name, long time, L2Npc npc, L2PcInstance player)
	{
		startTimer(name, time, npc, player, false);
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name      name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time      time in ms for when to fire the timer
	 * @param npc       npc associated with this timer (can be null)
	 * @param player    player associated with this timer (can be null)
	 * @param repeating indicates if the timer is repeatable or one-time.
	 */
	public void startTimer(String name, long time, L2Npc npc, L2PcInstance player, boolean repeating)
	{
		// Get quest timers for this timer type.
		List<ScripTask> timers = eventTimers.get(name.hashCode());
		if (timers == null)
		{
			writeLock.lock();
			try
			{
				// None timer exists, create new list.
				timers = new CopyOnWriteArrayList<>();
				
				// Add new timer to the list.
				timers.add(new ScripTask(this, name, npc, player, time, repeating));
				
				// Add timer list to the map.
				eventTimers.put(name.hashCode(), timers);
			}
			finally
			{
				writeLock.unlock();
			}
		}
		else
		{
			// Check, if specific timer already exists.
			for (ScripTask timer : timers)
			{
				// If so, return.
				if ((timer != null) && timer.equals(this, name, npc, player))
				{
					return;
				}
			}
			
			writeLock.lock();
			try
			{
				// Add new timer to the list.
				timers.add(new ScripTask(this, name, npc, player, time, repeating));
			}
			finally
			{
				writeLock.unlock();
			}
		}
	}
	
	// TODO revisar si los resultados son obtenidos
	public ScripTask getTimer(String name)
	{
		return getTimer(name, null, null);
	}
	
	public ScripTask getTimer(String name, L2PcInstance player)
	{
		return getTimer(name, null, player);
	}
	
	public ScripTask getTimer(String name, L2Npc npc, L2PcInstance player)
	{
		// Get quest timers for this timer type.
		List<ScripTask> timers = eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if ((timers == null) || timers.isEmpty())
		{
			return null;
		}
		
		readLock.lock();
		try
		{
			// Check, if specific timer exists.
			for (ScripTask timer : timers)
			{
				// If so, return him.
				if ((timer != null) && timer.equals(this, name, npc, player))
				{
					return timer;
				}
			}
		}
		finally
		{
			readLock.unlock();
		}
		
		return null;
	}
	
	public void cancelTimer(String name, L2Npc npc, L2PcInstance player)
	{
		// If specified timer exists, cancel him.
		ScripTask timer = getTimer(name, npc, player);
		if (timer != null)
		{
			timer.cancel();
		}
	}
	
	public void cancelTimers(String name)
	{
		// Get quest timers for this timer type.
		List<ScripTask> timers = eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if ((timers == null) || timers.isEmpty())
		{
			return;
		}
		
		// Cancel all quest timers.
		for (ScripTask timer : timers)
		{
			writeLock.lock();
			try
			{
				if (timer != null)
				{
					timer.cancel();
				}
			}
			finally
			{
				writeLock.unlock();
			}
		}
	}
	
	// Note, keep it default. It is used withing QuestTimer, when it terminates.
	/**
	 * Removes QuestTimer from timer list, when it terminates.
	 * @param timer : QuestTimer, which is beeing terminated.
	 */
	void removeTimer(ScripTask timer)
	{
		// Timer does not exist, return.
		if (timer == null)
		{
			return;
		}
		
		// Get quest timers for this timer type.
		List<ScripTask> timers = eventTimers.get(timer.getName().hashCode());
		
		// Timer list does not exists or is empty, return.
		if ((timers == null) || timers.isEmpty())
		{
			return;
		}
		
		// Remove timer from the list.
		writeLock.lock();
		try
		{
			timers.remove(timer);
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	public Map<Integer, List<ScripTask>> getQuestTimers()
	{
		return eventTimers;
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param  npcId the NPC template to spawn.
	 * @param  cha   the position where to spawn it.
	 * @return       instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, L2Character cha)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param  npcId the NPC template to spawn.
	 * @param  x
	 * @param  y
	 * @param  z
	 * @return       instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, int x, int y, int z)
	{
		return addSpawn(npcId, x, y, z, 0, false, 0);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param  npcId   the NPC template to spawn.
	 * @param  x
	 * @param  y
	 * @param  z
	 * @param  heading
	 * @return         instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading)
	{
		return addSpawn(npcId, x, y, z, heading, false, 0);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param  npcId        the NPC template to spawn.
	 * @param  cha          the position where to spawn it.
	 * @param  randomOffset
	 * @param  despawnDelay
	 * @return              instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, L2Character cha, boolean randomOffset, long despawnDelay)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), randomOffset, despawnDelay);
	}
	
	/**
	 * Add a temporary (quest) spawn on the Location object.
	 * @param  npcId        the NPC template to spawn.
	 * @param  loc          the position where to spawn it.
	 * @param  randomOffset
	 * @param  despawnDelay
	 * @return              instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, LocationHolder loc, boolean randomOffset, long despawnDelay)
	{
		return addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, despawnDelay);
	}
	
	/**
	 * Add a temporary (quest) spawn on the location of a character.
	 * @param  npcId        the NPC template to spawn.
	 * @param  x
	 * @param  y
	 * @param  z
	 * @param  heading
	 * @param  randomOffset
	 * @param  despawnDelay
	 * @return              instance of the newly spawned npc with summon animation.
	 */
	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay)
	{
		L2Npc npc = null;
		try
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			
			// Sometimes, even if the quest script specifies some xyz (for example npc.getX() etc) by the time the code
			// reaches here, xyz have become 0! Also, a questdev might have purposely set xy to 0,0...however,
			// the spawn code is coded such that if x=y=0, it looks into location for the spawn loc! This will NOT work
			// with quest spawns! For both of the above cases, we need a fail-safe spawn. For this, we use the
			// default spawn location, which is at the player's loc.
			if ((x == 0) && (y == 0))
			{
				LOG.log(Level.SEVERE, "Failed to adjust bad locks for quest spawn!  Spawn aborted!");
				return null;
			}
			
			if (randomOffset)
			{
				x += Rnd.get(-100, 100);
				y += Rnd.get(-100, 100);
			}
			
			Spawn spawn = new Spawn(template);
			spawn.setHeading(heading);
			spawn.setX(x);
			spawn.setY(y);
			spawn.setZ(z + 20);
			spawn.stopRespawn();
			npc = spawn.doSpawn();// isSummonSpawn
			
			if (despawnDelay > 0)
			{
				npc.scheduleDespawn(despawnDelay);
			}
		}
		catch (Exception e1)
		{
			LOG.warning("Could not spawn Npc " + npcId);
		}
		
		return npc;
	}
	
	/**
	 * @return default html page "You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements."
	 */
	public static String getNoQuestMsg()
	{
		return HTML_NONE_AVAILABLE;
	}
	
	/**
	 * @return default html page "This quest has already been completed."
	 */
	public static String getAlreadyCompletedMsg()
	{
		return HTML_ALREADY_COMPLETED;
	}
	
	/**
	 * @return default html page "You have already accepted the maximum number of quests. No more than 25 quests may be undertaken simultaneously. For quest information, enter Alt+U."
	 */
	public static String getTooMuchQuestsMsg()
	{
		return HTML_TOO_MUCH_QUESTS;
	}
	
	/**
	 * Show a message to player.<BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :
	 * <UL>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
	 * <LI><U>"res" starts with "<html>" :</U> the message hold in "res" is shown in a dialog box</LI>
	 * <LI><U>otherwise :</U> the message held in "res" is shown in chat box</LI>
	 * </UL>
	 * @param  npc    : which launches the dialog, null in case of random scripts
	 * @param  player : the player.
	 * @param  result : String pointing out the message to show at the player
	 * @return        boolean
	 */
	public boolean showResult(L2Npc npc, L2PcInstance player, String result)
	{
		if ((player == null) || (result == null) || result.isEmpty())
		{
			return false;
		}
		
		if (result.endsWith(".htm") || result.endsWith(".html"))
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
			if (isRealQuest())
			{
				npcReply.setFile("./data/html/quests/" + getName() + "/" + result);
			}
			else
			{
				npcReply.setFile("./" + result);
			}
			
			if (npc != null)
			{
				npcReply.replace("%objectId%", String.valueOf(npc.getObjectId()));
			}
			
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (result.startsWith("<html>"))
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
			npcReply.setHtml(result);
			
			if (npc != null)
			{
				npcReply.replace("%objectId%", String.valueOf(npc.getObjectId()));
			}
			
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			player.sendMessage(result);
		}
		
		return true;
	}
	
	/**
	 * Show message error to player who has an access level greater than 0
	 * @param  player : L2PcInstance
	 * @param  e      : Throwable
	 * @return        boolean
	 */
	public boolean showError(L2PcInstance player, Throwable e)
	{
		LOG.log(Level.WARNING, "Quest: Error in " + getName(), e);
		
		if (e.getMessage() == null)
		{
			e.printStackTrace();
		}
		
		if ((player != null) && player.isGM())
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(0);
			npcReply.setHtml("<html><body>" + e.getMessage() + "</body></html>");
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns String representation of given quest html.
	 * @param  fileName : the filename to send.
	 * @return          String : message sent to client.
	 */
	public String getHtmlText(String fileName)
	{
		if (isRealQuest())
		{
			return HtmData.getInstance().getHtmForce("./data/html/quests/" + getName() + "/" + fileName);
		}
		
		return HtmData.getInstance().getHtmForce("./data/html/" + getDescr() + "/" + getName() + "/" + fileName);
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for the specified Event type.
	 * @param npcId     : id of the NPC to register
	 * @param eventType : type of event being registered
	 */
	public void addEventId(int npcId, ScriptEventType eventType)
	{
		try
		{
			final NpcTemplate t = NpcData.getInstance().getTemplate(npcId);
			if (t != null)
			{
				t.addScriptEvent(eventType, this);
				scriptInvolvedNpcs.add(npcId);
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Exception on addEventId(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * Add the quest to the NPC's startQuest
	 * @param npcIds A serie of ids.
	 */
	public void addStartNpc(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.QUEST_START);
		}
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for Attack Events.
	 * @param npcIds A serie of ids.
	 */
	public void addAttackId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_ATTACK);
		}
	}
	
	/**
	 * Quest event notifycator for player's or player's pet attack.
	 * @param  npc      Attacked npc instance.
	 * @param  attacker Attacker or pet owner.
	 * @param  damage   Given damage.
	 * @param  isPet    Player summon attacked?
	 * @return          boolean
	 */
	public final boolean notifyAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		String res = null;
		try
		{
			res = onAttack(npc, attacker, damage, isPet);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(npc, attacker, res);
	}
	
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Character See Events.
	 * @param npcIds : A serie of ids.
	 */
	public void addAggroRangeEnterId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_AGGRO_RANGE_ENTER);
		}
	}
	
	private class TmpOnAggroEnter implements Runnable
	{
		private final L2Npc npc;
		private final L2PcInstance pc;
		private final boolean isPet;
		
		public TmpOnAggroEnter(L2Npc npc, L2PcInstance pc, boolean isPet)
		{
			this.npc = npc;
			this.pc = pc;
			this.isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onAggroRangeEnter(npc, pc, isPet);
			}
			catch (Exception e)
			{
				showError(pc, e);
			}
			
			showResult(npc, pc, res);
		}
	}
	
	public final boolean notifyAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ThreadPoolManager.execute(new TmpOnAggroEnter(npc, player, isPet));
		return true;
	}
	
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkill(L2Npc npc, L2PcInstance player, Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkill(npc, player, skill);
			if (res == "true")
			{
				return true;
			}
			else if (res == "false")
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkill(L2Npc npc, L2PcInstance player, Skill skill)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkillInfo(L2Npc npc, L2PcInstance player, Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkillInfo(npc, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkillInfo(L2Npc npc, L2PcInstance player, Skill skill)
	{
		return null;
	}
	
	public final boolean notifyAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAcquireSkillList(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	public final boolean notifyDeath(L2Character killer, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onDeath(killer, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		if (killer instanceof L2Npc)
		{
			return showResult((L2Npc) killer, player, res);
		}
		
		return showResult(null, player, res);
	}
	
	public String onDeath(L2Character killer, L2PcInstance player)
	{
		if (killer instanceof L2Npc)
		{
			return onAdvEvent("", (L2Npc) killer, player);
		}
		
		return onAdvEvent("", null, player);
	}
	
	public final boolean notifyEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAdvEvent(event, npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// if not overridden by a subclass, then default to the returned value of the simpler (and older) onEvent override
		// if the player has a state, use it as parameter in the next call, else return null
		if (player != null)
		{
			ScriptState qs = player.getScriptState(getName());
			if (qs != null)
			{
				return onEvent(event, qs);
			}
		}
		return null;
	}
	
	public String onEvent(String event, ScriptState qs)
	{
		return null;
	}
	
	public final boolean notifyEnterWorld(L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onEnterWorld(player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(null, player, res);
	}
	
	public String onEnterWorld(L2PcInstance player)
	{
		return null;
	}
	
	// TODO sera implementado junto al rework de la lectura y el sistema de zonas
	// /**
	// * Add this quest to the list of quests that triggers, when player enters specified zones.
	// * @param zoneIds : A serie of zone ids.
	// */
	// public void addEnterZoneId(int... zoneIds)
	// {
	// for (int zoneId : zoneIds)
	// {
	// final Zone zone = ZoneData.getInstance().getZoneId(zoneId);
	// if (zone != null)
	// {
	// zone.addQuestEvent(QuestEventType.ON_ENTER_ZONE, this);
	// }
	// }
	// }
	
	// public final boolean notifyEnterZone(L2Character character, L2ZoneType zone)
	// {
	// L2PcInstance player = character.getActingPlayer();
	// String res = null;
	// try
	// {
	// res = onEnterZone(character, zone);
	// }
	// catch (Exception e)
	// {
	// if (player != null)
	// {
	// return showError(player, e);
	// }
	// }
	// if (player != null)
	// {
	// return showResult(null, player, res);
	// }
	// return true;
	// }
	//
	// public String onEnterZone(L2Character character, L2ZoneType zone)
	// {
	// return null;
	// }
	
	// /**
	// * Add this quest to the list of quests that triggers, when player leaves specified zones.
	// * @param zoneIds : A serie of zone ids.
	// */
	// public void addExitZoneId(int... zoneIds)
	// {
	// for (int zoneId : zoneIds)
	// {
	// final L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
	// if (zone != null)
	// {
	// zone.addQuestEvent(QuestEventType.ON_EXIT_ZONE, this);
	// }
	// }
	// }
	//
	// public final boolean notifyExitZone(L2Character character, L2ZoneType zone)
	// {
	// L2PcInstance player = character.getActingPlayer();
	// String res = null;
	// try
	// {
	// res = onExitZone(character, zone);
	// }
	// catch (Exception e)
	// {
	// if (player != null)
	// {
	// return showError(player, e);
	// }
	// }
	// if (player != null)
	// {
	// return showResult(null, player, res);
	// }
	// return true;
	// }
	//
	// public String onExitZone(L2Character character, L2ZoneType zone)
	// {
	// return null;
	// }
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Faction Call Events.
	 * @param npcIds : A serie of ids.
	 */
	public void addFactionCallId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_FACTION_CALL);
		}
	}
	
	public final boolean notifyFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		String res = null;
		try
		{
			res = onFactionCall(npc, caller, attacker, isPet);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(npc, attacker, res);
	}
	
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add the quest to the NPC's first-talk (default action dialog)
	 * @param npcIds A serie of ids.
	 */
	public void addFirstTalkId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_FIRST_TALK);
		}
	}
	
	public final boolean notifyFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		
		// if the quest returns text to display, display it.
		if ((res != null) && (res.length() > 0))
		{
			return showResult(npc, player, res);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return true;
	}
	
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	/**
	 * Add the quest to an array of items templates.
	 * @param itemIds A serie of ids.
	 */
	public void addItemUse(int... itemIds)
	{
		for (int itemId : itemIds)
		{
			Item t = ItemData.getInstance().getTemplate(itemId);
			if (t != null)
			{
				t.addQuestEvent(this);
			}
		}
	}
	
	public final boolean notifyItemUse(ItemInstance item, L2PcInstance player, L2Object target)
	{
		String res = null;
		try
		{
			res = onItemUse(item, player, target);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(null, player, res);
	}
	
	public String onItemUse(ItemInstance item, L2PcInstance player, L2Object target)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for Kill Events.
	 * @param killIds A serie of ids.
	 */
	public void addKillId(int... killIds)
	{
		for (int killId : killIds)
		{
			addEventId(killId, ScriptEventType.ON_KILL);
		}
	}
	
	public final boolean notifyKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		String res = null;
		try
		{
			res = onKill(npc, killer, isPet);
		}
		catch (Exception e)
		{
			return showError(killer, e);
		}
		return showResult(npc, killer, res);
	}
	
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Spawn Events.
	 * @param npcIds : A serie of ids.
	 */
	public void addSpawnId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_SPAWN);
		}
	}
	
	public final boolean notifySpawn(L2Npc npc)
	{
		try
		{
			onSpawn(npc);
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Exception on onSpawn() in notifySpawn(): " + e.getMessage(), e);
			return true;
		}
		return false;
	}
	
	public String onSpawn(L2Npc npc)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Skill-See Events.
	 * @param npcIds : A serie of ids.
	 */
	public void addSkillSeeId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_SKILL_SEE);
		}
	}
	
	public class TmpOnSkillSee implements Runnable
	{
		private final L2Npc npc;
		private final L2PcInstance caster;
		private final Skill skill;
		private final List<L2Object> targets;
		private final boolean isPet;
		
		public TmpOnSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets2, boolean isPet)
		{
			this.npc = npc;
			this.caster = caster;
			this.skill = skill;
			targets = targets2;
			this.isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onSkillSee(npc, caster, skill, targets, isPet);
			}
			catch (Exception e)
			{
				showError(caster, e);
			}
			showResult(npc, caster, res);
			
		}
	}
	
	public final boolean notifySkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isPet)
	{
		ThreadPoolManager.execute(new TmpOnSkillSee(npc, caster, skill, targets, isPet));
		return true;
	}
	
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isPet)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to any skill being used by other npcs or players.
	 * @param npcIds : A serie of ids.
	 */
	public void addSpellFinishedId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, ScriptEventType.ON_SPELL_FINISHED);
		}
	}
	
	public final boolean notifySpellFinished(L2Npc npc, L2PcInstance player, Skill skill)
	{
		String res = null;
		try
		{
			res = onSpellFinished(npc, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(npc, player, res);
	}
	
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill)
	{
		return null;
	}
	
	/**
	 * Add this quest to the list of quests that the passed npc will respond to for Talk Events.
	 * @param talkIds : A serie of ids.
	 */
	public void addTalkId(int... talkIds)
	{
		for (int talkId : talkIds)
		{
			addEventId(talkId, ScriptEventType.ON_TALK);
		}
	}
	
	public final boolean notifyTalk(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onTalk(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		player.setLastQuestNpcObject(npc.getObjectId());
		return showResult(npc, player, res);
	}
	
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		return null;
	}
	
	public Set<Integer> getQuestInvolvedNpcs()
	{
		return scriptInvolvedNpcs;
	}
	
	private String patch = "";
	
	public void setFilePatch(String patch)
	{
		this.patch = patch;
	}
	
	public void reload()
	{
		unload();
		
		try
		{
			patch.getClass().getDeclaredConstructor().newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean unload()
	{
		return unload(true);
	}
	
	public boolean unload(boolean removeFromList)
	{
		for (List<ScripTask> timers : eventTimers.values())
		{
			readLock.lock();
			try
			{
				timers.forEach(timer -> timer.cancel());
			}
			finally
			{
				readLock.unlock();
			}
			timers.clear();
		}
		
		eventTimers.clear();
		
		for (int npcId : scriptInvolvedNpcs)
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			if (template != null)
			{
				template.removeScript(this);
			}
		}
		
		scriptInvolvedNpcs.clear();
		
		if (removeFromList)
		{
			return ScriptsData.remove(this);
		}
		
		return true;
	}
	
	/**
	 * Insert (or update) in the database variables that need to stay persistent for this quest after a reboot.<br>
	 * This function is for storage of values that do not related to a specific player but are global for all characters.<br>
	 * For example, if we need to disable a quest-gatekeeper until a certain time (as is done with some grand-boss gatekeepers), we can save that time in the DB.
	 * @param var   the name of the variable to save
	 * @param value the value of the variable
	 */
	public final void saveGlobalQuestVar(String var, String value)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO quest_global_data (quest_name,var,value) VALUES (?,?,?)"))
		{
			ps.setString(1, getName());
			ps.setString(2, var);
			ps.setString(3, value);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not insert global quest variable:", e);
		}
	}
	
	/**
	 * Read from the database a previously saved variable for this quest.<br>
	 * Due to performance considerations, this function should best be used only when the quest is first loaded.<br>
	 * Subclasses of this class can define structures into which these loaded values can be saved.<br>
	 * However, on-demand usage of this function throughout the script is not prohibited, only not recommended.<br>
	 * Values read from this function were entered by calls to "saveGlobalQuestVar".
	 * @param  var the name of the variable to load
	 * @return     the current value of the specified variable, or an empty string if the variable does not exist
	 */
	public final String loadGlobalQuestVar(String var)
	{
		String result = "";
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT value FROM quest_global_data WHERE quest_name = ? AND var = ?"))
		{
			ps.setString(1, getName());
			ps.setString(2, var);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.first())
				{
					result = rs.getString(1);
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not load global quest variable:", e);
		}
		return result;
	}
	
	/**
	 * Permanently delete from the database a global quest variable that was previously saved for this quest.
	 * @param var the name of the variable to delete
	 */
	public final void deleteGlobalQuestVar(String var)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM quest_global_data WHERE quest_name = ? AND var = ?"))
		{
			ps.setString(1, getName());
			ps.setString(2, var);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not delete global quest variable:", e);
		}
	}
	
	/**
	 * Permanently delete from the database all global quest variables that were previously saved for this quest.
	 */
	public final void deleteAllGlobalQuestVars()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM quest_global_data WHERE quest_name = ?"))
		{
			ps.setString(1, getName());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not delete global quest variables:", e);
		}
	}
	
	/**
	 * Get a random integer from 0 (inclusive) to {@code max} (exclusive).<br>
	 * Use this method instead of importing utility.
	 * @param  max the maximum value for randomization
	 * @return     a random integer number from 0 to {@code max - 1}
	 */
	public static int getRandom(int max)
	{
		return Rnd.get(max);
	}
	
	/**
	 * Get a random integer from {@code min} (inclusive) to {@code max} (inclusive).<br>
	 * Use this method instead of importing utility.
	 * @param  min the minimum value for randomization
	 * @param  max the maximum value for randomization
	 * @return     a random integer number from {@code min} to {@code max}
	 */
	public static int getRandom(int min, int max)
	{
		return Rnd.get(min, max);
	}
	
	/**
	 * Get a random boolean.<br>
	 * Use this method instead of importing utility.
	 * @return {@code true} or {@code false} randomly
	 */
	public static boolean getRandomBoolean()
	{
		return Rnd.nextBoolean();
	}
}
