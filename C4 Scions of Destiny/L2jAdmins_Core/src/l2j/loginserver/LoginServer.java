package l2j.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.Server;
import l2j.loginserver.datatable.ServerNameTable;
import l2j.loginserver.network.LoginClient;
import l2j.loginserver.network.LoginPacketHandler;
import l2j.mmocore.SelectorConfig;
import l2j.mmocore.SelectorThread;
import l2j.util.UtilPrint;

public class LoginServer
{
	private static final Logger LOG = Logger.getLogger(LoginServer.class.getName());
	
	public static final int PROTOCOL_REV = 0x0102;
	
	private static LoginServer loginServer;
	
	private GameServerListener gameServerListener;
	private SelectorThread<LoginClient> selectorThread;
	
	public static void main(String[] args) throws Exception
	{
		loginServer = new LoginServer();
	}
	
	public LoginServer() throws Exception
	{
		Server.SERVER_MODE = Server.MODE_LOGINSERVER;
		
		// Local Constants
		var LOG_FOLDER = "log"; // Name of folder for log file
		var LOG_NAME = "./log.cfg"; // Name of log file
		
		/*** Main ***/
		// Create log folder
		var logFolder = new File(Config.DATAPACK_ROOT, LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		try (var is = new FileInputStream(new File(LOG_NAME)))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		catch (Exception e)
		{
			LOG.warning("missing file " + LOG_NAME);
		}
		
		LOG.config("Login Server Configs");
		Config.load();
		DatabaseManager.getInstance();
		ServerNameTable.getInstance().load();
		LoginController.load();
		GameServerTable.getInstance();
		loadBanFile();
		
		UtilPrint.section("IP, Ports & Socket infos");
		InetAddress bindAddress = null;
		if (!Config.GAME_SERVER_LOGIN_HOST.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAME_SERVER_LOGIN_HOST);
			}
			catch (UnknownHostException uhe)
			{
				LOG.severe("WARNING: The LoginServer bind address is invalid, using all available IPs. Reason: " + uhe.getMessage());
				uhe.printStackTrace();
			}
		}
		
		var sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		var lph = new LoginPacketHandler();
		var sh = new SelectorHelper();
		try
		{
			selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException ioe)
		{
			LOG.severe("FATAL: Failed to open selector. Reason: " + ioe.getMessage());
			ioe.printStackTrace();
			
			System.exit(1);
		}
		
		try
		{
			gameServerListener = new GameServerListener();
			gameServerListener.start();
			LOG.info("Listening for gameservers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
		}
		catch (IOException ioe)
		{
			LOG.severe("FATAL: Failed to start the gameserver listener. Reason: " + ioe.getMessage());
			ioe.printStackTrace();
			
			System.exit(1);
		}
		
		try
		{
			selectorThread.openServerSocket(bindAddress, Config.PORT_LOGIN);
		}
		catch (IOException ioe)
		{
			LOG.severe("FATAL: Failed to open server socket. Reason: " + ioe.getMessage());
			ioe.printStackTrace();
			
			System.exit(1);
		}
		selectorThread.start();
		LOG.info("Loginserver ready on " + (bindAddress == null ? "*" : bindAddress.getHostAddress()) + ":" + Config.PORT_LOGIN);
		
		UtilPrint.section("Waiting for gameserver answer");
	}
	
	public static LoginServer getInstance()
	{
		return loginServer;
	}
	
	public GameServerListener getGameServerListener()
	{
		return gameServerListener;
	}
	
	private static void loadBanFile()
	{
		var banFile = new File("config/banned_ips.properties");
		if (banFile.exists() && banFile.isFile())
		{
			try (var reader = new LineNumberReader(new FileReader(banFile)))
			{
				String line;
				String[] parts;
				
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					// check if this line isnt a comment line
					if ((line.length() > 0) && (line.charAt(0) != '#'))
					{
						// split comments if any
						parts = line.split("#");
						
						// discard comments in the line, if any
						line = parts[0];
						parts = line.split(" ");
						
						var address = parts[0];
						var duration = 0L;
						
						if (parts.length > 1)
						{
							try
							{
								duration = Long.parseLong(parts[1]);
							}
							catch (NumberFormatException e)
							{
								LOG.warning("Skipped: Incorrect ban duration (" + parts[1] + "). Line: " + reader.getLineNumber());
								continue;
							}
						}
						
						try
						{
							LoginController.getInstance().addBanForAddress(address, duration);
						}
						catch (UnknownHostException e)
						{
							LOG.warning("Skipped: Invalid address (" + parts[0] + "). Line: " + reader.getLineNumber());
						}
					}
				}
			}
			catch (IOException e)
			{
				LOG.warning("Error while reading banned_ips.properties. Details: " + e.getMessage());
				e.printStackTrace();
			}
			LOG.info("Loaded " + LoginController.getInstance().getBannedIps().size() + " banned IP(s).");
		}
		else
		{
			LOG.warning("banned_ips.properties is missing. Ban listing is skipped.");
		}
	}
	
	public void shutdown(boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}