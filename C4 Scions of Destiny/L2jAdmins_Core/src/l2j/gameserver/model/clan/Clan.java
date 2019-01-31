package l2j.gameserver.model.clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.instancemanager.communitybbs.Community;
import l2j.gameserver.instancemanager.communitybbs.CommunityForumInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.enums.ClanPenaltyType;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.itemcontainer.warehouse.ClanWarehouse;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;
import l2j.gameserver.network.external.server.PledgeShowMemberListDeleteAll;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.7.2.4.2.7 $ $Date: 2005/04/06 16:13:41 $
 */
public class Clan
{
	private static final Logger LOG = Logger.getLogger(Clan.class.getName());
	
	private static final String UPDATE_CLAN = "UPDATE clan_data SET leader_id=?, ally_id=?, ally_name=?, char_penalty_expiry_time=?, recover_penalty_expiry_time=?, dissolving_expiry_time=?, ally_join_expiry_time=?, ally_penalty_expiry_time=?, ally_penalty_type=? WHERE clan_id=?";
	private static final String CREATE_CLAN = "INSERT INTO clan_data (clan_id, clan_name, clan_level, hasCastle, ally_id, ally_name, leader_id, crest_id, crest_large_id, ally_crest_id) values (?,?,?,?,?,?,?,?,?,?)";
	private static final String REMOVE_MEMBER_CLAN = "UPDATE characters SET clanid=0, title=?, clan_join_expiry_time=?, clan_create_expiry_time=?, clan_privs=0, wantspeace=0 WHERE obj_Id=?";
	private static final String RESTORE_CLAN = "SELECT clan_name, clan_level, hasCastle, ally_id, ally_name, leader_id, crest_id, crest_large_id, ally_crest_id, auction_bid_at, char_penalty_expiry_time, recover_penalty_expiry_time, dissolving_expiry_time, ally_join_expiry_time, ally_penalty_expiry_time, ally_penalty_type, enabled, notice FROM clan_data where clan_id=?";
	private static final String UPDATE_AUCTION_BID = "UPDATE clan_data SET auction_bid_at=? WHERE clan_id=?";
	private static final String UPDATE_CLAN_NOTICE = "UPDATE clan_data SET enabled=?, notice=? WHERE clan_id=?";
	private static final String UPDATE_CLAN_INTRODUCTION = "UPDATE clan_data SET introduction=? WHERE clan_id=?";
	
	private String name;
	private int clanId;
	private ClanMemberInstance leader;
	private final Map<Integer, ClanMemberInstance> members = new HashMap<>();
	
	private String allyName;
	private int allyId;
	private int level;
	private int hasCastle;
	private int hasClanHall;
	private int crestId;
	private int crestLargeId;
	private int allyCrestId;
	private int auctionBiddedAt = 0;
	private long charPenaltyExpiryTime;
	private long recoverPenaltyExpiryTime;
	private long dissolvingExpiryTime;
	private long allyJoinExpiryTime;
	private long allyPenaltyExpiryTime;
	private ClanPenaltyType allyPenaltyType;
	
	private final ItemContainer warehouse = new ClanWarehouse(this);
	private final List<Integer> atWarWith = new ArrayList<>();
	
	private CommunityForumInstance forum;
	
	private String notice;
	private boolean noticeEnabled;
	private static final int MAX_NOTICE_LENGTH = 8192;
	
	private String introduction;
	private static final int MAX_INTRODUCTION_LENGTH = 300;
	
	private final AtomicInteger siegeKills = new AtomicInteger();
	private final AtomicInteger siegeDeaths = new AtomicInteger();
	
	/**
	 * called if a clan is referenced only by id. in this case all other data needs to be fetched from db
	 * @param clanId
	 */
	public Clan(int clanId)
	{
		this.clanId = clanId;
		restore();
		getWarehouse().restore();
	}
	
	/**
	 * this is only called if a new clan is created
	 * @param clanId
	 * @param clanName
	 * @param leader
	 */
	public Clan(int clanId, String clanName, ClanMemberInstance leader)
	{
		this.clanId = clanId;
		name = clanName;
		setLeader(leader);
	}
	
