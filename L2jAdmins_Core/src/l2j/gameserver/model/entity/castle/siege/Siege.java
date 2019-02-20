package l2j.gameserver.model.entity.castle.siege;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.instancemanager.MercTicketManager;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.instance.L2ControlTowerInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.managers.SiegeArtifactManager;
import l2j.gameserver.model.entity.castle.siege.managers.SiegeClansListManager;
import l2j.gameserver.model.entity.castle.siege.managers.SiegeControlTowerManager;
import l2j.gameserver.model.entity.castle.siege.managers.SiegeGuardManager;
import l2j.gameserver.model.entity.castle.siege.task.SiegeEndTask;
import l2j.gameserver.model.entity.castle.siege.task.SiegeStartTask;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SiegeInfo;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;
import l2j.util.UtilPrint;

public class Siege
{
	private static final Logger LOG = Logger.getLogger(Siege.class.getName());
	
	private static enum TeleportWhoType
	{
		ALL,
		ATTACKER,
		DEFENDER_NOT_OWNER,
		OWNER,
		SPECTATOR
	}
	
	private final Castle castle;
	
	private boolean isInProgress = false;
	private boolean isRegistrationOver = false;
	private Calendar siegeEndDate;
	// Managers
	private SiegeGuardManager guardMngr;
	private SiegeArtifactManager artifactMngr;
	private SiegeControlTowerManager controlTowerMngr;
	private final SiegeClansListManager clanListMngr;
	
	private Calendar siegeRegistrationEndDate;
	
	public Siege(Castle castle)
	{
		this.castle = castle;
		// init all managers
		guardMngr = new SiegeGuardManager(castle);
		artifactMngr = new SiegeArtifactManager(castle);
		controlTowerMngr = new SiegeControlTowerManager(castle);
		clanListMngr = new SiegeClansListManager();
		
		startAutoTask();
	}
	
	/**
	 * When siege ends
	 */
	public void endSiege()
	{
		if (isInProgress())
		{
			announceToPlayer("The siege of " + castle.getName() + " has finished!", false);
			
			if (castle.getOwnerId() <= 0)
			{
				announceToPlayer("The siege of " + castle.getName() + " has ended in a draw.", false);
			}
			
			// Removes all flags. Note: Remove flag before teleporting players
			clanListMngr.removeFlags(SiegeClanType.DEFENDER, SiegeClanType.ATTACKER);
			// Teleport to the second closest town
			teleportPlayer(TeleportWhoType.ATTACKER, TeleportWhereType.TOWN);
			// Teleport to the second closest town
			teleportPlayer(TeleportWhoType.DEFENDER_NOT_OWNER, TeleportWhereType.TOWN);
			// Teleport to the second closest town
			teleportPlayer(TeleportWhoType.SPECTATOR, TeleportWhereType.TOWN);
			// Flag so that siege instance can be started
			isInProgress = false;
			updatePlayerSiegeStateFlags(true);
			// Save castle specific data
			saveCastleSiege();
			// Clear siege clan from db
			clearSiegeClan();
			// Remove artifact from this castle
			artifactMngr.removeAll();
			// Remove all control tower from this castle
			controlTowerMngr.removeAll();
			// Remove all spawned siege guard from this castle
			guardMngr.unspawnAllGuards();
			if (castle.getOwnerId() > 0)
			{
				guardMngr.removeMercs();
			}
			// Respawn door to castle
			castle.spawnDoors();
			castle.getZone().updateZoneStatusForCharactersInside();
			
			for (SiegeClanHolder attackerClan : clanListMngr.getClanList(SiegeClanType.ATTACKER))
			{
				final Clan clan = attackerClan.getClan();
				if (clan == null)
				{
					continue;
				}
				
				clan.clearSiegeKills();
				clan.clearSiegeDeaths();
			}
			
			for (SiegeClanHolder defenderClan : clanListMngr.getClanList(SiegeClanType.DEFENDER))
			{
				final Clan clan = defenderClan.getClan();
				if (clan == null)
				{
					continue;
				}
				
				clan.clearSiegeKills();
				clan.clearSiegeDeaths();
			}
		}
	}
	
	public Calendar getSiegeEndDate()
	{
		return siegeEndDate;
	}
	
