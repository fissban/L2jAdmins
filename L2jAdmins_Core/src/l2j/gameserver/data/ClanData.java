package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.util.Util;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.11.2.5.2.5 $ $Date: 2005/03/27 15:29:18 $
 */
public class ClanData
{
	private static final Logger LOG = Logger.getLogger(ClanData.class.getName());
	
	// SQL
	private static final String SELECT = "SELECT clan_id FROM clan_data";
	private static final String UPDATE_1 = "UPDATE characters SET clanid=0, clan_privs=0 WHERE clanid=?";
	private static final String UPDATE_2 = "UPDATE castle SET taxPercent=0 WHERE id=?";
	private static final String DELETE_1 = "DELETE FROM clan_data WHERE clan_id=?";
	private static final String DELETE_2 = "DELETE FROM clan_wars WHERE clan1=? OR clan2=?";
	private static final String REPLACE = "REPLACE INTO clan_wars (clan1, clan2, wantspeace1, wantspeace2) VALUES(?,?,?,?)";
	private static final String DELETE = "DELETE FROM clan_wars WHERE clan1=? AND clan2=?";
	//
	private final Map<Integer, Clan> clans = new HashMap<>();
	
	public ClanData()
	{
		clans.clear();
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				int clanId = rs.getInt("clan_id");
				
				clans.put(clanId, new Clan(clanId));
				
				Clan clan = getClanById(clanId);
				
				if (clan.getDissolvingExpiryTime() != 0)
				{
					if (clan.getDissolvingExpiryTime() < System.currentTimeMillis())
					{
						destroyClan(clan.getId());
					}
					else
					{
						scheduleRemoveClan(clan.getId());
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("data error on ClanTable: " + e);
			e.printStackTrace();
		}
		
		UtilPrint.result("ClanData", "Loaded clans", clans.size());
	}
	
	public Collection<Clan> getClans()
	{
		return clans.values();
	}
	
	/**
	 * @param  clanId
	 * @return
	 */
	public Clan getClanById(int clanId)
	{
		return clans.get(clanId);
	}
	
	public Clan getClanByName(String clanName)
	{
		for (Clan clan : getClans())
		{
			if (clan.getName().equalsIgnoreCase(clanName))
			{
				return clan;
			}
		}
		return null;
	}
	
	public Clan createClan(L2PcInstance player, String clanName)
	{
		if (player == null)
		{
			return null;
		}
		
		if (player.getLevel() < 10)
		{
			player.sendPacket(SystemMessage.FAILED_TO_CREATE_CLAN);
			return null;
		}
		
		if (player.getClanId() != 0)
		{
			player.sendPacket(SystemMessage.FAILED_TO_CREATE_CLAN);
			return null;
		}
		
		if (player.getClanCreateExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessage.YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN);
			return null;
		}
		
		if (!Util.isAlphaNumeric(clanName) || (clanName.length() < 2))
		{
			player.sendPacket(SystemMessage.CLAN_NAME_INCORRECT);
			return null;
		}
		
		if (clanName.length() > 16)
		{
			player.sendPacket(SystemMessage.CLAN_NAME_TOO_LONG);
			return null;
		}
		
		if (getClanByName(clanName) != null)
		{
			player.sendMessage("Clan name already exists.");
			return null;
		}
		
		ClanMemberInstance leader = new ClanMemberInstance(player.getName(), player.getLevel(), player.getClassId().getId(), player.getObjectId());
		leader.setPlayerInstance(player);
		
		Clan clan = new Clan(IdFactory.getInstance().getNextId(), clanName, leader);
		clan.store();
		
		player.setClan(clan);
		
		player.setClanPrivileges(ClanPrivilegesType.initPrivilegies(true));
		
		clans.put(clan.getId(), clan);
		
		// should be update packet only
		player.sendPacket(new PledgeShowInfoUpdate(clan));
		player.sendPacket(new PledgeShowMemberListAll(clan, player));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(SystemMessage.CLAN_CREATED);
		return clan;
	}
	
