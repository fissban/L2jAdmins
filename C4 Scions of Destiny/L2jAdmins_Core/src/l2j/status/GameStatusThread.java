package l2j.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.Shutdown;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.MultisellData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.DecayTaskManager;
import l2j.gameserver.util.audit.GMAudit;

public class GameStatusThread extends Thread
{
	private final Socket csocket;
	
	private final PrintWriter print;
	private final BufferedReader read;
	
	private final int uptime;
	
	private void telnetOutput(int type, String text)
	{
		// only print output if the message is rejected
		if (type == 5)
		{
			System.out.println("TELNET | " + text);
		}
	}
	
	private boolean isValidIP(Socket client)
	{
		boolean result = false;
		InetAddress ClientIP = client.getInetAddress();
		
		// convert IP to String, and compare with list
		String clientStringIP = ClientIP.getHostAddress();
		
		telnetOutput(1, "Connection from: " + clientStringIP);
		
		try (InputStream telnetIS = new FileInputStream(new File(Config.FILE_TELNET)))
		{
			Properties telnetSettings = new Properties();
			telnetSettings.load(telnetIS);
			telnetIS.close();
			
			String HostList = telnetSettings.getProperty("ListOfHosts", "127.0.0.1,localhost");
			
			// compare
			String ipToCompare = null;
			for (String ip : HostList.split(","))
			{
				if (!result)
				{
					ipToCompare = InetAddress.getByName(ip).getHostAddress();
					if (clientStringIP.equals(ipToCompare))
					{
						result = true;
					}
				}
			}
		}
		catch (IOException e)
		{
			telnetOutput(1, "Error: " + e);
		}
		
		return result;
	}
	
	public GameStatusThread(Socket client, int uptime, String StatusPW) throws IOException
	{
		csocket = client;
		this.uptime = uptime;
		
		print = new PrintWriter(csocket.getOutputStream());
		read = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
		
		if (isValidIP(client))
		{
			telnetOutput(1, client.getInetAddress().getHostAddress() + " accepted.");
			print.println("Welcome To The L2J Telnet Session.");
			print.println("Please Insert Your Password!");
			print.print("Password: ");
			print.flush();
			String tmpLine = read.readLine();
			if (tmpLine == null)
			{
				print.println("Error.");
				print.println("Disconnected...");
				print.flush();
				csocket.close();
			}
			else
			{
				if (tmpLine.compareTo(StatusPW) != 0)
				{
					print.println("Incorrect Password!");
					print.println("Disconnected...");
					print.flush();
					csocket.close();
				}
				else
				{
					print.println("Password Correct!");
					print.println("[L2J]");
					print.print("");
					print.flush();
					start();
				}
			}
		}
		else
		{
			telnetOutput(5, "Connection attempt from " + client.getInetAddress().getHostAddress() + " rejected.");
			csocket.close();
		}
	}
	
