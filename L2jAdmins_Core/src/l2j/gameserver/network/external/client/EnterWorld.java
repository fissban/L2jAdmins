package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.floodprotector.FloodProtector;
import l2j.gameserver.instancemanager.PetitionManager;
import l2j.gameserver.instancemanager.communitybbs.Community;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.Hero;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.castle.siege.type.PlayerSiegeStateType;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.Die;
import l2j.gameserver.network.external.server.EtcStatusUpdate;
import l2j.gameserver.network.external.server.ExMailArrived;
import l2j.gameserver.network.external.server.ExStorageMaxCount;
import l2j.gameserver.network.external.server.FriendList;
import l2j.gameserver.network.external.server.GameGuardQuery;
import l2j.gameserver.network.external.server.HennaInfo;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;
import l2j.gameserver.network.external.server.PledgeShowMemberListUpdate;
import l2j.gameserver.network.external.server.PledgeStatusChanged;
import l2j.gameserver.network.external.server.QuestList;
import l2j.gameserver.network.external.server.ShortCutInit;
import l2j.gameserver.network.external.server.SignsSky;
import l2j.gameserver.network.external.server.SkillCoolTime;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import main.EngineModsManager;

/**
 * Enter World Packet Handler<BR>
 * 0000: 03<BR>
 * packet format rev656 cbdddd
 * @version $Revision: 1.16.2.1.2.7 $ $Date: 2005/03/29 23:15:33 $
 */