	/**
	 * When control of castle changed during siege
	 */
	public void midVictory()
	{
		// Siege still in progress
		if (isInProgress())
		{
			if (castle.getOwnerId() > 0)
			{
				// Remove all merc entry from db
				guardMngr.removeMercs();
			}
			
			if (clanListMngr.getClanList(SiegeClanType.DEFENDER).isEmpty() && // If defender doesn't exist (Pc vs Npc)
				(clanListMngr.getClanList(SiegeClanType.ATTACKER).size() == 1)) // Only 1 attacker
			{
				clanListMngr.addClan(SiegeClanType.OWNER, castle.getOwnerId());
				endSiege();
				return;
			}
			if (castle.getOwnerId() > 0)
			{
				int allyId = ClanData.getInstance().getClanById(castle.getOwnerId()).getAllyId();
				// If defender doesn't exist (Pc vs Npc)
				if (clanListMngr.getClanList(SiegeClanType.DEFENDER).isEmpty())
				// and only an alliance attacks
				{
					// The player's clan is in an alliance
					if (allyId != 0)
					{
						boolean allInSameAlliance = true;
						for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.ATTACKER))
						{
							if (sc != null)
							{
								if (sc.getClan().getAllyId() != allyId)
								{
									allInSameAlliance = false;
								}
							}
						}
						if (allInSameAlliance)
						{
							clanListMngr.addClan(SiegeClanType.OWNER, castle.getOwnerId());
							endSiege();
							return;
						}
					}
				}
				
				// all clans who are defending as attacking move.
				for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.DEFENDER))
				{
					if (sc != null)
					{
						clanListMngr.addClan(SiegeClanType.ATTACKER, sc);
					}
				}
				
				// a new clan as "owner" is defined.
				clanListMngr.addClan(SiegeClanType.OWNER, castle.getOwnerId());
				
				// The player's clan is in an alliance
				if (allyId != 0)
				{
					for (Clan clan : ClanData.getInstance().getClans())
					{
						if (clan.getAllyId() == allyId)
						{
							clanListMngr.addClan(SiegeClanType.DEFENDER, clan.getId());
						}
					}
				}
				// Teleport to the second closest town
				teleportPlayer(TeleportWhoType.ATTACKER, TeleportWhereType.SIEGE_FLAG);
				// Teleport to the second closest town
				teleportPlayer(TeleportWhoType.SPECTATOR, TeleportWhereType.TOWN);
				
				// Removes all defenders' flags
				clanListMngr.removeFlags(SiegeClanType.DEFENDER);
				// Remove all castle door upgrades
				castle.removeDoorUpgrade();
				// Respawn door to castle but make them weaker (50% hp)
				castle.spawnDoors(true);
				// Remove all control tower from this castle
				controlTowerMngr.removeAll();
				// spawn all control towers
				controlTowerMngr.spawnAll();
			}
		}
	}
	
	/**
	 * When siege starts
	 */
	public void startSiege()
	{
		if (!isInProgress())
		{
			if (clanListMngr.getClanList(SiegeClanType.ATTACKER).isEmpty())
			{
				SystemMessage sm;
				if (castle.getOwnerId() <= 0)
				{
					sm = new SystemMessage(SystemMessage.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
				}
				else
				{
					sm = new SystemMessage(SystemMessage.S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED);
				}
				sm.addString(castle.getName());
				AnnouncementsData.getInstance().announceToAll(sm);
				return;
			}
			
			// Flag so that same siege instance cannot be started again
			isInProgress = true;
			// Load siege clan from db
			loadSiegeClan();
			updatePlayerSiegeStateFlags(false);
			// Teleport to the closest town
			teleportPlayer(TeleportWhoType.ATTACKER, TeleportWhereType.TOWN);
			// Spawn artifact
			artifactMngr.spawnAll();
			// Remove all control tower from this castle
			controlTowerMngr.removeAll();
			// Spawn all control towers
			controlTowerMngr.spawnAll();
			// Spawn door
			castle.spawnDoors();
			// Spawn siege guard
			spawnSiegeGuard();
			// remove the tickets from the ground
			MercTicketManager.getInstance().deleteTickets(castle.getId());
			
			castle.getZone().updateZoneStatusForCharactersInside();
			
			// Schedule a task to prepare auto siege end
			siegeEndDate = Calendar.getInstance();
			siegeEndDate.add(Calendar.MINUTE, Config.SIEGE_LENGTH);
			// Prepare auto end task
			scheduleEndSiegeTask(1000);
			
			announceToPlayer("The siege of " + castle.getName() + " has started!", false);
		}
	}
	
	public void scheduleEndSiegeTask(long time)
	{
		ThreadPoolManager.schedule(new SiegeEndTask(this), time);
	}
	
	public void scheduleStartSiegeTask(long time)
	{
		ThreadPoolManager.schedule(new SiegeStartTask(this), time);
	}
	
	/**
	 * Announce to player.
	 * @param message    The String of the message to send to player
	 * @param inAreaOnly The boolean flag to show message to players in area only.
	 */
	public void announceToPlayer(String message, boolean inAreaOnly)
	{
		if (inAreaOnly)
		{
			castle.getZone().announceToPlayers(message);
			return;
		}
		
		// Get all players
		Broadcast.toAllOnlinePlayers(message);
	}
	
	private void updatePlayerSiegeStateFlags(boolean clear)
	{
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.ATTACKER))
		{
			Clan clan = sc.getClan();
			for (L2PcInstance member : clan.getOnlineMembers())
			{
				if (clear)
				{
					member.setSiegeState(PlayerSiegeStateType.NOT_INVOLVED);
				}
				else
				{
					member.setSiegeState(PlayerSiegeStateType.ATACKER);
				}
				member.broadcastUserInfo();
			}
		}
		
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.DEFENDER))
		{
			Clan clan = sc.getClan();
			for (L2PcInstance member : clan.getOnlineMembers())
			{
				if (clear)
				{
					member.setSiegeState(PlayerSiegeStateType.NOT_INVOLVED);
				}
				else
				{
					member.setSiegeState(PlayerSiegeStateType.DEFENDER);
				}
				member.broadcastUserInfo();
			}
		}
	}
	
	/**
	 * Approve clan as defender for siege
	 * @param clanId The int of player's clan id
	 */
	public void approveSiegeDefenderClan(int clanId)
	{
		if (clanId <= 0)
		{
			return;
		}
		saveSiegeClan(ClanData.getInstance().getClanById(clanId), 0, true);
		loadSiegeClan();
	}
	
	/**
	 * @param  x
	 * @param  y
	 * @param  z
	 * @return   true if object is inside the zone
	 */
	public boolean checkIfInZone(int x, int y, int z)
	{
		return (isInProgress() && (castle.checkIfInZone(x, y, z))); // Castle zone during siege
	}
	
	/** Clear all registered siege clans from database for castle */
	public void clearSiegeClan()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=?"))
			{
				ps.setInt(1, castle.getId());
				ps.execute();
			}
			
			if (castle.getOwnerId() > 0)
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?"))
				{
					ps.setInt(1, castle.getOwnerId());
					ps.execute();
				}
			}
			
			clanListMngr.clearAll();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception clearSiegeClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/** Clear all siege clans waiting for approval from database for castle */
	public void clearSiegeWaitingClan()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? AND type = 2"))
		{
			ps.setInt(1, castle.getId());
			ps.execute();
			
			clanListMngr.clearByType(SiegeClanType.DEFENDER_PENDING);
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception clearSiegeWaitingClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @return list of L2PcInstance registered as attacker in the zone.
	 */
	public List<L2PcInstance> getAttackersInZone()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.ATTACKER))
		{
			Clan clan = sc.getClan();
			for (L2PcInstance player : clan.getOnlineMembers())
			{
				if (checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}
		return players;
	}
	
	/**
	 * @return list of L2PcInstance registered as defender but not owner in the zone.
	 */
	public List<L2PcInstance> getDefendersButNotOwnersInZone()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.DEFENDER))
		{
			Clan clan = sc.getClan();
			if (clan.getId() == castle.getOwnerId())
			{
				continue;
			}
			
			for (L2PcInstance player : clan.getOnlineMembers())
			{
				if (checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}
		return players;
	}
	
	/**
	 * @return list of L2PcInstance in the zone.
	 */
	public List<L2PcInstance> getPlayersInZone()
	{
		return castle.getZone().getAllPlayers();
	}
	
	/**
	 * @return list of L2PcInstance owning the castle in the zone.
	 */
	public List<L2PcInstance> getOwnersInZone()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.OWNER))
		{
			Clan clan = sc.getClan();
			
			if (clan.getId() != castle.getOwnerId())
			{
				continue;
			}
			for (L2PcInstance player : clan.getOnlineMembers())
			{
				if (checkIfInZone(player.getX(), player.getY(), player.getZ()))
				{
					players.add(player);
				}
			}
		}
		return players;
	}
	
	/**
	 * @return list of L2PcInstance not registered as attacker or defender in the zone.
	 */
	public List<L2PcInstance> getSpectatorsInZone()
	{
		List<L2PcInstance> players = new ArrayList<>();
		
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player == null)
			{
				continue;
			}
			if (!player.isInsideZone(ZoneType.SIEGE) || (player.getSiegeState() != PlayerSiegeStateType.NOT_INVOLVED))
			{
				continue;
			}
			if (checkIfInZone(player.getX(), player.getY(), player.getZ()))
			{
				players.add(player);
			}
		}
		
		return players;
	}
	
	/**
	 * Remove the flag that was killed
	 * @param flag
	 */
	public void killedFlag(L2SiegeFlagInstance flag)
	{
		if (flag == null)
		{
			return;
		}
		
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.ATTACKER))
		{
			if (sc.removeFlag(flag))
			{
				return;
			}
		}
	}
	
	/**
	 * Display list of registered clans
	 * @param player
	 */
	public void listRegisterClan(L2PcInstance player)
	{
		player.sendPacket(new SiegeInfo(castle));
	}
	
	/**
	 * Register clan as attacker
	 * @param player The L2PcInstance of the player trying to register
	 */
	public void registerAttacker(L2PcInstance player)
	{
		registerAttacker(player, false);
	}
	
	public void registerAttacker(L2PcInstance player, boolean force)
	{
		if (player.getClan() == null)
		{
			player.sendMessage("You need Clan.");
			return;
		}
		int allyId = 0;
		if (castle.getOwnerId() != 0)
		{
			allyId = ClanData.getInstance().getClanById(castle.getOwnerId()).getAllyId();
		}
		if (allyId != 0)
		{
			if ((player.getClan().getAllyId() == allyId) && !force)
			{
				player.sendMessage("You cannot register as an attacker as your alliance owns the castle.");
				return;
			}
		}
		if (force || checkIfCanRegister(player))
		{
			saveSiegeClan(player.getClan(), 1, false); // Save to database
		}
	}
	
	/**
	 * Register clan as defender
	 * @param player The L2PcInstance of the player trying to register
	 */
	public void registerDefender(L2PcInstance player)
	{
		registerDefender(player, false);
	}
	
	public void registerDefender(L2PcInstance player, boolean force)
	{
		if (castle.getOwnerId() <= 0)
		{
			player.sendMessage("You cannot register as a defender because " + castle.getName() + " is owned by NPC.");
		}
		else if (force || checkIfCanRegister(player))
		{
			saveSiegeClan(player.getClan(), 2, false); // Save to database
		}
	}
	
	/**
	 * Remove clan from siege
	 * @param clanId The int of player's clan id
	 */
	public void removeSiegeClan(int clanId)
	{
		if (clanId <= 0)
		{
			return;
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? AND clan_id=?"))
		{
			ps.setInt(1, castle.getId());
			ps.setInt(2, clanId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception removeSiegeClan(): " + e.getMessage());
			e.printStackTrace();
		}
		
		loadSiegeClan();
	}
	
	/**
	 * Remove clan from siege
	 * @param player The L2PcInstance of player/clan being removed
	 */
	public void removeSiegeClan(L2PcInstance player)
	{
		if ((player.getClan() == null) || (player.getClan().getCastleId() == castle.getId()) || !SiegeManager.getInstance().checkIsRegistered(player.getClan(), castle.getId()))
		{
			return;
		}
		
		if (isInProgress())
		{
			player.sendMessage("This is not the time for siege registration and so registration and cancellation cannot be done.");
			return;
		}
		removeSiegeClan(player.getClan().getId());
	}
	
	/**
	 * Start the auto tasks
	 */
	public void startAutoTask()
	{
		correctSiegeDateTime();
		UtilPrint.result("Siege", castle.getName(), castle.getSiegeDate().getTime().toString());
		
		loadSiegeClan();
		
		// Schedule registration end
		siegeRegistrationEndDate = Calendar.getInstance();
		siegeRegistrationEndDate.setTimeInMillis(castle.getSiegeDate().getTimeInMillis());
		siegeRegistrationEndDate.add(Calendar.DAY_OF_MONTH, -1);
		
		// Schedule siege auto start
		scheduleStartSiegeTask(1000);
	}
	
	/**
	 * Teleport players
	 * @param teleportWho
	 * @param teleportWhere
	 */
	public void teleportPlayer(TeleportWhoType teleportWho, TeleportWhereType teleportWhere)
	{
		List<L2PcInstance> players;
		switch (teleportWho)
		{
			case OWNER:
				players = getOwnersInZone();
				break;
			case ATTACKER:
				players = getAttackersInZone();
				break;
			case DEFENDER_NOT_OWNER:
				players = getDefendersButNotOwnersInZone();
				break;
			case SPECTATOR:
				players = getSpectatorsInZone();
				break;
			default:
				players = getPlayersInZone();
		}
		
		for (L2PcInstance player : players)
		{
			if (player.isGM() || player.isInJail())
			{
				continue;
			}
			player.teleToLocation(teleportWhere);
		}
	}
	
	/**
	 * @return        true if the player can register
	 * @param  player The L2PcInstance of the player trying to register
	 */
	private boolean checkIfCanRegister(L2PcInstance player)
	{
		if (isRegistrationOver())
		{
			player.sendPacket(new SystemMessage(SystemMessage.DEADLINE_FOR_SIEGE_S1_PASSED).addString(castle.getName()));
		}
		else if (isInProgress())
		{
			player.sendPacket(SystemMessage.NOT_SIEGE_REGISTRATION_TIME2);
		}
		else if ((player.getClan() == null) || (player.getClan().getLevel() < Config.SIEGE_MIN_CLAN_LVL))
		{
			player.sendPacket(SystemMessage.ONLY_CLAN_LEVEL_5_ABOVE_MAY_SIEGE);
		}
		else if (player.getClan().hasCastle())
		{
			player.sendPacket(SystemMessage.CLAN_THAT_OWNS_CASTLE_CANNOT_PARTICIPATE_OTHER_SIEGE);
		}
		else if (player.getClan().getId() == castle.getOwnerId())
		{
			player.sendPacket(SystemMessage.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING);
		}
		else if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), castle.getId()))
		{
			player.sendPacket(SystemMessage.ALREADY_REQUESTED_SIEGE_BATTLE);
		}
		else if (checkIfAlreadyRegisteredForSameDay(player.getClan()))
		{
			player.sendPacket(SystemMessage.APPLICATION_DENIED_BECAUSE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE);
		}
		else
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return      true if the clan has already registered to a siege for the same day.
	 * @param  clan The L2Clan of the player trying to register
	 */
	public boolean checkIfAlreadyRegisteredForSameDay(Clan clan)
	{
		for (Siege siege : SiegeManager.getInstance().getSieges())
		{
			if (siege == this)
			{
				continue;
			}
			if (siege.getSiegeDate().get(Calendar.DAY_OF_WEEK) == getSiegeDate().get(Calendar.DAY_OF_WEEK))
			{
				if (siege.isAttacker(clan))
				{
					return true;
				}
				if (siege.isDefender(clan))
				{
					return true;
				}
				if (siege.isDefenderWaiting(clan))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param siegeDate The Calendar siege date and time
	 */
	private void correctSiegeDateTime()
	{
		boolean corrected = false;
		
		if (castle.getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Since siege has past reschedule it to the next one (14 days)
			// This is usually caused by server being down
			corrected = true;
			setNextSiegeDate();
		}
		
		if (castle.getSiegeDate().get(Calendar.DAY_OF_WEEK) != castle.getSiegeDayOfWeek())
		{
			corrected = true;
			castle.getSiegeDate().set(Calendar.DAY_OF_WEEK, castle.getSiegeDayOfWeek());
		}
		if (castle.getSiegeDate().get(Calendar.HOUR_OF_DAY) != castle.getSiegeHourOfDay())
		{
			corrected = true;
			castle.getSiegeDate().set(Calendar.HOUR_OF_DAY, castle.getSiegeHourOfDay());
		}
		castle.getSiegeDate().set(Calendar.MINUTE, 0);
		
		if (corrected)
		{
			saveSiegeDate();
		}
	}
	
	/** Load siege clans. */
	private void loadSiegeClan()
	{
		clanListMngr.clearAll();
		
		// Add castle owner as defender (add owner first so that they are on the top of the defender list)
		if (castle.getOwnerId() > 0)
		{
			clanListMngr.addClan(SiegeClanType.OWNER, castle.getOwnerId());
		}
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT clan_id,type FROM siege_clans WHERE castle_id=?"))
		{
			ps.setInt(1, castle.getId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					switch (rs.getInt("type"))
					{
						case 0:
							clanListMngr.addClan(SiegeClanType.DEFENDER, rs.getInt("clan_id"));
							break;
						case 1:
							clanListMngr.addClan(SiegeClanType.ATTACKER, rs.getInt("clan_id"));
							break;
						case 2:
							clanListMngr.addClan(SiegeClanType.DEFENDER_PENDING, rs.getInt("clan_id"));
							break;
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception loadSiegeClan(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/** Save castle siege related to database. */
	private void saveCastleSiege()
	{
		// Set the next set date for 2 weeks from now
		setNextSiegeDate();
		// Save the new date
		saveSiegeDate();
		// Prepare auto start siege and end registration
		startAutoTask();
	}
	
	/** Save siege date to database. */
	private void saveSiegeDate()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE castle SET siegeDate=? WHERE id=?"))
		{
			ps.setLong(1, getSiegeDate().getTimeInMillis());
			ps.setInt(2, castle.getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception saveSiegeDate(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Save registration to database
	 * @param clan                 The L2Clan of player
	 * @param typeId               0 = defender, 1 = attacker, 2 = defender waiting
	 * @param isUpdateRegistration
	 */
	private void saveSiegeClan(Clan clan, int typeId, boolean isUpdateRegistration)
	{
		if (clan.hasCastle())
		{
			return;
		}
		
		if ((typeId == 0) || (typeId == 2))
		{
			if ((clanListMngr.getClanList(SiegeClanType.DEFENDER).size() + clanListMngr.getClanList(SiegeClanType.DEFENDER_PENDING).size()) >= Config.SIEGE_DEFENDER_MAX_CLANS)
			{
				return;
			}
		}
		else
		{
			if (clanListMngr.getClanList(SiegeClanType.ATTACKER).size() >= Config.SIEGE_ATTACKER_MAX_CLANS)
			{
				return;
			}
		}
		try (Connection con = DatabaseManager.getConnection())
		{
			if (!isUpdateRegistration)
			{
				try (PreparedStatement ps = con.prepareStatement("INSERT INTO siege_clans (clan_id,castle_id,type,castle_owner) VALUES (?,?,?,0)"))
				{
					ps.setInt(1, clan.getId());
					ps.setInt(2, castle.getId());
					ps.setInt(3, typeId);
					ps.execute();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement("UPDATE siege_clans SET type=? WHERE castle_id=? AND clan_id=?"))
				{
					ps.setInt(1, typeId);
					ps.setInt(2, castle.getId());
					ps.setInt(3, clan.getId());
					ps.execute();
				}
			}
			
			if (typeId == 0)
			{
				clanListMngr.addClan(SiegeClanType.DEFENDER, clan.getId());
				announceToPlayer(clan.getName() + " has been registered to defend " + castle.getName(), false);
			}
			else if (typeId == 1)
			{
				clanListMngr.addClan(SiegeClanType.ATTACKER, clan.getId());
				announceToPlayer(clan.getName() + " has been registered to attack " + castle.getName(), false);
			}
			else if (typeId == 2)
			{
				clanListMngr.addClan(SiegeClanType.DEFENDER_PENDING, clan.getId());
				announceToPlayer(clan.getName() + " has requested to defend " + castle.getName(), false);
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/** Set the date for the next siege. */
	private void setNextSiegeDate()
	{
		while (castle.getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Set next siege date if siege has passed
			castle.getSiegeDate().add(Calendar.DAY_OF_MONTH, 14); // Schedule to happen in 14 days
		}
		
		// Allow registration for next siege
		openRegistration();
	}
	
	/**
	 * Spawn siege guard.
	 */
	private void spawnSiegeGuard()
	{
		getGuardMngr().spawnAllGuards();
		
		// Register guard to the closest Control Tower
		// When CT dies, so do all the guards that it controls
		if ((getGuardMngr().getGuardSpawnsCount() > 0) && (controlTowerMngr.getCount() > 0))
		{
			L2ControlTowerInstance closestCt;
			double distance, x, y, z;
			double distanceClosest = 0;
			for (Spawn spawn : getGuardMngr().getGuardSpawns())
			{
				if (spawn == null)
				{
					continue;
				}
				closestCt = null;
				distanceClosest = 0;
				for (L2ControlTowerInstance ct : controlTowerMngr.getAll())
				{
					if (ct == null)
					{
						continue;
					}
					x = (spawn.getX() - ct.getX());
					y = (spawn.getY() - ct.getY());
					z = (spawn.getZ() - ct.getZ());
					
					distance = (x * x) + (y * y) + (z * z);
					
					if ((closestCt == null) || (distance < distanceClosest))
					{
						closestCt = ct;
						distanceClosest = distance;
					}
				}
				
				if (closestCt != null)
				{
					closestCt.registerGuard(spawn);
				}
			}
		}
	}
	
	public final Castle getCastle()
	{
		return castle;
	}
	
	public final boolean isInProgress()
	{
		return isInProgress;
	}
	
	public final boolean isRegistrationOver()
	{
		return isRegistrationOver;
	}
	
	public void closeRegistration()
	{
		isRegistrationOver = true;
	}
	
	public void openRegistration()
	{
		isRegistrationOver = false;
	}
	
	public final Calendar getSiegeDate()
	{
		return castle.getSiegeDate();
	}
	
	/**
	 * Broadcast a string to defenders.
	 * @param message   The String of the message to send to player
	 * @param bothSides if true, broadcast too to attackers clans.
	 */
	public void announceToPlayer(SystemMessage message, boolean bothSides)
	{
		for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.DEFENDER))
		{
			sc.getClan().broadcastToOnlineMembers(message);
		}
		
		if (bothSides)
		{
			for (SiegeClanHolder sc : clanListMngr.getClanList(SiegeClanType.ATTACKER))
			{
				sc.getClan().broadcastToOnlineMembers(message);
			}
		}
	}
	
	// XXX SiegeGuardManager -------------------------------------------------------------------------------------------------
	public SiegeGuardManager getGuardMngr()
	{
		return guardMngr;
	}
	
	// XXX SiegeArtifactManager ----------------------------------------------------------------------------------------------
	public SiegeArtifactManager getArtifactMngr()
	{
		return artifactMngr;
	}
	
	// XXX SiegeControlTowerManager ------------------------------------------------------------------------------------------
	public SiegeControlTowerManager getControlTowerMngr()
	{
		return controlTowerMngr;
	}
	
	// XXX SiegeClansListManager ---------------------------------------------------------------------------------------------
	
	public SiegeClansListManager getClansListMngr()
	{
		return clanListMngr;
	}
	
	/**
	 * Check if clan is attacker
	 * @param  clan The L2Clan of the player
	 * @return
	 */
	public boolean isAttacker(Clan clan)
	{
		if (clan == null)
		{
			return true;
		}
		return (clanListMngr.getClan(SiegeClanType.ATTACKER, clan.getId()) != null);
	}
	
	/**
	 * Check if clan is defender
	 * @param  clan The L2Clan of the player
	 * @return
	 */
	public boolean isDefender(Clan clan)
	{
		if (clan == null)
		{
			return false;
		}
		return (clanListMngr.getClan(SiegeClanType.DEFENDER, clan.getId()) != null);
	}
	
	/**
	 * Check if clan is defender waiting approval
	 * @param  clan The L2Clan of the player
	 * @return
	 */
	public boolean isDefenderWaiting(Clan clan)
	{
		return (clanListMngr.getClan(SiegeClanType.DEFENDER_PENDING, clan.getId()) != null);
	}
	
	public List<L2SiegeFlagInstance> getFlags(Clan clan)
	{
		if (clan != null)
		{
			SiegeClanHolder sc = clanListMngr.getClan(SiegeClanType.ATTACKER, clan.getId());
			if (sc != null)
			{
				return sc.getFlags();
			}
		}
		return null;
	}
}
