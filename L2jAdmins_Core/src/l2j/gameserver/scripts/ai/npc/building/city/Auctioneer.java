package l2j.gameserver.scripts.ai.npc.building.city;

import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.data.AuctionData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.clanhalls.auction.Auction;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban, zarie
 */
public class Auctioneer extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		7767,
		7768,
		7769,
		7770,
		7771
	};
	
	// Html
	private static final String HTML_PATH = "data/html/auction/";
	
	private static final Map<Integer, Auction> pendingAuctions = new ConcurrentHashMap<>();
	
	public Auctioneer()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setAuctioner(true);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getCastle().getSiege().isInProgress())
		{
			return HTML_PATH + "auction-busy.htm";
		}
		
		return HTML_PATH + "auction.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc.getCastle().getSiege().isInProgress())
		{
			player.sendMessage("Busy because of siege.");
		}
		else
		{
			var st = new StringTokenizer(event, " ");
			var command = st.nextToken();
			int param1;
			int param2;
			try
			{
				param1 = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
				param2 = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
			}
			catch (Exception e)
			{
				player.sendMessage("Invalid value");
				return null;
			}
			
			if (command.equalsIgnoreCase("main"))
			{
				return HTML_PATH + "auction.htm";
			}
			if (command.equalsIgnoreCase("auction"))
			{
				try
				{
					var days = param1;
					var bid = param2;
					if (st.hasMoreTokens())
					{
						bid = Integer.parseInt(st.nextToken());
					}
					
					var auction = new Auction(player.getClan().getClanHallId(), player.getClan(), days * 86400000, bid, ClanHallData.getClanHallByOwner(player.getClan()).getName());
					
					pendingAuctions.put(auction.getId(), auction);
					
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitSale3.htm");
					html.replace("%x%", days);
					html.replace("%AGIT_AUCTION_END_YY%", auction.getEndDate().get(Calendar.YEAR));
					html.replace("%AGIT_AUCTION_END_MM%", auction.getEndDate().get(Calendar.MONTH) + 1);
					html.replace("%AGIT_AUCTION_END_DD%", auction.getEndDate().get(Calendar.DAY_OF_MONTH));
					html.replace("%AGIT_AUCTION_END_HH%", auction.getEndDate().get(Calendar.HOUR_OF_DAY));
					html.replace("%AGIT_AUCTION_MINBID%", auction.getStartingBid());
					html.replace("%AGIT_AUCTION_MIN%", auction.getStartingBid());
					html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getClanHallByOwner(player.getClan()).getDesc());
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("confirmAuction"))
			{
				try
				{
					var a = pendingAuctions.get(player.getClan().getClanHallId());
					a.confirmAuction();
					pendingAuctions.remove(player.getClan().getClanHallId());
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("bidding"))
			{
				try
				{
					var auctionId = param1;
					var auction = AuctionData.getInstance().getAuction(auctionId);
					
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitAuctionInfo.htm");
					if (auction != null)
					{
						html.replace("%AGIT_NAME%", auction.getClanHallName());
						html.replace("%OWNER_PLEDGE_NAME%", auction.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", auction.getSellerName());
						html.replace("%AGIT_SIZE%", ClanHallData.getClanHallById(auction.getId()).getGrade() * 10);
						html.replace("%AGIT_LEASE%", ClanHallData.getClanHallById(auction.getId()).getLease());
						html.replace("%AGIT_LOCATION%", ClanHallData.getClanHallById(auction.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", auction.getEndDate().get(Calendar.YEAR));
						html.replace("%AGIT_AUCTION_END_MM%", auction.getEndDate().get(Calendar.MONTH) + 1);
						html.replace("%AGIT_AUCTION_END_DD%", auction.getEndDate().get(Calendar.DAY_OF_MONTH));
						html.replace("%AGIT_AUCTION_END_HH%", auction.getEndDate().get(Calendar.HOUR_OF_DAY));
						html.replace("%AGIT_AUCTION_REMAIN%", (auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000 + " hours " + (((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60) + " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", auction.getStartingBid());
						html.replace("%AGIT_AUCTION_COUNT%", auction.getBidders().size());
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getClanHallById(auction.getId()).getDesc());
						html.replace("%AGIT_LINK_BACK%", "bypass -h Quest Auctioneer list");
						html.replace("%AGIT_LINK_BIDLIST%", "bypass -h Quest Auctioneer bidlist " + auction.getId());
						html.replace("%AGIT_LINK_RE%", "bypass -h Quest Auctioneer bid1 " + auction.getId());
					}
					else
					{
						LOG.warning("Auctioneer: Auction null for AuctionId : " + auctionId);
					}
					
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("bid0"))
			{
				try
				{
					var auctionId = param1;
					var bid = param2;
					
					AuctionData.getInstance().getAuction(auctionId).setBid(player, bid);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("bid1"))
			{
				var auctionId = param1;
				
				if ((player.getClan() == null) || (player.getClan().getLevel() < 2))
				{
					player.sendMessage("Your clan's level needs to be at least 2, before you can bid in an auction.");
					return null;
				}
				
				if (((player.getClan().getAuctionBiddedAt() > 0) && (player.getClan().getAuctionBiddedAt() != auctionId)) || (player.getClan().hasClanHall()))
				{
					player.sendPacket(SystemMessage.ALREADY_SUBMITTED_BID);
					return null;
				}
				
				try
				{
					int minimumBid = AuctionData.getInstance().getAuction(auctionId).getHighestBidderMaxBid();
					if (minimumBid == 0)
					{
						minimumBid = AuctionData.getInstance().getAuction(auctionId).getStartingBid();
					}
					
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBid1.htm");
					html.replace("%AGIT_LINK_BACK%", "bypass -h Quest Auctioneer bidding " + auctionId);
					html.replace("%PLEDGE_ADENA%", player.getClan().getWarehouse().getAdena());
					html.replace("%AGIT_AUCTION_MINBID%", minimumBid + 1);
					html.replace("%AGIT_ID%", auctionId);
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("list"))
			{
				var items = new StringBuilder();
				
				for (var a : AuctionData.getInstance().getAuctions())
				{
					var year = (a.getEndDate().get(Calendar.YEAR) + "").substring(2, 4);
					var month = (a.getEndDate().get(Calendar.MONTH) + 1);
					var day = a.getEndDate().get(Calendar.DATE);
					
					items.append("<table width=270 border=0>");
					items.append("<tr>");
					items.append("<td width=50 align=center>" + ClanHallData.getClanHallById(a.getId()).getLocation() + "</td>");
					items.append("<td width=100 align=center><a action=\"bypass -h Quest Auctioneer bidding " + a.getId() + "\">" + a.getClanHallName() + "</a></td>");
					items.append("<td width=50 align=center>" + year + "/" + month + "/" + day + "</td>");
					items.append("<td width=70 align=center>" + a.getStartingBid() + "</td>");
					items.append("</tr>");
					items.append("</table>");
				}
				
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitAuctionList.htm");
				html.replace("%itemsField%", items.toString());
				player.sendPacket(html);
			}
			else if (command.equalsIgnoreCase("bidlist"))
			{
				var auctionId = param1;
				if (auctionId == 0)
				{
					if (player.getClan().getAuctionBiddedAt() <= 0)
					{
						return null;
					}
					auctionId = player.getClan().getAuctionBiddedAt();
				}
				
				var biders = new StringBuilder();
				
				for (var b : AuctionData.getInstance().getAuction(auctionId).getBidders().values())
				{
					biders.append("<table width=270 border=0>");
					biders.append("<tr>");
					biders.append("<td width=100 align=center>" + b.getClanName() + "</td>");
					biders.append("<td width=100 align=center>" + b.getBidderName() + "</td>");
					biders.append("<td width=70 align=center>" + b.getTimeBid().get(Calendar.YEAR) + "/" + (b.getTimeBid().get(Calendar.MONTH) + 1) + "/" + b.getTimeBid().get(Calendar.DATE) + "</td>");
					// biders.append("<td>"+b.getBid()+"</td>");
					biders.append("</tr>");
					biders.append("</table>");
				}
				
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitBidderList.htm");
				html.replace("%AGIT_LIST%", biders.toString());
				html.replace("%x%", auctionId);
				player.sendPacket(html);
			}
			else if (command.equalsIgnoreCase("selectedItems"))
			{
				if (player.getClan() == null)
				{
					player.sendMessage("you have not made any auction.");
					return null;
				}
				
				if ((player.getClan() != null) || (player.getClan().getLevel() < 2))
				{
					player.sendMessage("you have not made any auction.");
					return null;
				}
				
				if ((!player.getClan().hasClanHall()) && (player.getClan().getAuctionBiddedAt() > 0))
				{
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBidInfo.htm");
					var auction = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (auction != null)
					{
						html.replace("%AGIT_NAME%", auction.getClanHallName());
						html.replace("%OWNER_PLEDGE_NAME%", auction.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", auction.getSellerName());
						html.replace("%AGIT_SIZE%", ClanHallData.getClanHallById(auction.getId()).getGrade() * 10);
						html.replace("%AGIT_LEASE%", ClanHallData.getClanHallById(auction.getId()).getLease());
						html.replace("%AGIT_LOCATION%", ClanHallData.getClanHallById(auction.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", auction.getEndDate().get(Calendar.YEAR));
						html.replace("%AGIT_AUCTION_END_MM%", auction.getEndDate().get(Calendar.MONTH) + 1);
						html.replace("%AGIT_AUCTION_END_DD%", auction.getEndDate().get(Calendar.DAY_OF_MONTH));
						html.replace("%AGIT_AUCTION_END_HH%", auction.getEndDate().get(Calendar.HOUR_OF_DAY));
						html.replace("%AGIT_AUCTION_REMAIN%", (auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000 + " hours " + (((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60) + " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", auction.getStartingBid());
						html.replace("%AGIT_AUCTION_MYBID%", auction.getBidders().get(player.getClanId()).getBid());
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getClanHallById(auction.getId()).getDesc());
					}
					else
					{
						LOG.warning("Auctioneer Auction null for AuctionBiddedAt : " + player.getClan().getAuctionBiddedAt());
					}
					
					player.sendPacket(html);
				}
				else if ((player.getClan() != null) && (AuctionData.getInstance().getAuction(player.getClan().getClanHallId()) != null))
				{
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitSaleInfo.htm");
					var auction = AuctionData.getInstance().getAuction(player.getClan().getClanHallId());
					
					if (auction != null)
					{
						html.replace("%AGIT_NAME%", auction.getClanHallName());
						html.replace("%AGIT_OWNER_PLEDGE_NAME%", auction.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", auction.getSellerName());
						html.replace("%AGIT_SIZE%", ClanHallData.getClanHallById(auction.getId()).getGrade() * 10);
						html.replace("%AGIT_LEASE%", ClanHallData.getClanHallById(auction.getId()).getLease());
						html.replace("%AGIT_LOCATION%", ClanHallData.getClanHallById(auction.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", auction.getEndDate().get(Calendar.YEAR));
						html.replace("%AGIT_AUCTION_END_MM%", auction.getEndDate().get(Calendar.MONTH) + 1);
						html.replace("%AGIT_AUCTION_END_DD%", auction.getEndDate().get(Calendar.DAY_OF_MONTH));
						html.replace("%AGIT_AUCTION_END_HH%", auction.getEndDate().get(Calendar.HOUR_OF_DAY));
						html.replace("%AGIT_AUCTION_REMAIN%", (auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000 + " hours " + (((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60) + " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", auction.getStartingBid());
						html.replace("%AGIT_AUCTION_BIDCOUNT%", auction.getBidders().size());
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getClanHallById(auction.getId()).getDesc());
						html.replace("%id%", auction.getId());
					}
					else
					{
						LOG.warning("Auctioneer Auction null for getHasClanHall : " + player.getClan().getClanHallId());
					}
					
					player.sendPacket(html);
				}
				else if ((player.getClan() != null) && (player.getClan().hasClanHall()))
				{
					int clanHallId = player.getClan().getClanHallId();
					
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitInfo.htm");
					html.replace("%AGIT_NAME%", ClanHallData.getClanHallById(clanHallId).getName());
					html.replace("%AGIT_OWNER_PLEDGE_NAME%", player.getClan().getName());
					html.replace("%OWNER_PLEDGE_MASTER%", player.getClan().getLeaderName());
					html.replace("%AGIT_SIZE%", ClanHallData.getClanHallById(clanHallId).getGrade() * 10);
					html.replace("%AGIT_LEASE%", ClanHallData.getClanHallById(clanHallId).getLease());
					html.replace("%AGIT_LOCATION%", ClanHallData.getClanHallById(clanHallId).getLocation());
					player.sendPacket(html);
				}
			}
			else if (command.equalsIgnoreCase("cancelBid"))
			{
				var bid = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).getBidders().get(player.getClanId()).getBid();
				
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitBidCancel.htm");
				html.replace("%AGIT_BID%", bid);
				html.replace("%AGIT_BID_REMAIN%", (bid * 0.9) + "");
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("doCancelBid"))
			{
				if (AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()) != null)
				{
					AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).cancelBid(player.getClanId());
					player.sendMessage("You have succesfully cancelled your bidding at the auction");
				}
			}
			else if (command.equalsIgnoreCase("cancelAuction"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSaleCancel.htm");
				html.replace("%AGIT_DEPOSIT%", ClanHallData.getClanHallByOwner(player.getClan()).getLease());
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("doCancelAuction"))
			{
				if (AuctionData.getInstance().getAuction(player.getClan().getClanHallId()) != null)
				{
					AuctionData.getInstance().getAuction(player.getClan().getClanHallId()).cancelAuction();
					player.sendMessage("Your auction has been canceled");
				}
			}
			else if (command.equalsIgnoreCase("sale2"))
			{
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSale2.htm");
				html.replace("%AGIT_LAST_PRICE%", ClanHallData.getClanHallByOwner(player.getClan()).getLease());
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("sale"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSale1.htm");
				html.replace("%AGIT_DEPOSIT%", ClanHallData.getClanHallByOwner(player.getClan()).getLease());
				html.replace("%AGIT_PLEDGE_ADENA%", player.getClan().getWarehouse().getAdena());
				player.sendPacket(html);
			}
			else if (command.equalsIgnoreCase("rebid"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				try
				{
					var html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBid2.htm");
					var auction = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (auction != null)
					{
						html.replace("%AGIT_AUCTION_MINBID%", auction.getStartingBid());
						html.replace("%AGIT_AUCTION_END_YY%", auction.getEndDate().get(Calendar.YEAR));
						html.replace("%AGIT_AUCTION_END_MM%", auction.getEndDate().get(Calendar.MONTH) + 1);
						html.replace("%AGIT_AUCTION_END_DD%", auction.getEndDate().get(Calendar.DAY_OF_MONTH));
						html.replace("%AGIT_AUCTION_END_HH%", auction.getEndDate().get(Calendar.HOUR_OF_DAY));
						html.replace("npc_%objectId%_bid1", "Quest Auctionner bid1 " + auction.getId());
					}
					else
					{
						LOG.warning("Auctioneer Auction null for AuctionBiddedAt : " + player.getClan().getAuctionBiddedAt());
					}
					
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (command.equalsIgnoreCase("location"))
			{
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "location.htm");
				html.replace("%location%", MapRegionData.getInstance().getClosestTownName(player));
				html.replace("%LOCATION%", getPictureName(player));
				player.sendPacket(html);
			}
			else if (command.equalsIgnoreCase("start"))
			{
				var html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "auction.htm");
				player.sendPacket(html);
			}
		}
		
		return null;
	}
	
	private static String getPictureName(L2PcInstance plyr)
	{
		switch (MapRegionData.getInstance().getMapRegion(plyr.getX(), plyr.getY()))
		{
			case 5:
				return "GLUDIO";
			case 6:
				return "GLUDIN";
			case 7:
				return "DION";
			case 8:
				return "GIRAN";
			case 15:
				return "GODARD";
			default:
				return "ADEN";
		}
	}
}
