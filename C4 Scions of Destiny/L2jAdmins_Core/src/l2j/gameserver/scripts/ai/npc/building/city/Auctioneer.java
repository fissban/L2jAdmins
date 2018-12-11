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
import l2j.gameserver.model.holder.BidderHolder;
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
			StringTokenizer st = new StringTokenizer(event, " ");
			st.nextToken(); // actual command
			
			String val = "";
			if (st.hasMoreTokens())
			{
				val = st.nextToken();
			}
			
			if (event.equalsIgnoreCase("auction"))
			{
				try
				{
					final int days = Integer.parseInt(val);
					try
					{
						int bid = 0;
						if (st.hasMoreTokens())
						{
							bid = Integer.parseInt(st.nextToken());
						}
						
						Auction auction = new Auction(player.getClan().getClanHallId(), player.getClan(), days * 86400000, bid, ClanHallData.getInstance().getClanHallByOwner(player.getClan()).getName());
						
						pendingAuctions.put(auction.getId(), auction);
						
						NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(HTML_PATH + "AgitSale3.htm");
						html.replace("%x%", val);
						html.replace("%AGIT_AUCTION_END_YY%", String.valueOf(auction.getEndDate().get(Calendar.YEAR)));
						html.replace("%AGIT_AUCTION_END_MM%", String.valueOf(auction.getEndDate().get(Calendar.MONTH) + 1));
						html.replace("%AGIT_AUCTION_END_DD%", String.valueOf(auction.getEndDate().get(Calendar.DAY_OF_MONTH)));
						html.replace("%AGIT_AUCTION_END_HH%", String.valueOf(auction.getEndDate().get(Calendar.HOUR_OF_DAY)));
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(auction.getStartingBid()));
						html.replace("%AGIT_AUCTION_MIN%", String.valueOf(auction.getStartingBid()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getInstance().getClanHallByOwner(player.getClan()).getDesc());
						player.sendPacket(html);
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid bid!");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction duration!");
				}
			}
			if (event.equalsIgnoreCase("confirmAuction"))
			{
				try
				{
					Auction a = pendingAuctions.get(player.getClan().getClanHallId());
					a.confirmAuction();
					pendingAuctions.remove(player.getClan().getClanHallId());
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (event.startsWith("bidding"))
			{
				try
				{
					int auctionId = Integer.parseInt(val);
					
					Auction auction = AuctionData.getInstance().getAuction(auctionId);
					
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitAuctionInfo.htm");
					if (auction != null)
					{
						html.replace("%AGIT_NAME%", auction.getClanHallName());
						html.replace("%OWNER_PLEDGE_NAME%", auction.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", auction.getSellerName());
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallData.getInstance().getClanHallById(auction.getId()).getGrade() * 10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallData.getInstance().getClanHallById(auction.getId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallData.getInstance().getClanHallById(auction.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", String.valueOf(auction.getEndDate().get(Calendar.YEAR)));
						html.replace("%AGIT_AUCTION_END_MM%", String.valueOf(auction.getEndDate().get(Calendar.MONTH) + 1));
						html.replace("%AGIT_AUCTION_END_DD%", String.valueOf(auction.getEndDate().get(Calendar.DAY_OF_MONTH)));
						html.replace("%AGIT_AUCTION_END_HH%", String.valueOf(auction.getEndDate().get(Calendar.HOUR_OF_DAY)));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000) + " hours "
							+ String.valueOf((((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60)) + " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(auction.getStartingBid()));
						html.replace("%AGIT_AUCTION_COUNT%", String.valueOf(auction.getBidders().size()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getInstance().getClanHallById(auction.getId()).getDesc());
						html.replace("%AGIT_LINK_BACK%", "bypass -h Quest Auctioneer list");
						html.replace("%AGIT_LINK_BIDLIST%", "bypass -h Quest Auctioneer bidlist " + auction.getId());
						html.replace("%AGIT_LINK_RE%", "bypass -h Quest Auctioneer bid1 " + auction.getId());
					}
					else
					{
						LOG.warning("Auctioneer Auction null for AuctionId : " + auctionId);
					}
					
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (event.startsWith("bid0"))
			{
				try
				{
					int auctionId = Integer.parseInt(val);
					try
					{
						int bid = 0;
						if (st.countTokens() >= 1)
						{
							bid = Integer.parseInt(st.nextToken());
						}
						
						AuctionData.getInstance().getAuction(auctionId).setBid(player, bid);
					}
					catch (Exception e)
					{
						player.sendMessage("Invalid bid!");
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (event.startsWith("bid1"))
			{
				if ((player.getClan() == null) || (player.getClan().getLevel() < 2))
				{
					player.sendMessage("Your clan's level needs to be at least 2, before you can bid in an auction.");
					return null;
				}
				
				if (((player.getClan().getAuctionBiddedAt() > 0) && (player.getClan().getAuctionBiddedAt() != Integer.parseInt(val))) || (player.getClan().hasClanHall()))
				{
					player.sendPacket(SystemMessage.ALREADY_SUBMITTED_BID);
					return null;
				}
				
				try
				{
					int minimumBid = AuctionData.getInstance().getAuction(Integer.parseInt(val)).getHighestBidderMaxBid();
					if (minimumBid == 0)
					{
						minimumBid = AuctionData.getInstance().getAuction(Integer.parseInt(val)).getStartingBid();
					}
					
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBid1.htm");
					html.replace("%AGIT_LINK_BACK%", "bypass -h Quest Auctioneer bidding " + val);
					html.replace("%PLEDGE_ADENA%", String.valueOf(player.getClan().getWarehouse().getAdena()));
					html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(minimumBid + 1));
					html.replace("%AGIT_ID%", val);
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid auction!");
				}
			}
			else if (event.equals("list"))
			{
				StringBuilder items = new StringBuilder();
				
				for (Auction a : AuctionData.getInstance().getAuctions())
				{
					items.append("<tr><td>");
					items.append(ClanHallData.getInstance().getClanHallById(a.getId()).getLocation());
					items.append("</td><td>");
					items.append("<a action=\"bypass -h Quest Auctioneer bidding " + a.getId() + "\">" + a.getClanHallName() + "</a>");
					items.append("</td><td>");
					items.append(a.getEndDate().get(Calendar.YEAR) + "/" + (a.getEndDate().get(Calendar.MONTH) + 1) + "/" + a.getEndDate().get(Calendar.DATE));
					items.append("</td><td>");
					items.append(a.getStartingBid());
					items.append("</td></tr>");
				}
				
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitAuctionList.htm");
				html.replace("%itemsField%", items.toString());
				player.sendPacket(html);
			}
			else if (event.startsWith("bidlist"))
			{
				int auctionId = 0;
				if (val.isEmpty())
				{
					if (player.getClan().getAuctionBiddedAt() <= 0)
					{
						return null;
					}
					auctionId = player.getClan().getAuctionBiddedAt();
				}
				else
				{
					auctionId = Integer.parseInt(val);
				}
				
				StringBuilder biders = new StringBuilder();
				
				for (BidderHolder b : AuctionData.getInstance().getAuction(auctionId).getBidders().values())
				{
					biders.append("<tr><td>");
					biders.append(b.getClanName());
					biders.append("</td><td>");
					biders.append(b.getBidderName());
					biders.append("</td><td>");
					biders.append(b.getTimeBid().get(Calendar.YEAR) + "/" + (b.getTimeBid().get(Calendar.MONTH) + 1) + "/" + b.getTimeBid().get(Calendar.DATE));
					biders.append("</td><td>");
					biders.append(b.getBid());
					biders.append("</td></tr>");
				}
				
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitBidderList.htm");
				html.replace("%AGIT_LIST%", biders.toString());
				html.replace("%x%", val);
				player.sendPacket(html);
			}
			else if (event.equals("selectedItems"))
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
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBidInfo.htm");
					Auction a = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (a != null)
					{
						html.replace("%AGIT_NAME%", a.getClanHallName());
						html.replace("%OWNER_PLEDGE_NAME%", a.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", a.getSellerName());
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallData.getInstance().getClanHallById(a.getId()).getGrade() * 10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallData.getInstance().getClanHallById(a.getId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallData.getInstance().getClanHallById(a.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", String.valueOf(a.getEndDate().get(Calendar.YEAR)));
						html.replace("%AGIT_AUCTION_END_MM%", String.valueOf(a.getEndDate().get(Calendar.MONTH) + 1));
						html.replace("%AGIT_AUCTION_END_DD%", String.valueOf(a.getEndDate().get(Calendar.DAY_OF_MONTH)));
						html.replace("%AGIT_AUCTION_END_HH%", String.valueOf(a.getEndDate().get(Calendar.HOUR_OF_DAY)));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((a.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000) + " hours " + String.valueOf((((a.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60))
							+ " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_MYBID%", String.valueOf(a.getBidders().get(player.getClanId()).getBid()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getInstance().getClanHallById(a.getId()).getDesc());
					}
					else
					{
						LOG.warning("Auctioneer Auction null for AuctionBiddedAt : " + player.getClan().getAuctionBiddedAt());
					}
					
					player.sendPacket(html);
				}
				else if ((player.getClan() != null) && (AuctionData.getInstance().getAuction(player.getClan().getClanHallId()) != null))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitSaleInfo.htm");
					Auction auction = AuctionData.getInstance().getAuction(player.getClan().getClanHallId());
					
					if (auction != null)
					{
						html.replace("%AGIT_NAME%", auction.getClanHallName());
						html.replace("%AGIT_OWNER_PLEDGE_NAME%", auction.getSellerClanName());
						html.replace("%OWNER_PLEDGE_MASTER%", auction.getSellerName());
						html.replace("%AGIT_SIZE%", String.valueOf(ClanHallData.getInstance().getClanHallById(auction.getId()).getGrade() * 10));
						html.replace("%AGIT_LEASE%", String.valueOf(ClanHallData.getInstance().getClanHallById(auction.getId()).getLease()));
						html.replace("%AGIT_LOCATION%", ClanHallData.getInstance().getClanHallById(auction.getId()).getLocation());
						html.replace("%AGIT_AUCTION_END_YY%", String.valueOf(auction.getEndDate().get(Calendar.YEAR)));
						html.replace("%AGIT_AUCTION_END_MM%", String.valueOf(auction.getEndDate().get(Calendar.MONTH) + 1));
						html.replace("%AGIT_AUCTION_END_DD%", String.valueOf(auction.getEndDate().get(Calendar.DAY_OF_MONTH)));
						html.replace("%AGIT_AUCTION_END_HH%", String.valueOf(auction.getEndDate().get(Calendar.HOUR_OF_DAY)));
						html.replace("%AGIT_AUCTION_REMAIN%", String.valueOf((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000) + " hours "
							+ String.valueOf((((auction.getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 60000) % 60)) + " minutes");
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(auction.getStartingBid()));
						html.replace("%AGIT_AUCTION_BIDCOUNT%", String.valueOf(auction.getBidders().size()));
						html.replace("%AGIT_AUCTION_DESC%", ClanHallData.getInstance().getClanHallById(auction.getId()).getDesc());
						html.replace("%id%", String.valueOf(auction.getId()));
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
					
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitInfo.htm");
					html.replace("%AGIT_NAME%", ClanHallData.getInstance().getClanHallById(clanHallId).getName());
					html.replace("%AGIT_OWNER_PLEDGE_NAME%", player.getClan().getName());
					html.replace("%OWNER_PLEDGE_MASTER%", player.getClan().getLeaderName());
					html.replace("%AGIT_SIZE%", String.valueOf(ClanHallData.getInstance().getClanHallById(clanHallId).getGrade() * 10));
					html.replace("%AGIT_LEASE%", String.valueOf(ClanHallData.getInstance().getClanHallById(clanHallId).getLease()));
					html.replace("%AGIT_LOCATION%", ClanHallData.getInstance().getClanHallById(clanHallId).getLocation());
					player.sendPacket(html);
				}
			}
			else if (event.equalsIgnoreCase("cancelBid"))
			{
				int bid = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).getBidders().get(player.getClanId()).getBid();
				
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitBidCancel.htm");
				html.replace("%AGIT_BID%", String.valueOf(bid));
				html.replace("%AGIT_BID_REMAIN%", String.valueOf((bid * 0.9)));
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
			else if (event.equalsIgnoreCase("cancelAuction"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSaleCancel.htm");
				html.replace("%AGIT_DEPOSIT%", String.valueOf(ClanHallData.getInstance().getClanHallByOwner(player.getClan()).getLease()));
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
			else if (event.equalsIgnoreCase("sale2"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSale2.htm");
				html.replace("%AGIT_LAST_PRICE%", String.valueOf(ClanHallData.getInstance().getClanHallByOwner(player.getClan()).getLease()));
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("sale"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "AgitSale1.htm");
				html.replace("%AGIT_DEPOSIT%", String.valueOf(ClanHallData.getInstance().getClanHallByOwner(player.getClan()).getLease()));
				html.replace("%AGIT_PLEDGE_ADENA%", String.valueOf(player.getClan().getWarehouse().getAdena()));
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("rebid"))
			{
				if (!player.isClanLeader())
				{
					player.sendMessage("Only the clan leader has the privilege to do this.");
					return null;
				}
				try
				{
					NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setFile(HTML_PATH + "AgitBid2.htm");
					Auction a = AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt());
					if (a != null)
					{
						html.replace("%AGIT_AUCTION_MINBID%", String.valueOf(a.getStartingBid()));
						html.replace("%AGIT_AUCTION_END_YY%", String.valueOf(a.getEndDate().get(Calendar.YEAR)));
						html.replace("%AGIT_AUCTION_END_MM%", String.valueOf(a.getEndDate().get(Calendar.MONTH) + 1));
						html.replace("%AGIT_AUCTION_END_DD%", String.valueOf(a.getEndDate().get(Calendar.DAY_OF_MONTH)));
						html.replace("%AGIT_AUCTION_END_HH%", String.valueOf(a.getEndDate().get(Calendar.HOUR_OF_DAY)));
						html.replace("npc_%objectId%_bid1", "Quest Auctionner bid1 " + a.getId());
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
			else if (event.equals("location"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(HTML_PATH + "location.htm");
				html.replace("%location%", MapRegionData.getInstance().getClosestTownName(player));
				html.replace("%LOCATION%", getPictureName(player));
				player.sendPacket(html);
			}
			else if (event.equalsIgnoreCase("start"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
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
