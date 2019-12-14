package l2j.gameserver.model.entity.clanhalls.auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.AuctionData;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.holder.BidderHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;

public class Auction
{
	private static final Logger LOG = Logger.getLogger(Auction.class.getName());
	
	private int id = 0;
	private Calendar endDate;
	private int highestBidderId = 0;
	private String highestBidderName = "";
	private int highestBidderMaxBid = 0;
	private String clanHallName = "";
	private int sellerId = 0;
	private String sellerClanName = "";
	private String sellerName = "";
	private int currentBid = 0;
	private int startingBid = 0;
	private final Map<Integer, BidderHolder> bidders = new HashMap<>();
	
	/*
	 * TODO: Announce to losing bidder that they have been out bidded Take adena when bidding Return adena when out bid Give item when auction end UpdateBidInDb Schedule Auction end Remove auction from auction and auction_bid table when auction end
	 */
	
	public Auction(int auctionId)
	{
		id = auctionId;
		load();
		
		// end auction automatically
		startAutoTask(false);
	}
	
	public Auction(int clanHallId, Clan clan, long delay, int bid, String name)
	{
		id = clanHallId;
		endDate = Calendar.getInstance();
		endDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + delay);
		endDate.set(Calendar.MINUTE, 0);
		clanHallName = name;
		
