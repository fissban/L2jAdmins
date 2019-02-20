package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.5.4.13 $ $Date: 2005/04/06 16:13:38 $
 */
public class TradeControllerData
{
	private static final Logger LOG = Logger.getLogger(TradeControllerData.class.getName());
	
	private int nextListId;
	private final Map<Integer, MerchantTradeList> lists = new HashMap<>();
	
	public void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement statement1 = con.prepareStatement("SELECT shop_id, npc_id FROM merchant_shopids");
			ResultSet rset1 = statement1.executeQuery())
		{
			int itemId, price, count, currentCount, time;
			long saveTime;
			while (rset1.next())
			{
				try (PreparedStatement ps = con.prepareStatement("SELECT item_id, price, shop_id, 'order', count, currentCount, time, savetimer FROM merchant_buylists WHERE shop_id=? ORDER BY 'order' ASC"))
				{
					ps.setString(1, String.valueOf(rset1.getInt("shop_id")));
					try (ResultSet rs = ps.executeQuery())
					{
						MerchantTradeList buy1 = new MerchantTradeList(rset1.getInt("shop_id"));
						
						while (rs.next())
						{
							itemId = rs.getInt("item_id");
							price = rs.getInt("price");
							count = rs.getInt("count");
							currentCount = rs.getInt("currentCount");
							time = rs.getInt("time");
							saveTime = rs.getLong("savetimer");
							
							ItemInstance item = ItemData.getInstance().createDummyItem(itemId);
							if (item == null)
							{
								continue;
							}
							
							if (count > -1)
							{
								item.setCountDecrease(true);
							}
							
							item.setPriceToSell(price);
							item.setInitCount(count);
							
							if (currentCount > -1)
							{
								item.setCount(currentCount);
							}
							else
							{
								item.setCount(count);
							}
							
							item.setTime(time);
							if (item.getTime() > 0)
							{
								item.setRestoreTime(saveTime);
							}
							
							buy1.addItem(item);
							buy1.setNpcId(rset1.getString("npc_id"));
							
							lists.put(buy1.getListId(), buy1);
							nextListId = Math.max(nextListId, buy1.getListId() + 1);
						}
					}
				}
			}
			
		}
		catch (Exception e)
		{
			LOG.warning("TradeController: Buylists could not be initialized.");
			e.printStackTrace();
		}
		
		UtilPrint.result("TradeControllerData", "Loaded buylist", lists.size());
		
		/*
		 * If enabled, initialize the custom buylist
		 */
		if (Config.CUSTOM_MERCHANT_TABLES)
		{
			try (java.sql.Connection con = DatabaseManager.getConnection();
				PreparedStatement statement1 = con.prepareStatement("SELECT shop_id, npc_id  FROM custom_merchant_shopids");
				ResultSet rset1 = statement1.executeQuery())
			{
				int initialSize = lists.size();
				
				int itemId, price, count, currentCount, time;
				long saveTime;
				while (rset1.next())
				{
					try (PreparedStatement ps = con.prepareStatement("SELECT item_id, price, shop_id, 'order', count, currentCount, time, savetimer FROM custom_merchant_buylists WHERE shop_id=? ORDER BY 'order' ASC"))
					{
						ps.setString(1, String.valueOf(rset1.getInt("shop_id")));
						
						try (ResultSet rs = ps.executeQuery())
						{
							MerchantTradeList buy1 = new MerchantTradeList(rset1.getInt("shop_id"));
							
							while (rs.next())
							{
								itemId = rs.getInt("item_id");
								price = rs.getInt("price");
								count = rs.getInt("count");
								currentCount = rs.getInt("currentCount");
								time = rs.getInt("time");
								saveTime = rs.getLong("savetimer");
								
								ItemInstance item = ItemData.getInstance().createDummyItem(itemId);
								if (item == null)
								{
									continue;
								}
								
								if (count > -1)
								{
									item.setCountDecrease(true);
								}
								
								item.setPriceToSell(price);
								item.setInitCount(count);
								
								if (currentCount > -1)
								{
									item.setCount(currentCount);
								}
								else
								{
									item.setCount(count);
								}
								
								item.setTime(time);
								if (item.getTime() > 0)
								{
									item.setRestoreTime(saveTime);
								}
								
								buy1.addItem(item);
								buy1.setNpcId(rset1.getString("npc_id"));
								
								lists.put(buy1.getListId(), buy1);
								nextListId = Math.max(nextListId, buy1.getListId() + 1);
							}
						}
					}
					
				}
				
				UtilPrint.result("TradeControllerData", "Loaded custom buylist", (lists.size() - initialSize));
			}
			catch (Exception e)
			{
				LOG.warning("TradeController: Custom Buylists could not be initialized.");
				e.printStackTrace();
			}
		}
	}
	
	public MerchantTradeList getBuyList(int listId)
	{
		return lists.get(listId);
	}
	
	public List<MerchantTradeList> getBuyListByNpcId(int npcId)
	{
		List<MerchantTradeList> aux = new ArrayList<>();
		
		for (MerchantTradeList list : lists.values())
		{
			if (list.getNpcId().startsWith("gm"))
			{
				continue;
			}
			if (npcId == Integer.parseInt(list.getNpcId()))
			{
				aux.add(list);
			}
		}
		return aux;
	}
	
	public void dataCountStore()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE merchant_buylists SET currentCount =? WHERE item_id =? && shop_id = ?"))
		{
			for (MerchantTradeList list : lists.values())
			{
				for (ItemInstance Item : list.getItems())
				{
					if (Item.getCount() < Item.getInitCount())
					{
						ps.setInt(1, Item.getCount());
						ps.setInt(2, Item.getId());
						ps.setInt(3, list.getListId());
						ps.executeUpdate();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "TradeController: Could not store Count Item");
		}
	}
	
	public synchronized int getNextId()
	{
		return nextListId++;
	}
	
	public static TradeControllerData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TradeControllerData INSTANCE = new TradeControllerData();
	}
}
