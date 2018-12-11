package l2j.gameserver.model.holder;

import java.util.Calendar;

/**
 * @author fissban
 */
public class BidderHolder
{
	private final String bidderName;
	private final String clanName;
	private int bid;
	private final Calendar timeBid;
	
	public BidderHolder(String name, String clanName, int bid, long timeBid)
	{
		bidderName = name;
		this.clanName = clanName;
		this.bid = bid;
		this.timeBid = Calendar.getInstance();
		this.timeBid.setTimeInMillis(timeBid);
	}
	
	public String getBidderName()
	{
		return bidderName;
	}
	
	public String getClanName()
	{
		return clanName;
	}
	
	public int getBid()
	{
		return bid;
	}
	
	public Calendar getTimeBid()
	{
		return timeBid;
	}
	
	public void setTimeBid(long timeBid)
	{
		this.timeBid.setTimeInMillis(timeBid);
	}
	
	public void setBid(int bid)
	{
		this.bid = bid;
	}
}
