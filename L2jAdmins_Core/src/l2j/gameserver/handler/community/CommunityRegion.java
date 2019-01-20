package l2j.gameserver.handler.community;

import java.util.List;
import java.util.StringTokenizer;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class CommunityRegion extends AbstractCommunityHandler
{
	private static final String REGION_LIST = "<table><tr><td width=5></td><td width=160><a action=\"bypass _bbsloc;%castleId%\">%castleName%</a></td><td width=160>%ownerName%</td><td width=160>%allyName%</td><td width=120>%actualTax%</td><td width=5></td></tr></table><br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>";
	private static final String CLAN_HALL_BAR = "<br><br><table width=610 bgcolor=A7A19A><tr><td width=5></td><td width=200>Clan Hall Name</td><td width=200>Owning Clan</td><td width=200>Clan Leader Name</td><td width=5></td></tr></table><br1>";
	private static final String CLAN_HALL_LIST = "<table><tr><td width=5></td><td width=200>%chName%</td><td width=200>%clanName%</td><td width=200>%leaderName%</td><td width=5></td></tr></table><br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>";
	
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_bbsloc",
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// bbsloc
		
		if (!st.hasMoreTokens())
		{
			showRegionsList(activeChar);
		}
		else
		{
			showRegion(activeChar, Integer.parseInt(st.nextToken()));
		}
		
		return;
	}
	
	@Override
	public String getWriteList()
	{
		return "";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		//
	}
	
	// METODOS PARSECMD ---------------------------------------------------------------------------------------
	
	private static void showRegionsList(L2PcInstance activeChar)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "region/castlelist.htm");
		String list = "";
		
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			final Clan owner = ClanData.getInstance().getClanById(castle.getOwnerId());
			
			list += REGION_LIST;
			list = list.replace("%castleId%", Integer.toString(castle.getId()));
			list = list.replace("%castleName%", castle.getName());
			list = list.replace("%ownerName%", ((owner != null) ? "<a action=\"bypass _bbsclan;home;" + owner.getId() + "\">" + owner.getName() + "</a>" : "None"));
			list = list.replace("%allyName%", (((owner != null) && (owner.getAllyId() > 0)) ? owner.getAllyName() : "None"));
			list = list.replace("%actualTax%", ((owner != null) ? Integer.toString(castle.getTaxPercent()) : "0"));
		}
		content = content.replace("%castleList%", list);
		separateAndSend(content, activeChar);
	}
	
	private static void showRegion(L2PcInstance activeChar, int castleId)
	{
		final Castle castle = CastleData.getInstance().getCastleById(castleId);
		final Clan owner = ClanData.getInstance().getClanById(castle.getOwnerId());
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "region/castle.htm");
		
		content = content.replace("%castleName%", castle.getName());
		content = content.replace("%tax%", Integer.toString(castle.getTaxPercent()));
		content = content.replace("%lord%", ((owner != null) ? owner.getLeaderName() : "None"));
		content = content.replace("%clanName%", ((owner != null) ? "<a action=\"bypass _bbsclan;home;" + owner.getId() + "\">" + owner.getName() + "</a>" : "None"));
		content = content.replace("%allyName%", (((owner != null) && (owner.getAllyId() > 0)) ? owner.getAllyName() : "None"));
		content = content.replace("%siegeDate%", Util.formatDate(castle.getSiegeDate().getTimeInMillis(), "yyyy/MM/dd HH:mm"));
		
		String list = "";
		
		final List<ClanHall> clanHalls = ClanHallData.getClanHallsByLocation(castle.getName());
		if ((clanHalls != null) && !clanHalls.isEmpty())
		{
			list = CLAN_HALL_BAR;
			
			for (ClanHall ch : clanHalls)
			{
				final Clan chOwner = ClanData.getInstance().getClanById(ch.getOwnerId());
				
				list += CLAN_HALL_LIST;
				list = list.replace("%chName%", ch.getName());
				list = list.replace("%clanName%", ((chOwner != null) ? "<a action=\"bypass _bbsclan;home;" + chOwner.getId() + "\">" + chOwner.getName() + "</a>" : "None"));
				list = list.replace("%leaderName%", ((chOwner != null) ? chOwner.getLeaderName() : "None"));
			}
		}
		content = content.replaceAll("%hallsList%", list);
		separateAndSend(content, activeChar);
	}
}