	public void destroyClan(int clanId)
	{
		Clan clan = getClanById(clanId);
		if (clan == null)
		{
			return;
		}
		
		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_HAS_DISPERSED));
		
		if (AuctionData.getInstance().getAuction(clan.getAuctionBiddedAt()) != null)
		{
			AuctionData.getInstance().getAuction(clan.getAuctionBiddedAt()).cancelBid(clan.getId());
		}
		
		if (clan.hasClanHall())
		{
			ClanHallData.getInstance().getClanHallByOwner(clan).setOwner(null);
		}
		
		int castleId = clan.getCastleId();
		if (castleId == 0)
		{
			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				siege.removeSiegeClan(clanId);
			}
		}
		
		ClanMemberInstance leaderMember = clan.getLeader();
		if (leaderMember == null)
		{
			clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
		}
		else
		{
			clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);
		}
		
		// prevent ConcurrentModificationException
		Collection<ClanMemberInstance> members = clan.getMembers();
		
		for (ClanMemberInstance member : members)
		{
			clan.removeClanMember(member.getObjectId(), 0);
		}
		
		clans.remove(clanId);
		IdFactory.getInstance().releaseId(clanId);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(UPDATE_1))
			{
				ps.setInt(1, clanId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_1))
			{
				ps.setInt(1, clanId);
				ps.execute();
			}
			
			// TODO clan1 or clan2 ????
			try (PreparedStatement ps = con.prepareStatement(DELETE_2))
			{
				ps.setInt(1, clanId);
				ps.setInt(2, clanId);
				ps.execute();
			}
			
			if (castleId != 0)
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_2))
				{
					ps.setInt(1, castleId);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("could not dissolve clan:" + e);
		}
	}
	
	public boolean isAllyExists(String allyName)
	{
		for (Clan clan : getClans())
		{
			if ((clan.getAllyName() != null) && clan.getAllyName().equalsIgnoreCase(allyName))
			{
				return true;
			}
		}
		return false;
	}
	
	public void storeClansWars(int clanId1, int clanId2)
	{
		Clan clan1 = ClanData.getInstance().getClanById(clanId1);
		Clan clan2 = ClanData.getInstance().getClanById(clanId2);
		clan1.setEnemyClan(clan2);
		
		clan1.broadcastClanStatus();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(REPLACE))
		{
			statement.setInt(1, clanId1);
			statement.setInt(2, clanId2);
			statement.setInt(3, 0);
			statement.setInt(4, 0);
			statement.execute();
		}
		catch (Exception e)
		{
			LOG.warning("could not store clans wars data:" + e);
		}
		
		clan1.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP).addString(clan2.getName()));
		
		// clan1 declared clan war.
		clan2.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_S1_DECLARED_WAR).addString(clan1.getName()));
	}
	
	public void deleteClansWars(int clanId1, int clanId2)
	{
		Clan clan1 = ClanData.getInstance().getClanById(clanId1);
		Clan clan2 = ClanData.getInstance().getClanById(clanId2);
		clan1.deleteEnemyClan(clan2);
		
		clan1.broadcastClanStatus();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE))
		{
			ps.setInt(1, clanId1);
			ps.setInt(2, clanId2);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("could not restore clans wars data:" + e);
		}
		
		clan1.broadcastToOnlineMembers(new SystemMessage(SystemMessage.WAR_AGAINST_S1_HAS_STOPPED).addString(clan2.getName()));
		clan2.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_S1_HAS_DECIDED_TO_STOP).addString(clan1.getName()));
	}
	
	public void checkSurrender(Clan clan1, Clan clan2)
	{
		int count = 0;
		for (ClanMemberInstance player : clan1.getMembers())
		{
			if ((player != null) && (player.getPlayerInstance().getWantsPeace() == 1))
			{
				count++;
			}
		}
		if (count == (clan1.getMembers().size() - 1))
		{
			clan1.deleteEnemyClan(clan2);
			clan2.deleteEnemyClan(clan1);
			deleteClansWars(clan1.getId(), clan2.getId());
		}
	}
	
	public void scheduleRemoveClan(final int clanId)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (getClanById(clanId) == null)
			{
				return;
			}
			
			if (getClanById(clanId).getDissolvingExpiryTime() != 0)
			{
				destroyClan(clanId);
			}
			
		}, getClanById(clanId).getDissolvingExpiryTime() - System.currentTimeMillis());
	}
	
	public List<Clan> getClanAllies(int allianceId)
	{
		final List<Clan> clanAllies = new ArrayList<>();
		if (allianceId != 0)
		{
			for (Clan clan : clans.values())
			{
				if ((clan != null) && (clan.getAllyId() == allianceId))
				{
					clanAllies.add(clan);
				}
			}
		}
		return clanAllies;
	}
	
	public static ClanData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanData INSTANCE = new ClanData();
	}
}
