package l2j.gameserver;

import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.data.OfflineTradersData;
import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.external.server.LeaveWorld;
import l2j.gameserver.network.internal.gameserver.ServerStatus;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;
import main.EngineModsManager;

/**
 * This class provides the functions for shutting down and restarting the server It closes all open client connections and saves all data.
 * @version $Revision: 1.2.4.5 $ $Date: 2005/03/27 15:29:09 $
 */
public class Shutdown extends Thread
{
	private static final Logger LOG = Logger.getLogger(Shutdown.class.getName());
	private static Shutdown counterInstance = null;
	
	private int secondsShut;
	
	private int shutdownMode;
	public static final int SIGTERM = 0;
	public static final int GM_SHUTDOWN = 1;
	public static final int GM_RESTART = 2;
	public static final int ABORT = 3;
	private static String[] modeText =
	{
		"SIGTERM",
		"shutting down",
		"restarting",
		"aborting"
	};
	
	/**
	 * This function starts a shutdown countdown from Telnet (Copied from Function startShutdown())
	 * @param ip      Which Issued shutdown command
	 * @param seconds seconds until shutdown
	 * @param restart true if the server will restart after shutdown
	 */
	public void startTelnetShutdown(String ip, int seconds, boolean restart)
	{
		LOG.warning("IP: " + ip + " issued shutdown command. " + modeText[shutdownMode] + " in " + seconds + " seconds!");
		
		if (restart)
		{
			shutdownMode = GM_RESTART;
		}
		else
		{
			shutdownMode = GM_SHUTDOWN;
		}
		
		if (shutdownMode > 0)
		{
			AnnouncementsData.getInstance().announceToAll("Attention players!");
			AnnouncementsData.getInstance().announceToAll("Server is " + modeText[shutdownMode] + " in " + seconds + " seconds!");
			if ((shutdownMode == 1) || (shutdownMode == 2))
			{
				AnnouncementsData.getInstance().announceToAll("Please, avoid to use Gatekeepers/SoE");
				AnnouncementsData.getInstance().announceToAll("during server " + modeText[shutdownMode] + " procedure.");
			}
		}
		
		if (counterInstance != null)
		{
			counterInstance.abort();
		}
		
		counterInstance = new Shutdown(seconds, restart);
		counterInstance.start();
	}
	
	/**
	 * This function aborts a running countdown
	 * @param ip IP Which Issued shutdown command
	 */
	public void telnetAbort(String ip)
	{
		LOG.warning("IP: " + ip + " issued shutdown ABORT. " + modeText[shutdownMode] + " has been stopped!");
		AnnouncementsData.getInstance().announceToAll("Server aborts " + modeText[shutdownMode] + " and continues normal operation!");
		
		if (counterInstance != null)
		{
			counterInstance.abort();
		}
	}
	
	/**
	 * Default constructor is only used internal to create the shutdown-hook instance
	 */
	public Shutdown()
	{
		secondsShut = -1;
		shutdownMode = SIGTERM;
	}
	
	/**
	 * This creates a countdown instance of Shutdown.
	 * @param seconds how many seconds until shutdown
	 * @param restart true is the server shall restart after shutdown
	 */
	public Shutdown(int seconds, boolean restart)
	{
		if (seconds < 0)
		{
			seconds = 0;
		}
		
		secondsShut = seconds;
		
		if (restart)
		{
			shutdownMode = GM_RESTART;
		}
		else
		{
			shutdownMode = GM_SHUTDOWN;
		}
	}
	
	/**
	 * this function is called, when a new thread starts if this thread is the thread of getInstance, then this is the shutdown hook and we save all data and disconnect all clients. after this thread ends, the server will completely exit if this is not the thread of getInstance, then this is a
	 * countdown thread. we start the countdown, and when we finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit status of the server is 1, startServer.sh / startServer.bat will restart the server.
	 */
	@Override
	public void run()
	{
		// disallow new logins
		
		if (this == getInstance())
		{
			if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			{
				OfflineTradersData.storeOffliners();
			}
			
			try
			{
				LoginServerThread.getInstance().interrupt();
			}
			catch (Throwable t)
			{
			}
			
			// last bye bye, save all data and quit this server
			// logging doesn't work here :(
			saveData();
			
			// saveData sends messages to exit players, so shutdown selector after it
			try
			{
				GameServer.gameServer.getSelectorThread().interrupt();
			}
			catch (Throwable t)
			{
			}
			
			// commit data, last chance
			DatabaseManager.shutdown();
			
			// stop all threadpools
			ThreadPoolManager.shutdown();
			
			// server will quit, when this function ends.
			if (getInstance().shutdownMode == GM_RESTART)
			{
				Runtime.getRuntime().halt(2);
			}
			else
			{
				Runtime.getRuntime().halt(0);
			}
		}
		else
		{
			// gm shutdown: send warnings and then call exit to start shutdown sequence
			countdown();
			
			// last point where logging is operational :(
			LOG.warning("GM shutdown countdown is over. " + modeText[shutdownMode] + " NOW!");
			switch (shutdownMode)
			{
				case GM_SHUTDOWN:
					getInstance().setMode(GM_SHUTDOWN);
					System.exit(0);
					break;
				case GM_RESTART:
					getInstance().setMode(GM_RESTART);
					System.exit(2);
					break;
			}
		}
	}
	