public class EnterWorld extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			getClient().closeNow();
			LOG.warning("EnterWorld failed! activeChar is null...");
			return;
		}
		
		// Register in flood protector
		FloodProtector.getInstance().registerNewPlayer(activeChar);
		
		if (L2World.getInstance().getObject(activeChar.getObjectId()) != null)
		{
			LOG.warning("User already exist in OID map! User " + activeChar.getName() + " is a character clone");
		}
		
		if (activeChar.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE)
			{
				activeChar.setIsInvul(true);
				activeChar.sendMessage("Entering world in Invulnerable mode.");
			}
			if (Config.GM_STARTUP_INVISIBLE)
			{
				activeChar.setInvisible();
				activeChar.sendMessage("Entering world in Invisible mode.");
			}
			if (Config.GM_STARTUP_SILENCE)
			{
				activeChar.setInRefusalMode(true);
				activeChar.sendMessage("Entering world in Message Refusal mode.");
			}
			if (Config.GM_STARTUP_AUTO_LIST)
			{
				GmListData.getInstance().addGm(activeChar);
			}
			if (Config.GM_NAME_COLOR_ENABLED)
			{
				if (activeChar.getAccessLevel() == 8)
				{
					activeChar.setNameColor(Config.ADMIN_NAME_COLOR);
				}
				else if (activeChar.getAccessLevel() >= 1)
				{
					activeChar.setNameColor(Config.GM_NAME_COLOR);
				}
			}
		}
		
		if (activeChar.getCurrentHp() < 0.5)
		{
			activeChar.setIsDead(true);
		}
		
		if (activeChar.getClan() != null)
		{
			if (activeChar.isClanLeader() && (activeChar.getClan().getLevel() > 3))
			{
				for (Skill s : SkillData.getSiegeSkills())
				{
					activeChar.addSkill(s, false);
				}
			}
			
			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				if (!siege.isInProgress())
				{
					continue;
				}
				if (siege.isAttacker(activeChar.getClan()))
				{
					activeChar.setSiegeState(PlayerSiegeStateType.ATACKER);
				}
				if (siege.isDefender(activeChar.getClan()))
				{
					activeChar.setSiegeState(PlayerSiegeStateType.DEFENDER);
				}
			}
		}
		
		sendPacket(new UserInfo(activeChar));
		activeChar.getMacroses().sendUpdate();
		sendPacket(new ItemList(activeChar, false));
		sendPacket(new ShortCutInit(activeChar));
		sendPacket(new HennaInfo(activeChar));
		
		Script.playerEnter(activeChar);
		
		ScriptsData.getAllQuests().stream().filter(s -> (s != null) && s.getOnEnterWorld()).forEach(s -> s.notifyEnterWorld(activeChar));
		ScriptsData.getAllScripts().stream().filter(s -> (s != null) && s.getOnEnterWorld()).forEach(s -> s.notifyEnterWorld(activeChar));
		
		activeChar.sendPacket(new QuestList());
		
		// load tutorial
		ScriptState ss = activeChar.getScriptState("Q255_Tutorial");
		if (ss != null)
		{
			ss.getQuest().notifyEvent("UC", null, activeChar);
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
		{
			activeChar.setProtection(true);
		}
		
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		// Unread mails make a popup appears.
		if (Config.COMMUNITY_ENABLE && (Community.getInstance().checkUnreadMail(activeChar) > 0))
		{
			activeChar.sendPacket(SystemMessage.NEW_MAIL);
			activeChar.playSound(PlaySoundType.SYS_MSG_1233);
			activeChar.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		Clan clan = activeChar.getClan();
		// Clan notice, if active.
		if (Config.COMMUNITY_ENABLE && (clan != null) && clan.isNoticeEnabled())
		{
			NpcHtmlMessage notice = new NpcHtmlMessage(0);
			notice.setFile("data/html/clan_notice.htm");
			notice.replace("%clan_name%", clan.getName());
			notice.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(notice);
		}
		else
		{
			NpcHtmlMessage newa = new NpcHtmlMessage(0);
			newa.setFile("data/html/servnews.htm");
			sendPacket(newa);
		}
		
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			sendPacket(new SignsSky());
		}
		
		for (Effect e : activeChar.getAllEffects())
		{
			if (e.getEffectType() == EffectType.HEAL_OVER_TIME)
			{
				e.exit();
			}
			
			if (e.getEffectType() == EffectType.COMBAT_POINT_HEAL_OVER_TIME)
			{
				e.exit();
			}
			
			// Remove Clan Quest
			if (e.getSkill().getId() == 4082)
			{
				e.exit();
			}
		}
		
		activeChar.updateEffectIcons();
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		// Expand Skill
		activeChar.sendPacket(new ExStorageMaxCount(activeChar));
		
		sendPacket(new FriendList(activeChar));
		
		activeChar.sendPacket(SystemMessage.WELCOME_TO_LINEAGE);
		
		SevenSignsManager.getInstance().sendCurrentPeriodMsg(activeChar);
		AnnouncementsData.getInstance().sendAnnouncements(activeChar);
		
		PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		if ((activeChar.getClanId() != 0) && (activeChar.getClan() != null))
		{
			sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar));
			sendPacket(new PledgeStatusChanged(activeChar.getClan()));
		}
		
		notifyFriends(activeChar);
		notifyClanMembers(activeChar);
		
		activeChar.onPlayerEnter();
		
		activeChar.sendPacket(new SkillCoolTime(activeChar));
		
		if (Olympiad.getInstance().playerInStadia(activeChar))
		{
			activeChar.teleToLocation(MapRegionData.TeleportWhereType.TOWN);
		}
		
		if (activeChar.isAlikeDead())
		{
			// no broadcast needed since the player will already spawn dead to others
			sendPacket(new Die(activeChar));
		}
		
		switch (activeChar.getSiegeState())
		{
			case NOT_INVOLVED:
			case ATACKER:
				if (!activeChar.isGM() && activeChar.isInsideZone(ZoneType.SIEGE))
				{
					activeChar.teleToLocation(TeleportWhereType.TOWN);
					activeChar.sendMessage("You have been teleported to the nearest town due to being in a siege zone.");
				}
				break;
		}
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CLAN_MEMBERSHIP_TERMINATED));
		}
		
		if (activeChar.getClan() != null)
		{
			// Add message if clanHall not paid. Possibly this is custom...
			ClanHall clanHall = ClanHallData.getInstance().getClanHallByOwner(activeChar.getClan());
			if ((clanHall != null) && !clanHall.getPaid())
			{
				activeChar.sendPacket(SystemMessage.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			}
		}
		
		if (Config.GAMEGUARD_ENFORCE)
		{
			activeChar.sendPacket(new GameGuardQuery());
		}
		
		if (Hero.getInstance().getHeroes().containsKey(activeChar.getObjectId()))
		{
			activeChar.setHero(true);
		}
		
		EngineModsManager.onEnterWorld(activeChar);
	}
	
	/**
	 * @param activeChar
	 */
	private void notifyFriends(L2PcInstance activeChar)
	{
		SystemMessage sm = new SystemMessage(SystemMessage.FRIEND_S1_HAS_LOGGED_IN).addString(activeChar.getName());
		
		for (Integer friend : activeChar.getFriendList())
		{
			if (friend == null)
			{
				continue;
			}
			
			// notify online friends
			L2PcInstance friendChar = L2World.getInstance().getPlayer(friend);
			if (friendChar != null) // friend logged in.
			{
				friendChar.sendPacket(new FriendList(friendChar));
				friendChar.sendPacket(sm);
				friendChar.playSound(PlaySoundType.SYS_FRIEND_LOGIN);
			}
		}
	}
	
	/**
	 * @param activeChar
	 */
	private void notifyClanMembers(L2PcInstance activeChar)
	{
		Clan clan = activeChar.getClan();
		if (clan != null)
		{
			clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
			clan.broadcastToOtherOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_LOGGED_IN).addString(activeChar.getName()), activeChar);
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
		}
	}
}