	/**
	 * @return Returns the clanId.
	 */
	public int getId()
	{
		return clanId;
	}
	
	/**
	 * @param clanId The clanId to set.
	 */
	public void setClanId(int clanId)
	{
		this.clanId = clanId;
	}
	
	/**
	 * @return Returns the leaderId.
	 */
	public int getLeaderId()
	{
		return (leader != null ? leader.getObjectId() : 0);
	}
	
	/**
	 * @return L2ClanMember of clan leader.
	 */
	public ClanMemberInstance getLeader()
	{
		return leader;
	}
	
	/**
	 * @param leader The leaderId to set.
	 */
	public void setLeader(ClanMemberInstance leader)
	{
		this.leader = leader;
		members.put(leader.getObjectId(), leader);
	}
	
	/**
	 * @return Returns the leaderName.
	 */
	public String getLeaderName()
	{
		return leader != null ? leader.getName() : "";
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	private void addClanMember(ClanMemberInstance member)
	{
		members.put(member.getObjectId(), member);
	}
	
	public void addClanMember(L2PcInstance player)
	{
		addClanMember(new ClanMemberInstance(player));
	}
	
	public ClanMemberInstance getClanMember(String name)
	{
		for (ClanMemberInstance temp : members.values())
		{
			if (temp.getName().equals(name))
			{
				return temp;
			}
		}
		return null;
	}
	
	public ClanMemberInstance getClanMember(int objectID)
	{
		return members.get(objectID);
	}
	
	public void removeClanMember(int objectId, long clanJoinExpiryTime)
	{
		ClanMemberInstance exMember = members.remove(objectId);
		
		if (exMember == null)
		{
			LOG.warning("Member Object ID: " + objectId + " not found in clan while trying to remove");
			return;
		}
		
		if (!exMember.isOnline())
		{
			removeMemberFromDB(exMember, clanJoinExpiryTime, getLeaderId() == objectId ? System.currentTimeMillis() + (Config.ALT_CLAN_CREATE_DAYS * 86400000L) : 0);
			return;
		}
		
		L2PcInstance player = exMember.getPlayerInstance();
		if (player.isClanLeader())
		{
			for (Skill s : SkillData.getSiegeSkills())
			{
				player.addSkill(s, false);
			}
			
			player.setClanCreateExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_CREATE_DAYS * 86400000L)); // 24*60*60*1000 = 86400000
		}
		
		if (!player.isNoble())
		{
			player.setTitle("");
		}
		
		CastleData.getInstance().removeCirclet(this, getCastleId());
		
		player.setClan(null);
		player.setClanJoinExpiryTime(clanJoinExpiryTime);
		