	@Override
	public void run()
	{
		String usrCommand = "";
		try
		{
			while ((usrCommand.compareTo("quit") != 0) && (usrCommand.compareTo("exit") != 0))
			{
				usrCommand = read.readLine();
				if (usrCommand == null)
				{
					csocket.close();
					break;
				}
				if (usrCommand.equals("help"))
				{
					print.println("The following is a list of all available commands: ");
					print.println("help                - shows this help.");
					print.println("status              - displays basic server statistics.");
					print.println("performance         - shows server performance statistics.");
					print.println("purge               - removes finished threads from thread pools.");
					print.println("announce <text>     - announces <text> in game.");
					print.println("msg <nick> <text>   - Sends a whisper to char <nick> with <text>.");
					print.println("gmchat <text>       - Sends a message to all GMs with <text>.");
					print.println("gmlist              - lists all gms online.");
					print.println("kick                - kick player <name> from server.");
					print.println("shutdown <time>     - shuts down server in <time> seconds.");
					print.println("restart <time>      - restarts down server in <time> seconds.");
					print.println("abort               - aborts shutdown/restart.");
					print.println("give <player> <itemid> <amount>");
					print.println("extlist             - list all loaded extension classes");
					print.println("extreload <name>    - reload and initializes the named extension or all if used without argument");
					print.println("extinit <name>      - initilizes the named extension or all if used without argument");
					print.println("extunload <name>    - unload the named extension or all if used without argument");
					print.println("debug <cmd>         - executes the debug command (see 'help debug').");
					print.println("jail <player> [time]");
					print.println("unjail <player>");
					print.println("quit                - closes telnet session.");
				}
				else if (usrCommand.equals("help debug"))
				{
					print.println("The following is a list of all available debug commands: ");
					print.println("decay               - prints info about the DecayManager");
					// print.println("PacketTP - prints info about the General Packet ThreadPool");
					// print.println("IOPacketTP - prints info about the I/O Packet ThreadPool");
					// print.println("GeneralTP - prints info about the General ThreadPool");
				}
				else if (usrCommand.equals("status"))
				{
					//
				}
				else if (usrCommand.equals("performance"))
				{
					//
				}
				else if (usrCommand.equals("purge"))
				{
					//
				}
				else if (usrCommand.startsWith("announce"))
				{
					try
					{
						usrCommand = usrCommand.substring(9);
						AnnouncementsData.getInstance().announceToAll(usrCommand);
						print.println("Announcement Sent!");
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please Enter Some Text To Announce!");
					}
				}
				else if (usrCommand.startsWith("msg"))
				{
					try
					{
						String val = usrCommand.substring(4);
						StringTokenizer st = new StringTokenizer(val);
						String name = st.nextToken();
						String message = val.substring(name.length() + 1);
						L2PcInstance reciever = L2World.getInstance().getPlayer(name);
						
						if (reciever != null)
						{
							reciever.sendPacket(new CreatureSay(SayType.TELL, "Telnet Priv", message));
							print.println("Telnet Priv->" + name + ": " + message);
							print.println("Message Sent!");
						}
						else
						{
							print.println("Unable To Find Username: " + name);
						}
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please Enter Some Text!");
					}
				}
				else if (usrCommand.startsWith("gmchat"))
				{
					try
					{
						usrCommand = usrCommand.substring(7);
						GmListData.getInstance().broadcastToGMs(new CreatureSay(SayType.ALLIANCE, "Telnet GM Broadcast from " + csocket.getInetAddress().getHostAddress(), usrCommand));
						print.println("Your Message Has Been Sent To " + GetOnlineGMS() + " GM(s).");
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please Enter Some Text To Announce!");
					}
				}
				else if (usrCommand.equals("gmlist"))
				{
					int igm = 0;
					String gmList = "";
					
					for (L2PcInstance player : GmListData.getInstance().getAllGms())
					{
						gmList = gmList + ", " + player.getName();
						igm++;
					}
					print.println("There are currently " + igm + " GM(s) online...");
					if (!gmList.isEmpty())
					{
						print.println(gmList);
					}
				}
				/*
				 * else if (usrCommand.startsWith("unblock")) { try { usrCommand = usrCommand.substring(8); if (LoginServer.getInstance().unblockIp(usrCommand)) { log.warning("IP removed via TELNET by host: " + csocket.getInetAddress().getHostAddress()); print.println("The IP " + usrCommand +
				 * " has been removed from the hack protection list!"); } else { print.println("IP not found in hack protection list..."); } //TODO: with packet } catch (StringIndexOutOfBoundsException e) { print.println("Please Enter the IP to Unblock!"); } }
				 */
				else if (usrCommand.startsWith("kick"))
				{
					try
					{
						usrCommand = usrCommand.substring(5);
						L2PcInstance player = L2World.getInstance().getPlayer(usrCommand);
						if (player != null)
						{
							player.sendMessage("You are kicked by gm");
							player.closeConnection();
							print.println("Player kicked");
						}
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please enter player name to kick");
					}
				}
				else if (usrCommand.startsWith("shutdown"))
				{
					try
					{
						int val = Integer.parseInt(usrCommand.substring(9));
						Shutdown.getInstance().startTelnetShutdown(csocket.getInetAddress().getHostAddress(), val, false);
						print.println("Server Will Shutdown In " + val + " Seconds!");
						print.println("Type \"abort\" To Abort Shutdown!");
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please Enter * amount of seconds to shutdown!");
					}
					catch (Exception NumberFormatException)
					{
						print.println("Numbers Only!");
					}
				}
				else if (usrCommand.startsWith("restart"))
				{
					try
					{
						int val = Integer.parseInt(usrCommand.substring(8));
						Shutdown.getInstance().startTelnetShutdown(csocket.getInetAddress().getHostAddress(), val, true);
						print.println("Server Will Restart In " + val + " Seconds!");
						print.println("Type \"abort\" To Abort Restart!");
					}
					catch (StringIndexOutOfBoundsException e)
					{
						print.println("Please Enter * amount of seconds to restart!");
					}
					catch (Exception NumberFormatException)
					{
						print.println("Numbers Only!");
					}
				}
				else if (usrCommand.startsWith("abort"))
				{
					Shutdown.getInstance().telnetAbort(csocket.getInetAddress().getHostAddress());
					print.println("OK! - Shutdown/Restart Aborted.");
				}
				else if (usrCommand.equals("quit"))
				{ /* Do Nothing :p - Just here to save us from the "Command Not Understood" Text */
				}
				else if (usrCommand.startsWith("give"))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(5));
					
					try
					{
						L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
						int itemId = Integer.parseInt(st.nextToken());
						int amount = Integer.parseInt(st.nextToken());
						
						if (player != null)
						{
							ItemInstance item = player.getInventory().addItem("Status-Give", itemId, amount, null, null);
							InventoryUpdate iu = new InventoryUpdate();
							iu.addItems(item);
							player.sendPacket(iu);
							SystemMessage sm = new SystemMessage(SystemMessage.YOU_PICKED_UP_S1_S2);
							sm.addItemName(itemId);
							sm.addNumber(amount);
							player.sendPacket(sm);
							print.println("ok");
							GMAudit.auditGMAction("Telnet Admin", "Give Item", player.getName(), "item: " + itemId + " amount: " + amount);
						}
					}
					catch (Exception e)
					{
						
					}
				}
				else if (usrCommand.startsWith("jail"))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(5));
					try
					{
						String playerName = st.nextToken();
						L2PcInstance playerObj = L2World.getInstance().getPlayer(playerName);
						int delay = 0;
						try
						{
							delay = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException nfe)
						{
						}
						catch (NoSuchElementException nsee)
						{
						}
						// L2PcInstance playerObj = L2World.getInstance().getPlayer(player);
						
						if (playerObj != null)
						{
							playerObj.setInJail(true, delay);
							print.println("Character " + playerObj.getName() + " jailed for " + (delay > 0 ? delay + " minutes." : "ever!"));
						}
						else
						{
							jailOfflinePlayer(playerName, delay);
						}
					}
					catch (NoSuchElementException nsee)
					{
						print.println("Specify a character name.");
					}
					catch (Exception e)
					{
						if (Config.DEBUG)
						{
							e.printStackTrace();
						}
					}
				}
				else if (usrCommand.startsWith("unjail"))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(7));
					try
					{
						String playerName = st.nextToken();
						L2PcInstance playerObj = L2World.getInstance().getPlayer(playerName);
						
						if (playerObj != null)
						{
							playerObj.stopJailTask(false);
							playerObj.setInJail(false, 0);
							print.println("Character " + playerObj.getName() + " removed from jail");
						}
						else
						{
							unjailOfflinePlayer(playerName);
						}
					}
					catch (NoSuchElementException nsee)
					{
						print.println("Specify a character name.");
					}
					catch (Exception e)
					{
						if (Config.DEBUG)
						{
							e.printStackTrace();
						}
					}
				}
				else if (usrCommand.startsWith("debug") && (usrCommand.length() > 6))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(6));
					try
					{
						String dbg = st.nextToken();
						
						if (dbg.equals("decay"))
						{
							print.print(DecayTaskManager.getInstance().toString());
						}
						else if (dbg.equals("ai"))
						{
							//
						}
						else if (dbg.equals("aiflush"))
						{
							//
						}
						else if (dbg.equals("PacketTP"))
						{
							//
						}
						else if (dbg.equals("IOPacketTP"))
						{
							//
						}
						else if (dbg.equals("GeneralTP"))
						{
							//
						}
					}
					catch (Exception e)
					{
					}
				}
				else if (usrCommand.startsWith("reload"))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(7));
					try
					{
						String type = st.nextToken();
						
						if (type.equals("multisell"))
						{
							print.print("Reloading multisell... ");
							MultisellData.getInstance().reload();
							print.print("done\n");
						}
						else if (type.equals("skill"))
						{
							print.print("Reloading skills... ");
							SkillData.getInstance().reload();
							print.print("done\n");
						}
						else if (type.equals("npc"))
						{
							print.print("Reloading npc templates... ");
							NpcData.getInstance().reload();
							print.print("done\n");
						}
						else if (type.equals("html"))
						{
							print.print("Reloading html cache... ");
							HtmData.getInstance().reload();
							print.print("done\n");
						}
						else if (type.equals("item"))
						{
							print.print("Reloading item templates... ");
							ItemData.getInstance().reload();
							print.print("done\n");
						}
						else if (type.equals("instancemanager"))
						{
							print.print("Reloading instance managers... ");
							print.print("aun sin diseñar!"); // TODO crear el sistema para recargar todo!
							// Manager.reloadAll();
							print.print("done\n");
						}
						
					}
					catch (Exception e)
					{
					}
				}
				else if (usrCommand.startsWith("gamestat"))
				{
					StringTokenizer st = new StringTokenizer(usrCommand.substring(9));
					try
					{
						String type = st.nextToken();
						
						// name;type;x;y;itemId:enchant:price...
						if (type.equals("privatestore"))
						{
							for (L2PcInstance player : L2World.getInstance().getAllPlayers())
							{
								if (!player.getPrivateStore().isInStoreMode())
								{
									continue;
								}
								
								CharacterTradeList list = null;
								String content = "";
								
								if (player.getPrivateStore().getStoreType() == PcStoreType.SELL)
								{
									list = player.getPrivateStore().getSellList();
									for (TradeItemHolder item : list.getItems())
									{
										content += item.getItem().getId() + ":" + item.getEnchant() + ":" + item.getPrice() + ":";
									}
									content = player.getName() + ";" + "sell;" + player.getX() + ";" + player.getY() + ";" + content;
									print.println(content);
									continue;
								}
								else if (player.getPrivateStore().getStoreType() == PcStoreType.BUY)
								{
									list = player.getPrivateStore().getBuyList();
									for (TradeItemHolder item : list.getItems())
									{
										content += item.getItem().getId() + ":" + item.getEnchant() + ":" + item.getPrice() + ":";
									}
									content = player.getName() + ";" + "buy;" + player.getX() + ";" + player.getY() + ";" + content;
									print.println(content);
									continue;
								}
								
							}
						}
					}
					catch (Exception e)
					{
					}
				}
				else if (usrCommand.startsWith("extreload"))
				{
				}
				else if (usrCommand.startsWith("extinit"))
				{
				}
				else if (usrCommand.startsWith("extunload"))
				{
				}
				else if (usrCommand.startsWith("extlist"))
				{
				}
				else if (usrCommand.startsWith("get"))
				{
				}
				else if (usrCommand.length() > 0)
				{
				}
				else if (usrCommand.length() == 0)
				{
					/* Do Nothing Again - Same reason as the quit part */
				}
				print.print("");
				print.flush();
			}
			if (!csocket.isClosed())
			{
				print.println("Bye Bye!");
				print.flush();
				csocket.close();
			}
			telnetOutput(1, "Connection from " + csocket.getInetAddress().getHostAddress() + " was closed by client.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void jailOfflinePlayer(String name, int delay)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?"))
		{
			ps.setInt(1, -114356);
			ps.setInt(2, -249645);
			ps.setInt(3, -2984);
			ps.setInt(4, 1);
			ps.setLong(5, delay * 60000);
			ps.setString(6, name);
			
			ps.execute();
			int count = ps.getUpdateCount();
			
			if (count == 0)
			{
				print.println("Character not found!");
			}
			else
			{
				print.println("Character " + name + " jailed for " + (delay > 0 ? delay + " minutes." : "ever!"));
			}
		}
		catch (SQLException se)
		{
			print.println("SQLException while jailing player");
			if (Config.DEBUG)
			{
				se.printStackTrace();
			}
		}
	}
	
	private void unjailOfflinePlayer(String name)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?"))
		{
			ps.setInt(1, 17836);
			ps.setInt(2, 170178);
			ps.setInt(3, -3507);
			ps.setInt(4, 0);
			ps.setLong(5, 0);
			ps.setString(6, name);
			
			ps.execute();
			int count = ps.getUpdateCount();
			
			if (count == 0)
			{
				print.println("Character not found!");
			}
			else
			{
				print.println("Character " + name + " set free.");
			}
		}
		catch (SQLException se)
		{
			print.println("SQLException while jailing player");
			if (Config.DEBUG)
			{
				se.printStackTrace();
			}
		}
	}
	
	private int GetOnlineGMS()
	{
		return GmListData.getInstance().getAllGms().size();
	}
	
	private String GetUptime(int time)
	{
		int uptime = (int) System.currentTimeMillis() - time;
		uptime = uptime / 1000;
		int h = uptime / 3600;
		int m = (uptime - (h * 3600)) / 60;
		int s = ((uptime - (h * 3600)) - (m * 60));
		return h + "hrs " + m + "mins " + s + "secs";
	}
}
