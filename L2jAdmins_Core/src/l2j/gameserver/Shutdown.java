package l2j.gameserver;

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
import l2j.gameserver.util.Broadcast;
import main.EngineModsManager;

/**
 * <li>To execute the server shutdown, {@link #startShutdown(int, boolean)} must be invoked within this class.
 * <li>It should never be run using {@link l2j.gameserver.ThreadPoolManager ThreadPoolManager}.
 * @author anonymous & fissban
 */
public class Shutdown extends Thread
{
	protected static final Logger LOG = Logger.getLogger(Shutdown.class.getName());
	
	public enum ShutdownType
	{
		NONE,
		SHUTDOWN,
		RESTART,
		ABORT;
		
		public String getText()
		{
			return toString().toLowerCase();
		}
	}
	
	/** seconds for execute shutdown */
	private int secondsShut;
	/** shut down type {@link #ShutdownType} */
	private ShutdownType shutdownMode = ShutdownType.NONE;
	
	/**
	 * Default constructor, only used for getInstance()
	 */
	public Shutdown()
	{
		//
	}
	
	/**
	 * This method is invoked to start the server shutdown process
	 * <li>The countdown is initiated during which the process abortion can be performed.
	 * <li>The login server is closed.
	 * <li>All data stored in memory is saved.
	 * <li>The connection to the DB is closed.
	 * <li>The Threads system is terminated.
	 */
	@Override
	public void run()
	{
		switch (shutdownMode)
		{
			case SHUTDOWN:
				System.err.println("--------------------- Shutting down NOW! ----------------------");
				break;
			case RESTART:
				System.err.println("----------------------- Restarting NOW! -----------------------");
				break;
		}
		
		countdown();
		
		if (shutdownMode == ShutdownType.ABORT)
		{
			shutdownMode = ShutdownType.NONE;
			return;
		}
		
		// Finish login server
		try
		{
			LoginServerThread.getInstance().interrupt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Last bye bye, save all data and quit this server
		// Logging doesn't work here :(
		saveData();
		
		// Finish game server
		try
		{
			GameServer.gameServer.getSelectorThread().interrupt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("EngineModsManager: Shutdown.");
		EngineModsManager.onShutDown();
		
		// Commit data, last chance
		System.out.println("DatabaseManager: Shutdown.");
		DatabaseManager.shutdown();
		
		// Stop all thread pools
		System.out.println("ThreadPoolManager: Shutdown.");
		ThreadPoolManager.shutdown();
		
		// Server will quit, when this function ends.
		if (shutdownMode == ShutdownType.RESTART)
		{
			Runtime.getRuntime().halt(2);
		}
		else
		{
			Runtime.getRuntime().halt(0);
		}
		
		// init ??
		shutdownMode = ShutdownType.NONE;
	}
	
	/**
	 * This functions starts a shutdown count down
	 * @param seconds seconds until shutdown
	 * @param restart true if the server will restart after shutdown
	 */
	public void startShutdown(int seconds, boolean restart)
	{
		// prevent more executions
		if (shutdownMode != ShutdownType.NONE)
		{
			return;
		}
		
		// time can never take negative values
		if (seconds < 0)
		{
			seconds = 0;
		}
		
		secondsShut = seconds;
		
		shutdownMode = restart ? ShutdownType.RESTART : ShutdownType.SHUTDOWN;
		
		// Announce server shutdown
		Broadcast.toAllOnlinePlayers("Attention players!");
		Broadcast.toAllOnlinePlayers("Server is " + shutdownMode.getText() + " in " + secondsShut + " seconds!");
		Broadcast.toAllOnlinePlayers("Please, avoid to use Gatekeepers/SoE");
		Broadcast.toAllOnlinePlayers("during server " + shutdownMode.getText() + " procedure.");
		
		// Start shutdown system
		start();
	}
	
	/**
	 * This function aborts a running countdown
	 * @param activeChar GM who issued the abort command
	 */
	public void abort(L2PcInstance activeChar)
	{
		if (activeChar != null)
		{
			LOG.warning("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") issued shutdown ABORT. " + shutdownMode.getText() + " has been stopped!");
		}
		Broadcast.toAllOnlinePlayers("Server aborts " + shutdownMode.getText() + " and continues normal operation!");
		
		shutdownMode = ShutdownType.ABORT;
	}
	
	/**
	 * this counts the countdown and reports it to all players countdown is aborted if mode changes to ABORT
	 */
	private void countdown()
	{
		while (secondsShut > 0)
		{
			switch (secondsShut)
			{
				case 540:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 9 minutes.");
					break;
				case 480:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 8 minutes.");
					break;
				case 420:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 7 minutes.");
					break;
				case 360:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 6 minutes.");
					break;
				case 300:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 5 minutes.");
					break;
				case 240:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 4 minutes.");
					break;
				case 180:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 3 minutes.");
					break;
				case 120:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 2 minutes.");
					break;
				case 60:
					LoginServerThread.getInstance().setServerStatus(ServerStatus.STATUS_DOWN); // prevents new players from logging in
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 1 minute.");
					break;
				case 30:
					Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in 30 seconds.");
					break;
			}
			
			if (secondsShut > 10)
			{
				Broadcast.toAllOnlinePlayers("The server is " + shutdownMode.getText() + " in " + secondsShut + " seconds, please log out NOW !");
			}
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			secondsShut--;
			
			if (shutdownMode == ShutdownType.ABORT)
			{
				return;
			}
		}
	}
	
	/**
	 * this sends a last bye bye, disconnects all players and saves data
	 */
	private void saveData()
	{
		Broadcast.toAllOnlinePlayers("Server is " + shutdownMode.getText() + " NOW!");
		
		// Cannot abort shutdown anymore, so i removed the "if"
		System.err.println("Shutdown: disconnect and save all players!!");
		disconnectAllPlayers();
		
		System.err.println("OfflineTradersData: Save data.");
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
		{
			OfflineTradersData.storeOffliners();
		}
		
		// Seven Signs data is now saved along with Festival data.
		System.err.println("SevenSignsManager: Save data.");
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			SevenSignsFestival.getInstance().saveFestivalData(false);
		}
		// Save Seven Signs data before closing.
		SevenSignsManager.getInstance().saveSevenSignsData(null, true);
		
		// Save all raidboss and grandboss status.
		System.err.println("RaidBossSpawnManager: Save data.");
		RaidBossSpawnData.getInstance().saveAllBoss();
		
		System.err.println("ZoneGrandBossManager: Save data.");
		ZoneGrandBossManager.storeToDb();
		
		GrandBossSpawnData.saveAllBoss();
		System.err.println("GrandBossManager: Save data.");
		
		TradeControllerData.getInstance().dataCountStore();
		System.err.println("TradeController: Save data.");
		
		System.err.println("AnnouncementsData: Save data.");
		AnnouncementsData.getInstance().save();
		
		System.err.println("Olympiad: Save data.");
		Olympiad.getInstance().saveOlympiadStatus();
		
		System.err.println("CastleManorManager: Save data.");
		CastleManorManager.getInstance().save();
		
		System.err.println("ItemsOnGroundTaskManager: Save data.");
		ItemsOnGroundTaskManager.getInstance().save();
		
		System.err.println("Data saved. All players were disconnected, shutting down.");
	}
	
	/**
	 * Disconnects and close clients from all players.
	 */
	private void disconnectAllPlayers()
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