		sellerId = clan.getLeaderId();
		sellerName = clan.getLeaderName();
		sellerClanName = clan.getName();
		startingBid = bid;
	}
	
	public final int getId()
	{
		return id;
	}
	
	public final int getCurrentBid()
	{
		return currentBid;
	}
	
	public final Calendar getEndDate()
	{
		return endDate;
	}
	
	public final int getHighestBidderId()
	{
		return highestBidderId;
	}
	
	public final String getHighestBidderName()
	{
		return highestBidderName;
	}
	
	public final int getHighestBidderMaxBid()
	{
		return highestBidderMaxBid;
	}
	
	public final String getClanHallName()
	{
		return clanHallName;
	}
	
	public final int getSellerId()
	{
		return sellerId;
	}
	
	public final String getSellerName()
	{
		return sellerName;
	}
	
	public final String getSellerClanName()
	{
		return sellerClanName;
	}
	
	public final int getStartingBid()
	{
		return startingBid;
	}
	
	public final Map<Integer, BidderHolder> getBidders()
	{
		return bidders;
	}
	
	private class AutoEndTask implements Runnable
	{
		public AutoEndTask()
		{
			// any actions
		}
		
		@Override
		public void run()
		{
			try
			{
				long timeRemaining = getEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
				if (timeRemaining > 0)
				{
					ThreadPoolManager.schedule(new AutoEndTask(), timeRemaining);
				}
				else
				{
					endAuction();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void startAutoTask(boolean forced)
	{
		correctAuctionTime(forced);
		ThreadPoolManager.schedule(new AutoEndTask(), 1000);
	}
	
	private void correctAuctionTime(boolean forced)
	{
		boolean corrected = false;
		
		if ((endDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) || forced)
		{
			// Since auction has past reschedule it to the next one (7 days)
			// This is usually caused by server being down
			corrected = true;
			if (forced)
			{
				setNextAuctionDate();
			}
			else
			{
				endAuction(); // end auction normally in case it had bidders and server was down when it ended
			}
		}
		
		endDate.set(Calendar.MINUTE, 0);
		
		if (corrected)
		{
			saveAuctionDate();
		}
	}
	
	private void setNextAuctionDate()
	{
		while (endDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Set next auction date if auction has passed
			endDate.add(Calendar.DAY_OF_MONTH, 7); // Schedule to happen in 7 days
		}
	}
	
	private void saveAuctionDate()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE auction set endDate = ? WHERE id = ?"))
		{
			ps.setLong(1, endDate.getTimeInMillis());
			ps.setInt(2, id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: saveAuctionDate(): " + e.getMessage(), e);
		}
	}
	
	public synchronized void setBid(L2PcInstance bidder, int bid)
	{
		// Update bid if new bid is higher
		int requiredAdena = bid;
		if (getHighestBidderName().equals(bidder.getClan().getLeaderName()))
		{
			requiredAdena = bid - getHighestBidderMaxBid();
		}
		if (((getHighestBidderId() > 0) && (bid > getHighestBidderMaxBid())) || ((getHighestBidderId() == 0) && (bid >= getStartingBid())))
		{
			if (takeItem(bidder, requiredAdena))
			{
				updateInDB(bidder, bid);
				bidder.getClan().setAuctionBiddedAt(id, true);
				return;
			}
		}
		
		// Your bid price must be higher than the minimum price that can be bid.
		bidder.sendPacket(SystemMessage.BID_PRICE_MUST_BE_HIGHER);
	}
	
	private void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM auction WHERE id = ?"))
		{
			ps.setInt(1, getId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					currentBid = rs.getInt("currentBid");
					endDate = Calendar.getInstance();
					endDate.setTimeInMillis(rs.getLong("endDate"));
					
					clanHallName = rs.getString("itemName");
					sellerId = rs.getInt("sellerId");
					sellerClanName = rs.getString("sellerClanName");
					sellerName = rs.getString("sellerName");
					startingBid = rs.getInt("startingBid");
				}
			}
			
			loadBid();
		}
		catch (Exception e)
		{
			LOG.warning("Exception: Auction.load(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void loadBid()
	{
		highestBidderId = 0;
		highestBidderName = "";
		highestBidderMaxBid = 0;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT bidderId,bidderName,maxBid,clan_name,time_bid FROM auction_bid WHERE auctionId = ? ORDER BY maxBid DESC"))
		{
			ps.setInt(1, getId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					if (rs.isFirst())
					{
						highestBidderId = rs.getInt("bidderId");
						highestBidderName = rs.getString("bidderName");
						highestBidderMaxBid = rs.getInt("maxBid");
					}
					bidders.put(rs.getInt("bidderId"), new BidderHolder(rs.getString("bidderName"), rs.getString("clan_name"), rs.getInt("maxBid"), rs.getLong("time_bid")));
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Exception: Auction.loadBid(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void returnItem(String Clan, int quantity, boolean penalty)
	{
		if (penalty)
		{
			quantity *= 0.9; // take 10% tax fee if needed
		}
		ClanData.getInstance().getClanByName(Clan).getWarehouse().addItem("Outbidded", Inventory.ADENA_ID, quantity, null, null);
	}
	
	private static boolean takeItem(L2PcInstance bidder, int quantity)
	{
		// Take item from clan warehouse
		if ((bidder.getClan() != null) && (bidder.getClan().getWarehouse().getAdena() >= quantity))
		{
			bidder.getClan().getWarehouse().destroyItemByItemId("Buy", Inventory.ADENA_ID, quantity, bidder, bidder);
			return true;
		}
		
		bidder.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
		return false;
	}
	
	private void updateInDB(L2PcInstance bidder, int bid)
	{
		// Check and remove amount being bid
		// if (!takeItem(bidder, Inventory.ADENA_ID, bid)) return;
		
		try (Connection con = DatabaseManager.getConnection())
		{
			if (getBidders().get(bidder.getClanId()) != null)
			{
				try (PreparedStatement ps = con.prepareStatement("UPDATE auction_bid SET bidderId=?, bidderName=?, maxBid=?, time_bid=? WHERE auctionId=? AND bidderId=?"))
				{
					ps.setInt(1, bidder.getClanId());
					ps.setString(2, bidder.getClan().getLeaderName());
					ps.setInt(3, bid);
					ps.setLong(4, Calendar.getInstance().getTimeInMillis());
					ps.setInt(5, getId());
					ps.setInt(6, bidder.getClanId());
					ps.execute();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement("INSERT INTO auction_bid (id, auctionId, bidderId, bidderName, maxBid, clan_name, time_bid) VALUES (?, ?, ?, ?, ?, ?, ?)"))
				{
					ps.setInt(1, IdFactory.getInstance().getNextId());
					ps.setInt(2, getId());
					ps.setInt(3, bidder.getClanId());
					ps.setString(4, bidder.getName());
					ps.setInt(5, bid);
					ps.setString(6, bidder.getClan().getName());
					ps.setLong(7, Calendar.getInstance().getTimeInMillis());
					ps.execute();
				}
				
				L2PcInstance player = L2World.getInstance().getPlayer(CharNameData.getInstance().getIdByName(highestBidderName));
				if (player != null)
				{
					player.sendMessage("You have been outbidded.");
				}
			}
			
			// Announce to losing bidder that they have been out bidded
			
			// Update internal var
			highestBidderId = bidder.getClanId();
			highestBidderMaxBid = bid;
			highestBidderName = bidder.getClan().getLeaderName();
			
			BidderHolder bh = bidders.get(highestBidderId);
			
			if (bh == null)
			{
				bidders.put(highestBidderId, new BidderHolder(highestBidderName, bidder.getClan().getName(), bid, Calendar.getInstance().getTimeInMillis()));
			}
			else
			{
				bh.setBid(bid);
				bh.setTimeBid(Calendar.getInstance().getTimeInMillis());
			}
			// You have bid in a clan hall auction
			bidder.sendPacket(SystemMessage.BID_IN_CLANHALL_AUCTION);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: Auction.updateInDB(L2PcInstance bidder, int bid): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void removeBids()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=?"))
		{
			ps.setInt(1, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: Auction.deleteFromDB(): " + e.getMessage(), e);
		}
		
		for (BidderHolder bh : bidders.values())
		{
			if (!ClanData.getInstance().getClanByName(bh.getClanName()).hasClanHall())
			{
				returnItem(bh.getClanName(), bh.getBid(), true); // 10 % tax
			}
			else
			{
				L2PcInstance bidder = L2World.getInstance().getPlayer(CharNameData.getInstance().getIdByName(bh.getBidderName()));
				if (bidder != null)
				{
					bidder.sendPacket(SystemMessage.CLANHALL_AWARDED_TO_CLAN);
				}
			}
			ClanData.getInstance().getClanByName(bh.getClanName()).setAuctionBiddedAt(0, true);
		}
		bidders.clear();
	}
	
	public void deleteAuctionFromDB()
	{
		AuctionData.getInstance().getAuctions().remove(this);
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM auction WHERE id=?"))
		{
			ps.setInt(1, id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: Auction.deleteFromDB(): " + e.getMessage(), e);
		}
	}
	
	public void endAuction()
	{
		if (highestBidderId == 0)
		{
			if ((sellerId == 0))
			{
				startAutoTask(true);
			}
			else if (sellerId > 0)
			{
				deleteAuctionFromDB();
			}
			
			return;
		}
		if (sellerId > 0)
		{
			returnItem(sellerClanName, highestBidderMaxBid, true);
			returnItem(sellerClanName, ClanHallData.getClanHallById(id).getLease(), false);
		}
		ClanHallData.getClanHallById(id).setOwner(ClanData.getInstance().getClanByName(bidders.get(highestBidderId).getClanName()));
		cancelAuction();
	}
	
	public synchronized void cancelBid(int bidder)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM auction_bid WHERE auctionId=? AND bidderId=?"))
		{
			ps.setInt(1, getId());
			ps.setInt(2, bidder);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: Auction.cancelBid(String bidder): " + e.getMessage(), e);
		}
		
		returnItem(bidders.get(bidder).getClanName(), bidders.get(bidder).getBid(), true);
		ClanData.getInstance().getClanByName(bidders.get(bidder).getClanName()).setAuctionBiddedAt(0, true);
		bidders.remove(bidder);
	}
	
	public void cancelAuction()
	{
		removeBids();
		deleteAuctionFromDB();
	}
	
	public void confirmAuction()
	{
		AuctionData.getInstance().getAuctions().add(this);
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO auction (id, sellerId, sellerName, sellerClanName, itemName, startingBid, currentBid, endDate) VALUES (?,?,?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, getId());
			ps.setInt(2, sellerId);
			ps.setString(3, sellerName);
			ps.setString(4, sellerClanName);
			ps.setString(5, clanHallName);
			ps.setInt(6, startingBid);
			ps.setInt(7, currentBid);
			ps.setLong(8, endDate.getTimeInMillis());
			ps.execute();
			
			loadBid();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: Auction.load(): " + e.getMessage(), e);
		}
	}
}
