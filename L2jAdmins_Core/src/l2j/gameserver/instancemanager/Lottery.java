package l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

public class Lottery
{
	protected static final Logger LOG = Logger.getLogger(Lottery.class.getName());
	
	protected static final long SECOND = 1000;
	protected static final long MINUTE = 60000;
	
	private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
	private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
	private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
	private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
	private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 and idnr = ?";
	
	protected int number;
	protected int prize;
	protected boolean isSellingTickets;
	protected boolean isStarted;
	protected long enddate;
	
	public Lottery()
	{
		number = 1;
		prize = Config.ALT_LOTTERY_PRIZE;
		isSellingTickets = false;
		isStarted = false;
		enddate = System.currentTimeMillis();
		
		if (Config.ALLOW_LOTTERY)
		{
			(new startLottery()).run();
		}
	}
	
	public int getId()
	{
		return number;
	}
	
	public int getPrize()
	{
		return prize;
	}
	
	public long getEndDate()
	{
		return enddate;
	}
	
	public void increasePrize(int count)
	{
		prize += count;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_PRICE))
		{
			ps.setInt(1, getPrize());
			ps.setInt(2, getPrize());
			ps.setInt(3, getId());
			ps.execute();
		}
		catch (SQLException e)
		{
			LOG.warning("Lottery: Could not increase current lottery prize: " + e);
		}
	}
	
	public boolean isSellableTickets()
	{
		return isSellingTickets;
	}
	
	public boolean isStarted()
	{
		return isStarted;
	}
	
	private class startLottery implements Runnable
	{
		protected startLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT_LAST_LOTTERY);
				ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					number = rset.getInt("idnr");
					
					if (rset.getInt("finished") == 1)
					{
						number++;
						prize = rset.getInt("newprize");
					}
					else
					{
						prize = rset.getInt("prize");
						enddate = rset.getLong("enddate");
						
						if (enddate <= (System.currentTimeMillis() + (2 * MINUTE)))
						{
							(new finishLottery()).run();
							return;
						}
						
						if (enddate > System.currentTimeMillis())
						{
							isStarted = true;
							ThreadPoolManager.getInstance().schedule(new finishLottery(), enddate - System.currentTimeMillis());
							
							if (enddate > (System.currentTimeMillis() + (12 * MINUTE)))
							{
								isSellingTickets = true;
								ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), enddate - System.currentTimeMillis() - (10 * MINUTE));
							}
							return;
						}
					}
				}
			}
			catch (SQLException e)
			{
				LOG.warning("Lottery: Could not restore lottery data: " + e);
			}
			
			if (Config.DEBUG)
			{
				LOG.info("Lottery: Starting ticket sell for lottery #" + getId() + ".");
			}
			isSellingTickets = true;
			isStarted = true;
			
			AnnouncementsData.getInstance().announceToAll("Lottery tickets are now available for Lucky Lottery #" + getId() + ".");
			Calendar finishtime = Calendar.getInstance();
			finishtime.setTimeInMillis(enddate);
			finishtime.set(Calendar.MINUTE, 0);
			finishtime.set(Calendar.SECOND, 0);
			
			if (finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				enddate = finishtime.getTimeInMillis();
				enddate += 604800000;
			}
			else
			{
				finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				enddate = finishtime.getTimeInMillis();
			}
			
			ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), enddate - System.currentTimeMillis() - (10 * MINUTE));
			ThreadPoolManager.getInstance().schedule(new finishLottery(), enddate - System.currentTimeMillis());
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_LOTTERY))
			{
				ps.setInt(1, 1);
				ps.setInt(2, getId());
				ps.setLong(3, getEndDate());
				ps.setInt(4, getPrize());
				ps.setInt(5, getPrize());
				ps.execute();
			}
			catch (SQLException e)
			{
				LOG.warning("Lottery: Could not store new lottery data: " + e);
			}
		}
	}
	
	private class stopSellingTickets implements Runnable
	{
		protected stopSellingTickets()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			if (Config.DEBUG)
			{
				LOG.info("Lottery: Stopping ticket sell for lottery #" + getId() + ".");
			}
			isSellingTickets = false;
			
			AnnouncementsData.getInstance().announceToAll(new SystemMessage(SystemMessage.LOTTERY_TICKET_SALES_TEMP_SUSPENDED));
		}
	}
	
	private class finishLottery implements Runnable
	{
		protected finishLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			if (Config.DEBUG)
			{
				LOG.info("Lottery: Ending lottery #" + getId() + ".");
			}
			
			int[] luckynums = new int[5];
			int luckynum = 0;
			
			for (int i = 0; i < 5; i++)
			{
				boolean found = true;
				
				while (found)
				{
					luckynum = Rnd.get(20) + 1;
					found = false;
					
					for (int j = 0; j < i; j++)
					{
						if (luckynums[j] == luckynum)
						{
							found = true;
						}
					}
				}
				
				luckynums[i] = luckynum;
			}
			
			if (Config.DEBUG)
			{
				LOG.info("Lottery: The lucky numbers are " + luckynums[0] + ", " + luckynums[1] + ", " + luckynums[2] + ", " + luckynums[3] + ", " + luckynums[4] + ".");
			}
			
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (luckynums[i] < 17)
				{
					enchant += Math.pow(2, luckynums[i] - 1);
				}
				else
				{
					type2 += Math.pow(2, luckynums[i] - 17);
				}
			}
			
			if (Config.DEBUG)
			{
				LOG.info("Lottery: Encoded lucky numbers are " + enchant + ", " + type2);
			}
			
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT_LOTTERY_ITEM))
			{
				ps.setInt(1, getId());
				try (ResultSet rset = ps.executeQuery())
				{
					while (rset.next())
					{
						int curenchant = rset.getInt("enchant_level") & enchant;
						int curtype2 = rset.getInt("custom_type2") & type2;
						
						if ((curenchant == 0) && (curtype2 == 0))
						{
							continue;
						}
						
						int count = 0;
						
						for (int i = 1; i <= 16; i++)
						{
							int val = curenchant / 2;
							
							if (val != ((double) curenchant / 2))
							{
								count++;
							}
							
							int val2 = curtype2 / 2;
							
							if (val2 != ((double) curtype2 / 2))
							{
								count++;
							}
							
							curenchant = val;
							curtype2 = val2;
						}
						
						if (count == 5)
						{
							count1++;
						}
						else if (count == 4)
						{
							count2++;
						}
						else if (count == 3)
						{
							count3++;
						}
						else if (count > 0)
						{
							count4++;
						}
					}
				}
			}
			catch (SQLException e)
			{
				LOG.warning("Lottery: Could restore lottery data: " + e);
			}
			
			int prize4 = count4 * Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
			int prize1 = 0;
			int prize2 = 0;
			int prize3 = 0;
			
			if (count1 > 0)
			{
				prize1 = (int) (((getPrize() - prize4) * Config.ALT_LOTTERY_5_NUMBER_RATE) / count1);
			}
			
			if (count2 > 0)
			{
				prize2 = (int) (((getPrize() - prize4) * Config.ALT_LOTTERY_4_NUMBER_RATE) / count2);
			}
			
			if (count3 > 0)
			{
				prize3 = (int) (((getPrize() - prize4) * Config.ALT_LOTTERY_3_NUMBER_RATE) / count3);
			}
			
			if (Config.DEBUG)
			{
				LOG.info("Lottery: " + count1 + " players with all FIVE numbers each win " + prize1 + ".");
				LOG.info("Lottery: " + count2 + " players with FOUR numbers each win " + prize2 + ".");
				LOG.info("Lottery: " + count3 + " players with THREE numbers each win " + prize3 + ".");
				LOG.info("Lottery: " + count4 + " players with ONE or TWO numbers each win " + prize4 + ".");
			}
			
			int newprize = getPrize() - (prize1 + prize2 + prize3 + prize4);
			if (Config.DEBUG)
			{
				LOG.info("Lottery: Jackpot for next lottery is " + newprize + ".");
			}
			
			if (count1 > 0)
			{
				// There are winners.
				AnnouncementsData.getInstance().announceToAll(new SystemMessage(SystemMessage.AMOUNT_FOR_WINNER_S1_IS_S2_ADENA_WE_HAVE_S3_PRIZE_WINNER).addNumber(getId()).addNumber(getPrize()).addNumber(count1));
			}
			else
			{
				// There are no winners.
				AnnouncementsData.getInstance().announceToAll(new SystemMessage(SystemMessage.AMOUNT_FOR_LOTTERY_S1_IS_S2_ADENA_NO_WINNER).addNumber(getId()).addNumber(getPrize()));
			}
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_LOTTERY))
			{
				ps.setInt(1, getPrize());
				ps.setInt(2, newprize);
				ps.setInt(3, enchant);
				ps.setInt(4, type2);
				ps.setInt(5, prize1);
				ps.setInt(6, prize2);
				ps.setInt(7, prize3);
				ps.setInt(8, getId());
				ps.execute();
			}
			catch (SQLException e)
			{
				LOG.warning("Lottery: Could not store finished lottery data: " + e);
			}
			
			ThreadPoolManager.getInstance().schedule(new startLottery(), MINUTE);
			number++;
			
			isStarted = false;
		}
	}
	
	public int[] decodeNumbers(int enchant, int type2)
	{
		int res[] = new int[5];
		int id = 0;
		int nr = 1;
		
		while (enchant > 0)
		{
			int val = enchant / 2;
			if (val != ((double) enchant / 2))
			{
				res[id] = nr;
				id++;
			}
			enchant /= 2;
			nr++;
		}
		
		nr = 17;
		
		while (type2 > 0)
		{
			int val = type2 / 2;
			if (val != ((double) type2 / 2))
			{
				res[id] = nr;
				id++;
			}
			type2 /= 2;
			nr++;
		}
		
		return res;
	}
	
	public int[] checkTicket(ItemInstance item)
	{
		return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
	}
	
	public int[] checkTicket(int id, int enchant, int type2)
	{
		int res[] =
		{
			0,
			0
		};
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_LOTTERY_TICKET))
		{
			ps.setInt(1, id);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					int curenchant = rset.getInt("number1") & enchant;
					int curtype2 = rset.getInt("number2") & type2;
					
					if ((curenchant == 0) && (curtype2 == 0))
					{
						return res;
					}
					
					int count = 0;
					
					for (int i = 1; i <= 16; i++)
					{
						int val = curenchant / 2;
						if (val != ((double) curenchant / 2))
						{
							count++;
						}
						int val2 = curtype2 / 2;
						if (val2 != ((double) curtype2 / 2))
						{
							count++;
						}
						curenchant = val;
						curtype2 = val2;
					}
					
					switch (count)
					{
						case 0:
							break;
						case 5:
							res[0] = 1;
							res[1] = rset.getInt("prize1");
							break;
						case 4:
							res[0] = 2;
							res[1] = rset.getInt("prize2");
							break;
						case 3:
							res[0] = 3;
							res[1] = rset.getInt("prize3");
							break;
						default:
							res[0] = 4;
							res[1] = 200;
					}
					
					if (Config.DEBUG)
					{
						LOG.warning("count: " + count + ", id: " + id + ", enchant: " + enchant + ", type2: " + type2);
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warning("Lottery: Could not check lottery ticket #" + id + ": " + e);
		}
		
		return res;
	}
	
	public static Lottery getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public static class SingletonHolder
	{
		protected static final Lottery INSTANCE = new Lottery();
	}
}