	/**
	 * This functions starts a shutdown countdown
	 * @param seconds seconds until shutdown
	 * @param restart true if the server will restart after shutdown
	 */
	public void startShutdown(int seconds, boolean restart)
	{
		if (restart)
		{
			shutdownMode = GM_RESTART;
		}
		else
		{
			shutdownMode = GM_SHUTDOWN;
		}
		
		if (shutdownMode > 0)
		{
			AnnouncementsData.getInstance().announceToAll("Attention players!");
			AnnouncementsData.getInstance().announceToAll("Server is " + modeText[shutdownMode] + " in " + seconds + " seconds!");
			if ((shutdownMode == 1) || (shutdownMode == 2))
			{
				AnnouncementsData.getInstance().announceToAll("Please, avoid to use Gatekeepers/SoE");
				AnnouncementsData.getInstance().announceToAll("during server " + modeText[shutdownMode] + " procedure.");
			}
		}
		
		if (counterInstance != null)
		{
			counterInstance.abort();
		}
		
		// the main instance should only run for shutdown hook, so we start a new instance
		counterInstance = new Shutdown(seconds, restart);
		counterInstance.start();
	}
	
	/**
	 * This function aborts a running countdown
	 * @param activeChar GM who issued the abort command
	 */
	public void abort(L2PcInstance activeChar)
	{
		LOG.warning("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") issued shutdown ABORT. " + modeText[shutdownMode] + " has been stopped!");
		AnnouncementsData.getInstance().announceToAll("Server aborts " + modeText[shutdownMode] + " and continues normal operation!");
		
		if (counterInstance != null)
		{
			counterInstance.abort();
		}
	}
	
	/**
	 * set the shutdown mode
	 * @param mode what mode shall be set
	 */
	private void setMode(int mode)
	{
		shutdownMode = mode;
	}
	
	/**
	 * set shutdown mode to ABORT
	 */
	private void abort()
	{
		shutdownMode = ABORT;
	}
	
	/**
	 * this counts the countdown and reports it to all players countdown is aborted if mode changes to ABORT
	 */
	private void countdown()
	{
		try
		{
			while (secondsShut > 0)
			{
				switch (secondsShut)
				{
					case 540:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 9 minutes.");
						break;
					case 480:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 8 minutes.");
						break;
					case 420:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 7 minutes.");
						break;
					case 360:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 6 minutes.");
						break;
					case 300:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 5 minutes.");
						break;
					case 240:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 4 minutes.");
						break;
					case 180:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 3 minutes.");
						break;
					case 120:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 2 minutes.");
						break;
					case 60:
						LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_DOWN); // prevents new players from logging in
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 1 minute.");
						break;
					case 30:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 30 seconds.");
						break;
					case 5:
						AnnouncementsData.getInstance().announceToAll("The server is " + modeText[shutdownMode] + " in 5 seconds, please log out NOW !");
						break;
				}
				
				secondsShut--;
				
				int delay = 1000; // milliseconds
				Thread.sleep(delay);
				
				if (shutdownMode == ABORT)
				{
					break;
				}
			}
		}
		catch (InterruptedException e)
		{
			// this will never happen
		}
	}
	
	/**
	 * this sends a last bye bye, disconnects all players and saves data
	 */
	private void saveData()
	{
		switch (shutdownMode)
		{
			case SIGTERM:
				System.err.println("SIGTERM received. Shutting down NOW!");
				break;
			case GM_SHUTDOWN:
				System.err.println("GM shutdown received. Shutting down NOW!");
				break;
			case GM_RESTART:
				System.err.println("GM restart received. Restarting NOW!");
				break;
		}
		
		try
		{
			AnnouncementsData.getInstance().announceToAll("Server is " + modeText[shutdownMode] + " NOW!");
		}
		catch (Throwable t)
		{
			LOG.log(Level.INFO, "", t);
		}
		
		EngineModsManager.onShutDown();
		
		// we cannot abort shutdown anymore, so i removed the "if"
		disconnectAllCharacters();
		
		// Seven Signs data is now saved along with Festival data.
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			SevenSignsFestival.getInstance().saveFestivalData(false);
		}
		
		// Save Seven Signs data before closing.
		SevenSignsManager.getInstance().saveSevenSignsData(null, true);
		
		// Save all raidboss and grandboss status.
		RaidBossSpawnData.getInstance().saveAllBoss();
		System.err.println("RaidBossSpawnManager: All Raid Boss info saved!!");
		
		ZoneGrandBossManager.clearAllZone();
		
		GrandBossSpawnData.saveAllBoss();
		System.err.println("GrandBossManager: All Grand Boss info saved!!");
		
		TradeControllerData.getInstance().dataCountStore();
		System.err.println("TradeController: All count Item saved!!");
		
		System.err.println("AnnouncementsData: All announcements saved!!");
		AnnouncementsData.getInstance().save();
		
		// save olympiad
		Olympiad.getInstance().saveOlympiadStatus();
		
		// Save all manor data
		CastleManorManager.getInstance().save();
		
		// Save items on ground before closing
		ItemsOnGroundTaskManager.getInstance().save();
		
		System.err.println("Data saved. All players were disconnected, shutting down.");
		
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			// never happens
		}
	}
	
	/**
	 * this disconnects all clients from the server
	 */
	private void disconnectAllCharacters()
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			final GameClient client = player.getClient();
			if ((client != null) && !client.isDetached())
			{
				client.close(LeaveWorld.STATIC_PACKET);
				client.setActiveChar(null);
				
				player.setClient(null);
			}
			player.deleteMe();
		}
	}
	
	public static Shutdown getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Shutdown INSTANCE = new Shutdown();
	}
}
