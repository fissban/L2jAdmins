package l2j.gameserver.handler.community;

import java.util.StringTokenizer;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class CommunityClan extends AbstractCommunityHandler
{
	private static final String HOME_BAR = "<table width=610 bgcolor=A7A19A><tr><td width=5></td><td width=605><a action=\"bypass _bbsclan home %clanid%\">[GO TO MY CLAN]</a></td></tr></table>";
	
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_bbsclan"
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// bbsclan
		
		if (!st.hasMoreTokens())
		{
			if (activeChar.getClan() == null)
			{
				sendClanList(activeChar, 1);
			}
			else
			{
				sendClanDetails(activeChar, activeChar.getClan().getId());
			}
		}
		else
		{
			final String clanCommand = st.nextToken();
			
			switch (clanCommand)
			{
				case "clan":
					sendClanList(activeChar, Integer.parseInt(st.nextToken()));
					break;
				
				case "home":
					sendClanDetails(activeChar, Integer.parseInt(st.nextToken()));
					break;
				
				case "mail":
					sendClanMail(activeChar, Integer.parseInt(st.nextToken()));
					break;
				
				case "management":
					sendClanManagement(activeChar, Integer.parseInt(st.nextToken()));
					break;
				
				case "notice":
					if (st.hasMoreTokens())
					{
						final String noticeCommand = st.nextToken();
						if (!noticeCommand.isEmpty() && (activeChar.getClan() != null))
						{
							activeChar.getClan().setNoticeEnabledAndStore(Boolean.parseBoolean(noticeCommand));
						}
					}
					sendClanNotice(activeChar, activeChar.getClanId());
					break;
			}
		}
	}
	
	@Override
	public String getWriteList()
	{
		return "Clan";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		switch (ar1)
		{
			case "intro":
				if (Integer.valueOf(ar2) != activeChar.getClanId())
				{
					return;
				}
				
				final Clan clan = ClanData.getInstance().getClanById(activeChar.getClanId());
				if (clan == null)
				{
					return;
				}
				
				clan.setIntroduction(ar3, true);
				sendClanManagement(activeChar, Integer.valueOf(ar2));
				break;
			
			case "notice":
				activeChar.getClan().setNoticeAndStore(ar4);
				sendClanNotice(activeChar, activeChar.getClanId());
				break;
			
			case "mail":
				
				if (Integer.valueOf(ar2) != activeChar.getClanId())
				{
					return;
				}
				
				final Clan clan1 = ClanData.getInstance().getClanById(activeChar.getClanId());
				if (clan1 == null)
				{
					return;
				}
				
				// Retrieve clans members, and store them under a String.
				final StringBuffer membersList = new StringBuffer();
				
				for (ClanMemberInstance player : clan1.getMembers())
				{
					if (player != null)
					{
						if (membersList.length() > 0)
						{
							membersList.append(";");
						}
						
						membersList.append(player.getName());
					}
				}
				CommunityMail.sendLetter(membersList.toString(), ar4, ar5, activeChar);
				sendClanDetails(activeChar, activeChar.getClanId());
				break;
		}
	}
	
	// METODOS VARIOS --------------------------------------------------------------------------------------------//
	
	private static void sendClanMail(L2PcInstance activeChar, int clanId)
	{
		final Clan clan = ClanData.getInstance().getClanById(clanId);
		if (clan == null)
		{
			return;
		}
		
		if ((activeChar.getClanId() != clanId) || !activeChar.isClanLeader())
		{
			activeChar.sendPacket(SystemMessage.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			sendClanList(activeChar, 1);
			return;
		}
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome-mail.htm");
		content = content.replaceAll("%clanid%", Integer.toString(clanId));
		content = content.replaceAll("%clanName%", clan.getName());
		separateAndSend(content, activeChar);
	}
	
	private static void sendClanManagement(L2PcInstance activeChar, int clanId)
	{
		final Clan clan = ClanData.getInstance().getClanById(clanId);
		if (clan == null)
		{
			return;
		}
		
		if ((activeChar.getClanId() != clanId) || !activeChar.isClanLeader())
		{
			activeChar.sendPacket(SystemMessage.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			sendClanList(activeChar, 1);
			return;
		}
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome-management.htm");
		content = content.replaceAll("%clanid%", Integer.toString(clan.getId()));
		send1001(content, activeChar);
		send1002(activeChar, clan.getIntroduction(), "", "");
	}
	
	private static void sendClanNotice(L2PcInstance activeChar, int clanId)
	{
		final Clan clan = ClanData.getInstance().getClanById(clanId);
		if ((clan == null) || (activeChar.getClanId() != clanId))
		{
			return;
		}
		
		if (clan.getLevel() < 2)
		{
			activeChar.sendPacket(SystemMessage.NO_CB_IN_MY_CLAN);
			sendClanList(activeChar, 1);
			return;
		}
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome-notice.htm");
		content = content.replaceAll("%clanid%", Integer.toString(clan.getId()));
		content = content.replace("%enabled%", "[" + String.valueOf(clan.isNoticeEnabled()) + "]");
		content = content.replace("%flag%", String.valueOf(!clan.isNoticeEnabled()));
		content = content.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
		send1001(content, activeChar);
		send1002(activeChar, clan.getNotice(), "", "");
	}
	
	private static void sendClanDetails(L2PcInstance activeChar, int clanId)
	{
		final Clan clan = ClanData.getInstance().getClanById(clanId);
		if (clan == null)
		{
			return;
		}
		
		if (clan.getLevel() < 2)
		{
			activeChar.sendPacket(SystemMessage.NO_CB_IN_MY_CLAN);
			sendClanList(activeChar, 1);
			return;
		}
		
		// Load different HTM following player case, 3 possibilites : randomer, member, clan leader.
		String content;
		if (activeChar.getClanId() != clanId)
		{
			content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome.htm");
		}
		else if (activeChar.isClanLeader())
		{
			content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome-leader.htm");
		}
		else
		{
			content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanhome-member.htm");
		}
		
		content = content.replaceAll("%clanid%", Integer.toString(clan.getId()));
		content = content.replace("%clanIntro%", clan.getIntroduction());
		content = content.replace("%clanName%", clan.getName());
		content = content.replace("%clanLvL%", Integer.toString(clan.getLevel()));
		content = content.replace("%clanMembers%", Integer.toString(clan.getMembersCount()));
		content = content.replaceAll("%clanLeader%", clan.getLeaderName());
		content = content.replace("%allyName%", (clan.getAllyId() > 0) ? clan.getAllyName() : "");
		separateAndSend(content, activeChar);
	}
	
	private static void sendClanList(L2PcInstance activeChar, int index)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "clan/clanlist.htm");
		
		// Player got a clan, show the associated header.
		String homeBar = "";
		
		final Clan playerClan = activeChar.getClan();
		if (playerClan != null)
		{
			homeBar = HOME_BAR.replace("%clanid%", Integer.toString(playerClan.getId()));
		}
		content = content.replace("%homebar%", homeBar);
		
		if (index < 1)
		{
			index = 1;
		}
		
		// List of clans.
		final StringBuilder html = new StringBuilder();
		
		int i = 0;
		for (Clan cl : ClanData.getInstance().getClans())
		{
			if (i > ((index + 1) * 7))
			{
				break;
			}
			
			if (i++ >= ((index - 1) * 7))
			{
				html.append("<table width=610><tr>");
				html.append("<td width=5></td>");
				html.append("<td width=150 align=center><a action=\"bypass _bbsclan;home;" + Integer.toString(cl.getId()) + "\">" + cl.getName() + "</a></td>");
				html.append("<td width=150 align=center>" + cl.getLeaderName() + "</td>");
				html.append("<td width=100 align=center>" + Integer.toString(cl.getLevel()) + "</td>");
				html.append("<td width=200 align=center>" + Integer.toString(cl.getMembersCount()) + "</td>");
				html.append("<td width=5></td>");
				html.append("</tr></table>");
				html.append("<br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>");
			}
		}
		
		html.append("<table><tr>");
		
		if (index == 1)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16></td>");
		}
		else
		{
			html.append("<td><button action=\"_bbsclan;clan;" + Integer.toString(index - 1) + "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		
		i = 0;
		int nbp = ClanData.getInstance().getClans().size() / 8;
		if ((nbp * 8) != ClanData.getInstance().getClans().size())
		{
			nbp++;
		}
		
		for (i = 1; i <= nbp; i++)
		{
			if (i == index)
			{
				html.append("<td> " + Integer.toString(i) + " </td>");
			}
			else
			{
				html.append("<td><a action=\"bypass _bbsclan;clan;" + Integer.toString(i) + "\"> " + Integer.toString(i) + " </a></td>");
			}
		}
		
		if (index == nbp)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16></td>");
		}
		else
		{
			html.append("<td><button action=\"bypass _bbsclan;clan;" + Integer.toString(index + 1) + "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		
		html.append("</tr></table>");
		
		content = content.replace("%clanlist%", html.toString());
		separateAndSend(content, activeChar);
	}
}
