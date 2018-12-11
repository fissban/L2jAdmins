package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ManufactureItemHolder;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.privatestore.PcStoreType;
import l2j.gameserver.model.privatestore.PrivateStoreList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.util.UtilPrint;

public class OfflineTradersData
{
	private static final Logger LOG = Logger.getLogger(OfflineTradersData.class.getName());
	
	// SQL DEFINITIONS
	private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (`char_id`,`time`,`type`,`title`) VALUES (?,?,?,?)";
	private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (`char_id`,`item`,`count`,`price`) VALUES (?,?,?,?)";
	private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
	private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
	private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE char_id = ?";
	
	public static void storeOffliners()
	{
		var count = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			clearAllTables(con);
			
			con.setAutoCommit(false); // avoid halfway done
			
			try (PreparedStatement stm = con.prepareStatement(SAVE_OFFLINE_STATUS))
			{
				try (PreparedStatement stmItems = con.prepareStatement(SAVE_ITEMS))
				{
					for (L2PcInstance pc : L2World.getInstance().getAllPlayers())
					{
						try
						{
							if (pc.getPrivateStore().inOfflineMode())
							{
								stm.setInt(1, pc.getObjectId()); // Char Id
								stm.setLong(2, pc.getPrivateStore().getOfflineStartTime());
								stm.setInt(3, pc.getPrivateStore().getStoreType().getValue()); // store type
								String title = null;
								
								switch (pc.getPrivateStore().getStoreType())
								{
									case BUY:
										if (!Config.OFFLINE_TRADE_ENABLE)
										{
											continue;
										}
										
										title = pc.getPrivateStore().getBuyList().getTitle();
										for (TradeItemHolder i : pc.getPrivateStore().getBuyList().getItems())
										{
											stmItems.setInt(1, pc.getObjectId());
											stmItems.setInt(2, i.getItem().getId());
											stmItems.setInt(3, i.getCount());
											stmItems.setInt(4, i.getPrice());
											stmItems.executeUpdate();
											stmItems.clearParameters();
										}
										break;
									case SELL:
									case PACKAGE_SELL:
										if (!Config.OFFLINE_TRADE_ENABLE)
										{
											continue;
										}
										title = pc.getPrivateStore().getSellList().getTitle();
										for (TradeItemHolder i : pc.getPrivateStore().getSellList().getItems())
										{
											stmItems.setInt(1, pc.getObjectId());
											stmItems.setInt(2, i.getObjectId());
											stmItems.setInt(3, i.getCount());
											stmItems.setInt(4, i.getPrice());
											stmItems.executeUpdate();
											stmItems.clearParameters();
										}
										break;
									case MANUFACTURE:
										if (!Config.OFFLINE_CRAFT_ENABLE)
										{
											continue;
										}
										title = pc.getPrivateStore().getCreateList().getStoreName();
										for (ManufactureItemHolder i : pc.getPrivateStore().getCreateList().getList())
										{
											stmItems.setInt(1, pc.getObjectId());
											stmItems.setInt(2, i.getRecipeId());
											stmItems.setInt(3, 0);
											stmItems.setInt(4, i.getCost());
											stmItems.executeUpdate();
											stmItems.clearParameters();
										}
								}
								
								stm.setString(4, title);
								stm.executeUpdate();
								stm.clearParameters();
								con.commit(); // flush
							}
						}
						catch (Exception e)
						{
							LOG.log(Level.WARNING, "OfflineTradersTable[storeTradeItems()]: Error while saving offline trader: " + pc.getObjectId() + " " + e, e);
						}
						
						count++;
					}
				}
			}
			
			LOG.info("All Offline Traders saved!!");
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "OfflineTradersTable[storeTradeItems()]: Error while saving offline traders: " + e, e);
			e.printStackTrace();
		}
		
		UtilPrint.result("OfflineTradersData", "Loaded offline traders", count);
	}
	
	public static void restoreOfflineTraders()
	{
		LOG.info("Loading offline traders...");
		
		int nTraders = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(LOAD_OFFLINE_STATUS);
			ResultSet rs = stm.executeQuery())
		{
			while (rs.next())
			{
				long time = rs.getLong("time");
				if (Config.OFFLINE_MAX_DAYS > 0)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					cal.add(Calendar.DAY_OF_YEAR, Config.OFFLINE_MAX_DAYS);
					if (cal.getTimeInMillis() <= System.currentTimeMillis())
					{
						continue;
					}
				}
				
				PcStoreType type = null;
				for (PcStoreType st : PcStoreType.values())
				{
					if (st.getValue() == rs.getInt("type"))
					{
						type = st;
						break;
					}
				}
				
				if (type == PcStoreType.NONE)
				{
					continue;
				}
				
				L2PcInstance player = null;
				
				try
				{
					// Socket() creates an unconnected socket which is perfect for our purpose
					GameClient client = new GameClient(null);
					player = L2PcInstance.load(rs.getInt("char_id"));
					client.setActiveChar(player);
					client.setAccountName(player.getAccountName());
					client.setState(GameClientState.IN_GAME);
					client.setDetached(true);
					player.setConnected(false);
					player.setClient(client);
					player.spawnMe(player.getX(), player.getY(), player.getZ());
					LoginServerThread.getInstance().addClient(player.getAccountName(), client);
					
					try (PreparedStatement stm_items = con.prepareStatement(LOAD_OFFLINE_ITEMS))
					{
						stm_items.setInt(1, player.getObjectId());
						
						try (ResultSet items = stm_items.executeQuery())
						{
							switch (type)
							{
								case BUY:
									while (items.next())
									{
										if (player.getPrivateStore().getBuyList().addItemByItemId(items.getInt(2), items.getInt(3), items.getInt(4)) == null)
										{
											throw new NullPointerException();
										}
									}
									player.getPrivateStore().getBuyList().setTitle(rs.getString("title"));
									break;
								case SELL:
								case PACKAGE_SELL:
									while (items.next())
									{
										if (player.getPrivateStore().getSellList().addItem(items.getInt(2), items.getInt(3), items.getInt(4)) == null)
										{
											throw new NullPointerException();
										}
									}
									player.getPrivateStore().getSellList().setTitle(rs.getString("title"));
									player.getPrivateStore().getSellList().setPackaged(type == PcStoreType.PACKAGE_SELL);
									break;
								case MANUFACTURE:
									PrivateStoreList createList = new PrivateStoreList();
									while (items.next())
									{
										createList.add(new ManufactureItemHolder(items.getInt(2), items.getInt(4)));
									}
									player.getPrivateStore().setCreateList(createList);
									player.getPrivateStore().getCreateList().setStoreName(rs.getString("title"));
									break;
							}
							
							player.sitDown();
							if (Config.OFFLINE_SET_NAME_COLOR)
							{
								player.setNameColor(Config.OFFLINE_NAME_COLOR);
							}
							player.getPrivateStore().setInOfflineMode();
							player.getPrivateStore().setStoreType(type);
							player.setOnlineStatus(true);
							player.restoreEffects();
							player.broadcastUserInfo();
							nTraders++;
						}
					}
				}
				catch (Exception e)
				{
					LOG.log(Level.WARNING, "OfflineTradersTable[loadOffliners()]: Error loading trader: " + player, e);
					if (player != null)
					{
						player.closeConnection();
					}
				}
			}
			
			LOG.info("Loaded: " + nTraders + " offline trader(s)");
			
			clearAllTables(con);
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "OfflineTradersTable[loadOffliners()]: Error while loading offline traders: ", e);
		}
	}
	
	private static void clearAllTables(Connection con) throws SQLException
	{
		try (PreparedStatement stm = con.prepareStatement(CLEAR_OFFLINE_TABLE))
		{
			stm.execute();
		}
		
		try (PreparedStatement stm = con.prepareStatement(CLEAR_OFFLINE_TABLE_ITEMS))
		{
			stm.execute();
		}
	}
}