		player.broadcastUserInfo();
		player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
	}
	
	public Collection<ClanMemberInstance> getMembers()
	{
		return members.values();
	}
	
	public int getMembersCount()
	{
		return members.size();
	}
	
	public List<L2PcInstance> getOnlineMembers()
	{
		List<L2PcInstance> result = new ArrayList<>();
		for (ClanMemberInstance temp : members.values())
		{
			try
			{
				if (temp.isOnline())
				{
					result.add(temp.getPlayerInstance());
				}
			}
			catch (NullPointerException e)
			{
				// ignore
			}
		}
		
		return result;
	}
	
	/**
	 * @return
	 */
	public int getAllyId()
	{
		return allyId;
	}
	
	/**
	 * @return
	 */
	public String getAllyName()
	{
		return allyName;
	}
	
	public void setAllyCrestId(int allyCrestId)
	{
		this.allyCrestId = allyCrestId;
	}
	
	/**
	 * @return
	 */
	public int getAllyCrestId()
	{
		return allyCrestId;
	}
	
	/**
	 * @return
	 */
	public int getLevel()
	{
		return level;
	}
	
	public boolean hasCastle()
	{
		return hasCastle > 0;
	}
	
	/**
	 * @return
	 */
	public int getCastleId()
	{
		return hasCastle;
	}
	
	public int getClanHallId()
	{
		return hasClanHall;
	}
	
	/**
	 * @param hasClanHall The hasClanHall to set.
	 */
	public void setClanHallId(int hasClanHall)
	{
		this.hasClanHall = hasClanHall;
	}
	
	/**
	 * @return
	 */
	public boolean hasClanHall()
	{
		return hasClanHall > 0;
	}
	
	/**
	 * @param crestId The id of pledge crest.
	 */
	public void setCrestId(int crestId)
	{
		this.crestId = crestId;
	}
	
	/**
	 * @return Returns the clanCrestId.
	 */
	public int getCrestId()
	{
		return crestId;
	}
	
	/**
	 * @param crestLargeId The id of pledge LargeCrest.
	 */
	public void setCrestLargeId(int crestLargeId)
	{
		this.crestLargeId = crestLargeId;
	}
	
	/**
	 * @return Returns the clan CrestLargeId
	 */
	public int getCrestLargeId()
	{
		return crestLargeId;
	}
	
	/**
	 * @param allyId The allyId to set.
	 */
	public void setAllyId(int allyId)
	{
		this.allyId = allyId;
	}
	
	/**
	 * @param allyName The allyName to set.
	 */
	public void setAllyName(String allyName)
	{
		this.allyName = allyName;
	}
	
	/**
	 * @param hasCastle The hasCastle to set.
	 */
	public void setHasCastle(int hasCastle)
	{
		this.hasCastle = hasCastle;
	}
	
	/**
	 * @param level The level to set.
	 */
	public void setLevel(int level)
	{
		this.level = level;
		
		if (Config.COMMUNITY_ENABLE && (level >= 2) && (forum == null))
		{
			CommunityForumInstance forum = Community.getInstance().getForumByName("ClanRoot");
			if (forum != null)
			{
				forum = forum.getChildByName(name);
				if (forum == null)
				{
					forum = Community.getInstance().createNewForum(name, Community.getInstance().getForumByName("ClanRoot"), CommunityForumInstance.CLAN, CommunityForumInstance.CLANMEMBERONLY, getId());
				}
			}
		}
	}
	
	/**
	 * @param  id
	 * @return
	 */
	public boolean isMember(int id)
	{
		return (id == 0 ? false : members.containsKey(id));
	}
	
	public void updateClanInDB()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN))
		{
			ps.setInt(1, getLeaderId());
			ps.setInt(2, getAllyId());
			ps.setString(3, getAllyName());
			ps.setLong(4, getCharPenaltyExpiryTime());
			ps.setLong(5, getRecoverPenaltyExpiryTime());
			ps.setLong(6, getDissolvingExpiryTime());
			ps.setLong(7, getAllyJoinExpiryTime());
			ps.setLong(8, getAllyPenaltyExpiryTime());
			ps.setInt(9, getAllyPenaltyType().ordinal());
			ps.setInt(10, getId());
			ps.execute();
			if (Config.DEBUG)
			{
				LOG.fine("New clan leader saved in db: " + getId());
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while saving new clan leader to db " + e);
		}
	}
	
	/**
	 * Creamos un nuevo clan.<br>
	 * <b><u>Usado en: </u></b><br>
	 * <li>ClanTable -> createClan()</li>
	 */
	public void store()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(CREATE_CLAN))
		{
			ps.setInt(1, getId());
			ps.setString(2, getName());
			ps.setInt(3, getLevel());
			ps.setInt(4, getCastleId());
			ps.setInt(5, getAllyId());
			ps.setString(6, getAllyName());
			ps.setInt(7, getLeaderId());
			ps.setInt(8, getCrestId());
			ps.setInt(9, getCrestLargeId());
			ps.setInt(10, getAllyCrestId());
			ps.execute();
			
			if (Config.DEBUG)
			{
				LOG.fine("New clan saved in db: " + getId());
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while saving new clan to db " + e);
		}
	}
	
	private void removeMemberFromDB(ClanMemberInstance member, long clanJoinExpiryTime, long clanCreateExpiryTime)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(REMOVE_MEMBER_CLAN))
		{
			ps.setString(1, "");
			ps.setLong(2, clanJoinExpiryTime);
			ps.setLong(3, clanCreateExpiryTime);
			ps.setInt(4, member.getObjectId());
			ps.execute();
			
			if (Config.DEBUG)
			{
				LOG.fine("clan member removed in db: " + getId());
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while removing clan member in db " + e);
		}
	}
	
	private void restoreWars()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT clan1, clan2, wantspeace1, wantspeace2 FROM clan_wars");
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				if (rset.getInt("clan1") == clanId)
				{
					setEnemyClan(rset.getInt("clan2"));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("could not restore clan wars data:" + e);
		}
	}
	
	private void restore()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_CLAN))
		{
			ps.setInt(1, getId());
			try (ResultSet clanData = ps.executeQuery())
			{
				if (clanData.next())
				{
					setName(clanData.getString("clan_name"));
					setLevel(clanData.getInt("clan_level"));
					setHasCastle(clanData.getInt("hasCastle"));
					setAllyId(clanData.getInt("ally_id"));
					setAllyName(clanData.getString("ally_name"));
					setNoticeEnabled(clanData.getBoolean("enabled"));
					setNotice(clanData.getString("notice"));
					
					setCharPenaltyExpiryTime(clanData.getLong("char_penalty_expiry_time"));
					if ((getCharPenaltyExpiryTime() + (Config.ALT_CLAN_JOIN_DAYS * 86400000L)) < System.currentTimeMillis()) // 24*60*60*1000 = 86400000
					{
						setCharPenaltyExpiryTime(0);
					}
					setRecoverPenaltyExpiryTime(clanData.getLong("recover_penalty_expiry_time"));
					if ((getRecoverPenaltyExpiryTime() + (Config.ALT_RECOVERY_PENALTY * 86400000L)) < System.currentTimeMillis()) // 24*60*60*1000 = 86400000
					{
						setRecoverPenaltyExpiryTime(0);
					}
					
					setDissolvingExpiryTime(clanData.getLong("dissolving_expiry_time"));
					setAllyJoinExpiryTime(clanData.getLong("ally_join_expiry_time"));
					
					setAllyPenaltyExpiryTime(clanData.getLong("ally_penalty_expiry_time"), ClanPenaltyType.values()[clanData.getInt("ally_penalty_type")]);// TODO se podria poner para q almacene directamente el enumerador
					if (getAllyPenaltyExpiryTime() < System.currentTimeMillis())
					{
						setAllyPenaltyExpiryTime(0, ClanPenaltyType.NOTHING);
					}
					
					setCrestId(clanData.getInt("crest_id"));
					
					setCrestLargeId(clanData.getInt("crest_large_id"));
					
					setAllyCrestId(clanData.getInt("ally_crest_id"));
					setAuctionBiddedAt(clanData.getInt("auction_bid_at"), false);
					
					int leaderId = (clanData.getInt("leader_id"));
					
					try (PreparedStatement ps2 = con.prepareStatement("SELECT char_name,level,classid,obj_Id FROM characters WHERE clanid=?"))
					{
						ps2.setInt(1, getId());
						try (ResultSet clanMembers = ps2.executeQuery())
						{
							ClanMemberInstance member;
							
							while (clanMembers.next())
							{
								member = new ClanMemberInstance(clanMembers.getString("char_name"), clanMembers.getInt("level"), clanMembers.getInt("classid"), clanMembers.getInt("obj_id"));
								if (member.getObjectId() == leaderId)
								{
									setLeader(member);
								}
								else
								{
									addClanMember(member);
								}
							}
						}
					}
				}
			}
			
			restoreWars();
		}
		catch (Exception e)
		{
			LOG.warning("Clan: error while restoring clan " + e);
		}
	}
	
	public void broadcastToOnlineAllyMembers(AServerPacket packet)
	{
		if (getAllyId() == 0)
		{
			return;
		}
		
		for (Clan clan : ClanData.getInstance().getClans())
		{
			if (clan.getAllyId() == getAllyId())
			{
				clan.broadcastToOnlineMembers(packet);
			}
		}
	}
	
	public void broadcastToOnlineMembers(AServerPacket packet)
	{
		for (ClanMemberInstance member : members.values())
		{
			try
			{
				if (member.isOnline())
				{
					member.getPlayerInstance().sendPacket(packet);
				}
			}
			catch (NullPointerException e)
			{
				// ignore
			}
		}
	}
	
	public void broadcastToOtherOnlineMembers(AServerPacket packet, L2PcInstance player)
	{
		for (ClanMemberInstance member : members.values())
		{
			try
			{
				if (member.isOnline() && (member.getPlayerInstance() != player))
				{
					member.getPlayerInstance().sendPacket(packet);
				}
			}
			catch (NullPointerException e)
			{
				// ignore
			}
		}
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	public ItemContainer getWarehouse()
	{
		return warehouse;
	}
	
	public boolean isAtWarWith(Integer id)
	{
		if ((atWarWith != null) && (!atWarWith.isEmpty()))
		{
			if (atWarWith.contains(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public void setEnemyClan(Clan clan)
	{
		atWarWith.add(clan.getId());
	}
	
	public void setEnemyClan(int clan)
	{
		atWarWith.add(clan);
	}
	
	public void deleteEnemyClan(Clan clan)
	{
		atWarWith.remove(Integer.valueOf(clan.getId()));
	}
	
	public boolean isAtWar()
	{
		if ((atWarWith != null) && (!atWarWith.isEmpty()))
		{
			return true;
		}
		return false;
	}
	
	public void broadcastClanStatus()
	{
		for (L2PcInstance member : getOnlineMembers())
		{
			member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
			member.sendPacket(new PledgeShowMemberListAll(this, member));
		}
	}
	
	public int getAuctionBiddedAt()
	{
		return auctionBiddedAt;
	}
	
	public void setAuctionBiddedAt(int id, boolean storeInDb)
	{
		auctionBiddedAt = id;
		
		if (storeInDb)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION_BID))
			{
				ps.setInt(1, id);
				ps.setInt(2, getId());
				ps.execute();
			}
			catch (Exception e)
			{
				LOG.warning("Could not store auction for clan: " + e);
			}
		}
	}
	
	public long getCharPenaltyExpiryTime()
	{
		return charPenaltyExpiryTime;
	}
	
	public void setCharPenaltyExpiryTime(long time)
	{
		charPenaltyExpiryTime = time;
	}
	
	public long getRecoverPenaltyExpiryTime()
	{
		return recoverPenaltyExpiryTime;
	}
	
	public void setRecoverPenaltyExpiryTime(long time)
	{
		recoverPenaltyExpiryTime = time;
	}
	
	public long getDissolvingExpiryTime()
	{
		return dissolvingExpiryTime;
	}
	
	public void setDissolvingExpiryTime(long time)
	{
		dissolvingExpiryTime = time;
	}
	
	public long getAllyJoinExpiryTime()
	{
		return allyJoinExpiryTime;
	}
	
	public void setAllyJoinExpiryTime(long time)
	{
		allyJoinExpiryTime = time;
	}
	
	public long getAllyPenaltyExpiryTime()
	{
		return allyPenaltyExpiryTime;
	}
	
	public ClanPenaltyType getAllyPenaltyType()
	{
		return allyPenaltyType;
	}
	
	public void setAllyPenaltyExpiryTime(long expiryTime, ClanPenaltyType penaltyType)
	{
		allyPenaltyExpiryTime = expiryTime;
		allyPenaltyType = penaltyType;
	}
	
	public void createAlly(L2PcInstance player, String allyName)
	{
		if (player == null)
		{
			return;
		}
		
		if (Config.DEBUG)
		{
			LOG.fine(player.getObjectId() + "(" + player.getName() + ") requested ally creation from ");
		}
		
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessage.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
			return;
		}
		
		if (getAllyId() != 0)
		{
			player.sendPacket(SystemMessage.ALREADY_JOINED_ALLIANCE);
			return;
		}
		
		if (getLevel() < 5)
		{
			player.sendPacket(SystemMessage.TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
			return;
		}
		
		if (getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (getAllyPenaltyType() == ClanPenaltyType.DISSOLVE_ALLY)
			{
				player.sendPacket(SystemMessage.CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION);
				return;
			}
		}
		
		if (getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessage.YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING);
			return;
		}
		
		if (!Util.isAlphaNumeric(allyName))
		{
			player.sendPacket(SystemMessage.INCORRECT_ALLIANCE_NAME);
			return;
		}
		
		if ((allyName.length() > 16) || (allyName.length() < 2))
		{
			player.sendPacket(SystemMessage.INCORRECT_ALLIANCE_NAME_LENGTH);
			return;
		}
		
		if (ClanData.getInstance().isAllyExists(allyName))
		{
			player.sendPacket(SystemMessage.ALLIANCE_ALREADY_EXISTS);
			return;
		}
		
		setAllyId(getId());
		setAllyName(allyName.trim());
		setAllyPenaltyExpiryTime(0, ClanPenaltyType.NOTHING);
		updateClanInDB();
		
		player.sendPacket(new UserInfo(player));
		
		// TODO: Need correct message id
		player.sendMessage("Alliance " + allyName + " has been created.");
	}
	
	public void dissolveAlly(L2PcInstance player)
	{
		if (getAllyId() == 0)
		{
			player.sendPacket(SystemMessage.NO_CURRENT_ALLIANCES);
			return;
		}
		
		if (!player.isClanLeader() || (getId() != getAllyId()))
		{
			player.sendPacket(SystemMessage.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return;
		}
		
		if (player.isInsideZone(ZoneType.SIEGE))
		{
			player.sendPacket(SystemMessage.CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE);
			return;
		}
		
		broadcastToOnlineAllyMembers(new SystemMessage(SystemMessage.ALLIANCE_DISOLVED));
		
		for (Clan clan : ClanData.getInstance().getClans())
		{
			if ((clan.getAllyId() == getAllyId()) && (clan.getId() != getId()))
			{
				clan.setAllyId(0);
				clan.setAllyName(null);
				clan.setAllyPenaltyExpiryTime(0, ClanPenaltyType.NOTHING);
				clan.updateClanInDB();
			}
		}
		
		setAllyId(0);
		setAllyName(null);
		changeAllyCrest(0, false);
		setAllyPenaltyExpiryTime(System.currentTimeMillis() + (Config.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000), ClanPenaltyType.DISSOLVE_ALLY); // 24*60*60*1000 = 86400000
		updateClanInDB();
		
		// The clan leader should take the XP penalty of a full death.
		player.deathPenalty(false);
	}
	
	/**
	 * Checks if activeChar and target meet various conditions to join a clan
	 * @param  activeChar
	 * @param  target
	 * @return
	 */
	public boolean checkClanJoinCondition(L2PcInstance activeChar, L2PcInstance target)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		int limit = 0;
		
		switch (getLevel())
		{
			case 0:
				limit = 10;
				break;
			case 1:
				limit = 15;
				break;
			case 2:
				limit = 20;
				break;
			case 3:
				limit = 30;
				break;
			default:
				limit = 40;
				break;
		}
		
		if (!activeChar.hasClanPrivilege(ClanPrivilegesType.CL_JOIN_CLAN))
		{
			activeChar.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		
		if (target == null)
		{
			return false;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (getCharPenaltyExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessage.YOU_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN);
			return false;
		}
		
		if (target.getClanId() != 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_WORKING_WITH_ANOTHER_CLAN).addString(target.getName()));
			return false;
		}
		
		if (target.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.C1_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN).addString(target.getName()));
			return false;
		}
		
		if (getMembers().size() >= limit)
		{
			activeChar.sendPacket(SystemMessage.CLAN_IS_FULL);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if activeChar and target meet various conditions to join a clan
	 * @param  activeChar
	 * @param  target
	 * @return
	 */
	public boolean checkAllyJoinCondition(L2PcInstance activeChar, L2PcInstance target)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if ((activeChar.getAllyId() == 0) || !activeChar.isClanLeader() || (activeChar.getClanId() != activeChar.getAllyId()))
		{
			activeChar.sendPacket(SystemMessage.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return false;
		}
		
		Clan leaderClan = activeChar.getClan();
		if (leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (leaderClan.getAllyPenaltyType() == ClanPenaltyType.DISMISS_CLAN)
			{
				activeChar.sendPacket(SystemMessage.CANT_INVITE_CLAN_WITHIN_1_DAY);
				return false;
			}
		}
		
		if (target == null)
		{
			return false;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (target.getClan() == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_MUST_BE_IN_CLAN);
			return false;
		}
		
		if (!target.isClanLeader())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addString(target.getName()));
			return false;
		}
		
		Clan targetClan = target.getClan();
		if (target.getAllyId() != 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE).addString(targetClan.getName()).addString(targetClan.getAllyName()));
			return false;
		}
		
		if (targetClan.getAllyJoinExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessage.CANT_ENTER_ALLIANCE_WITHIN_1_DAY);
			return false;
		}
		
		if (activeChar.isInsideZone(ZoneType.SIEGE) && target.isInsideZone(ZoneType.SIEGE))
		{
			activeChar.sendPacket(SystemMessage.OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE);
			return false;
		}
		
		if (leaderClan.isAtWarWith(targetClan.getId()))
		{
			activeChar.sendPacket(SystemMessage.MAY_NOT_ALLY_CLAN_BATTLE);
			return false;
		}
		
		int numOfClansInAlly = 0;
		for (Clan clan : ClanData.getInstance().getClans())
		{
			if (clan.getAllyId() == activeChar.getAllyId())
			{
				++numOfClansInAlly;
			}
		}
		
		if (numOfClansInAlly >= Config.ALT_MAX_NUM_OF_CLANS_IN_ALLY)
		{
			activeChar.sendPacket(SystemMessage.YOU_HAVE_EXCEEDED_THE_LIMIT);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Cambiamos el lvl de clan.<br>
	 * Usado unicamente desde el handler "AdminPledge"<br>
	 * @param level
	 */
	public void changeLevel(int level)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET clan_level = ? WHERE clan_id = ?"))
		{
			ps.setInt(1, level);
			ps.setInt(2, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("could not increase clan level:" + e);
		}
		
		setLevel(level);
		
		if (getLeader().isOnline())
		{
			L2PcInstance leader = getLeader().getPlayerInstance();
			if (leader == null)
			{
				return;
			}
			
			if (level > 3)
			{
				for (Skill s : SkillData.getSiegeSkills())
				{
					leader.addSkill(s, false);
				}
			}
			else
			{
				for (Skill s : SkillData.getSiegeSkills())
				{
					leader.removeSkill(s);
				}
			}
		}
		
		// notify all the members about it
		broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_LEVEL_INCREASED));
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
	}
	
	// ------------------------------------------
	
	/**
	 * Change the ally crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId      if 0, crest is removed, else new crest id is set and saved to database
	 * @param onlyThisClan
	 */
	public void changeAllyCrest(int crestId, boolean onlyThisClan)
	{
		String sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE clan_id = ?";
		int allyId = clanId;
		if (!onlyThisClan)
		{
			if (crestId == 0)
			{
				CrestData.removeCrest(CrestType.ALLY, allyCrestId);
			}
			sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE ally_id = ?";
			this.allyId = allyId;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sqlStatement))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, allyId);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.warning("Could not update ally crest for ally/clan id " + allyId + " : " + e.getMessage() + e);
		}
		
		if (onlyThisClan)
		{
			allyCrestId = crestId;
			for (L2PcInstance member : getOnlineMembers())
			{
				member.broadcastUserInfo();
			}
		}
		else
		{
			for (Clan clan : ClanData.getInstance().getClans())
			{
				if (clan.getAllyId() == allyId)
				{
					clan.setAllyCrestId(crestId);
					for (L2PcInstance member : clan.getOnlineMembers())
					{
						member.broadcastUserInfo();
					}
				}
			}
		}
	}
	
	/**
	 * Change the clan crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeClanCrest(int crestId)
	{
		if (getCrestId() != 0)
		{
			CrestData.removeCrest(CrestType.PLEDGE, getCrestId());
		}
		
		setCrestId(crestId);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?"))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, getId());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.warning("Could not update crest for clan " + getName() + " [" + getId() + "] : " + e.getMessage() + e);
		}
		
		for (L2PcInstance member : getOnlineMembers())
		{
			member.broadcastUserInfo();
		}
	}
	
	/**
	 * Change the large crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeLargeCrest(int crestId)
	{
		if (getCrestLargeId() != 0)
		{
			CrestData.removeCrest(CrestType.PLEDGE_LARGE, getCrestLargeId());
		}
		
		setCrestLargeId(crestId);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET crest_large_id = ? WHERE clan_id = ?"))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, getId());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOG.warning("Could not update large crest for clan " + getName() + " [" + getId() + "] : " + e.getMessage() + e);
		}
		
		for (L2PcInstance member : getOnlineMembers())
		{
			member.broadcastUserInfo();
		}
	}
	
	private void storeNotice(String notice, boolean enabled)
	{
		if (notice == null)
		{
			notice = "";
		}
		
		if (notice.length() > MAX_NOTICE_LENGTH)
		{
			notice = notice.substring(0, MAX_NOTICE_LENGTH - 1);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_NOTICE))
		{
			ps.setString(1, (enabled) ? "true" : "false");
			ps.setString(2, notice);
			ps.setInt(3, clanId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("L2Clan : could not store clan notice: " + e.getMessage() + e);
		}
		
		this.notice = notice;
		noticeEnabled = enabled;
	}
	
	public void setNoticeEnabledAndStore(boolean enabled)
	{
		storeNotice(notice, enabled);
	}
	
	public void setNoticeAndStore(String notice)
	{
		storeNotice(notice, noticeEnabled);
	}
	
	public boolean isNoticeEnabled()
	{
		return noticeEnabled;
	}
	
	public void setNoticeEnabled(boolean enabled)
	{
		noticeEnabled = enabled;
	}
	
	public String getNotice()
	{
		return (notice == null) ? "" : notice;
	}
	
	public void setNotice(String notice)
	{
		this.notice = notice;
	}
	
	public String getIntroduction()
	{
		return (introduction == null) ? "" : introduction;
	}
	
	public void setIntroduction(String intro, boolean saveOnDb)
	{
		if (saveOnDb)
		{
			if (intro == null)
			{
				intro = "";
			}
			
			if (intro.length() > MAX_INTRODUCTION_LENGTH)
			{
				intro = intro.substring(0, MAX_INTRODUCTION_LENGTH - 1);
			}
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_INTRODUCTION))
			{
				ps.setString(1, intro);
				ps.setInt(2, clanId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOG.warning("L2Clan : could not store clan introduction: " + e.getMessage() + e);
			}
		}
		
		introduction = intro;
	}
	
	public int getSiegeKills()
	{
		return siegeKills.get();
	}
	
	public int getSiegeDeaths()
	{
		return siegeDeaths.get();
	}
	
	public int addSiegeKill()
	{
		return siegeKills.incrementAndGet();
	}
	
	public int addSiegeDeath()
	{
		return siegeDeaths.incrementAndGet();
	}
	
	public void clearSiegeKills()
	{
		siegeKills.set(0);
	}
	
	public void clearSiegeDeaths()
	{
		siegeDeaths.set(0);
	}
}
