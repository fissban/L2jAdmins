package l2j.loginserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import l2j.Config;
import l2j.loginserver.datatable.ServerNameTable;
import l2j.loginserver.model.GameServerInfo;
import l2j.loginserver.network.internal.gameserver.BlowFishKey;
import l2j.loginserver.network.internal.gameserver.ChangeAccessLevel;
import l2j.loginserver.network.internal.gameserver.GameServerAuth;
import l2j.loginserver.network.internal.gameserver.PlayerAuthRequest;
import l2j.loginserver.network.internal.gameserver.PlayerInGame;
import l2j.loginserver.network.internal.gameserver.PlayerLogout;
import l2j.loginserver.network.internal.gameserver.ServerStatus;
import l2j.loginserver.network.internal.loginserver.AuthResponse;
import l2j.loginserver.network.internal.loginserver.InitLS;
import l2j.loginserver.network.internal.loginserver.KickPlayer;
import l2j.loginserver.network.internal.loginserver.LoginServerFail;
import l2j.loginserver.network.internal.loginserver.PlayerAuthResponse;
import l2j.mmocore.AbstractThread;
import l2j.util.crypt.NewCrypt;

public class GameServerThread extends AbstractThread
{
	protected static final Logger LOG = Logger.getLogger(GameServerThread.class.getName());
	private final Socket connection;
	
	private final RSAPublicKey publicKey;
	private final RSAPrivateKey privateKey;
	
	private final String connectionIp;
	
	private GameServerInfo gsi;
	
	/** Authed Clients on a GameServer */
	private final Set<String> accountsOnGameServer = new HashSet<>();
	
	private String connectionIpAddress;
	
	public GameServerThread(Socket con)
	{
		super("GameServerThread");
		
		connection = con;
		connectionIp = con.getInetAddress().getHostAddress();
		try
		{
			in = connection.getInputStream();
			out = new BufferedOutputStream(connection.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		var pair = GameServerTable.getInstance().getKeyPair();
		privateKey = (RSAPrivateKey) pair.getPrivate();
		publicKey = (RSAPublicKey) pair.getPublic();
		blowfish = new NewCrypt(CRYPT);
		start();
	}
	
	@Override
	public void run()
	{
		connectionIpAddress = connection.getInetAddress().getHostAddress();
		
		// Ensure no further processing for this connection if server is considered as banned.
		if (GameServerThread.isBannedGameserverIP(connectionIpAddress))
		{
			LOG.info("GameServer with banned IP " + connectionIpAddress + " tries to register.");
			forceClose(LoginServerFail.REASON_IP_BANNED);
			return;
		}
		
		try
		{
			sendPacket(new InitLS(publicKey.getModulus().toByteArray()));
			
			int opCode;
			byte[] data;
			
			while (true)
			{
				data = readData();
				
				if (data == null)
				{
					break;
				}
				
				opCode = data[0] & 0xff;
				switch (opCode)
				{
					case 0x00:
						onReceiveBlowfishKey(data);
						break;
					case 0x01:
						onGameServerAuth(data);
						break;
					case 0x02:
						onReceivePlayerInGame(data);
						break;
					case 0x03:
						onReceivePlayerLogOut(data);
						break;
					case 0x04:
						onReceiveChangeAccessLevel(data);
						break;
					case 0x05:
						onReceivePlayerAuthRequest(data);
						break;
					case 0x06:
						onReceiveServerStatus(data);
						break;
					default:
						LOG.warning("Unknown Opcode (" + Integer.toHexString(opCode).toUpperCase() + ") from GameServer, closing connection.");
						forceClose(LoginServerFail.NOT_AUTHED);
				}
				
			}
		}
		catch (IOException e)
		{
			String serverName = (getServerId() != -1 ? "[" + getServerId() + "] " + ServerNameTable.getInstance().getServerName(getServerId()) : "(" + connectionIpAddress + ")");
			LOG.info("GameServer " + serverName + ": " + e.getMessage() + ".");
		}
		finally
		{
			if (isAuthed())
			{
				gsi.setDown();
				LOG.info("GameServer [" + getServerId() + "] " + ServerNameTable.getInstance().getServerName(getServerId()) + " is now set as disconnected.");
			}
			LoginServer.getInstance().getGameServerListener().removeGameServer(this);
			LoginServer.getInstance().getGameServerListener().removeFloodProtection(connectionIp);
		}
	}
	
	private void onReceiveBlowfishKey(byte[] data)
	{
		var bfk = new BlowFishKey(data, privateKey);
		
		blowfishKey = bfk.getKey();
		blowfish = new NewCrypt(blowfishKey);
	}
	
	private void onGameServerAuth(byte[] data)
	{
		handleRegProcess(new GameServerAuth(data));
		
		if (isAuthed())
		{
			sendPacket(new AuthResponse(gsi.getId()));
		}
	}
	
	private void onReceivePlayerInGame(byte[] data)
	{
		if (isAuthed())
		{
			var pig = new PlayerInGame(data);
			
			for (var account : pig.getAccounts())
			{
				accountsOnGameServer.add(account);
			}
		}
		else
		{
			forceClose(LoginServerFail.NOT_AUTHED);
		}
	}
	
	private void onReceivePlayerLogOut(byte[] data)
	{
		if (isAuthed())
		{
			final PlayerLogout plo = new PlayerLogout(data);
			
			accountsOnGameServer.remove(plo.getAccount());
		}
		else
		{
			forceClose(LoginServerFail.NOT_AUTHED);
		}
	}
	
	private void onReceiveChangeAccessLevel(byte[] data)
	{
		if (isAuthed())
		{
			var cal = new ChangeAccessLevel(data);
			
			LoginController.getInstance().setAccountAccessLevel(cal.getAccount(), cal.getLevel());
			LOG.info("Changed " + cal.getAccount() + " access level to " + cal.getLevel() + ".");
		}
		else
		{
			forceClose(LoginServerFail.NOT_AUTHED);
		}
	}
	
	private void onReceivePlayerAuthRequest(byte[] data)
	{
		if (isAuthed())
		{
			var par = new PlayerAuthRequest(data);
			var key = LoginController.getInstance().getKeyForAccount(par.getAccount());
			
			if ((key != null) && key.equals(par.getKey()))
			{
				LoginController.getInstance().removeAuthedLoginClient(par.getAccount());
				sendPacket(new PlayerAuthResponse(par.getAccount(), true));
			}
			else
			{
				sendPacket(new PlayerAuthResponse(par.getAccount(), false));
			}
		}
		else
		{
			forceClose(LoginServerFail.NOT_AUTHED);
		}
	}
	
	private void onReceiveServerStatus(byte[] data)
	{
		if (isAuthed())
		{
			new ServerStatus(data, getServerId()); // will do the actions by itself
		}
		else
		{
			forceClose(LoginServerFail.NOT_AUTHED);
		}
	}
	
	private void handleRegProcess(GameServerAuth gameServerAuth)
	{
		var id = gameServerAuth.getDesiredID();
		var hexId = gameServerAuth.getHexID();
		
		var gsi = GameServerTable.getInstance().getRegisteredGameServers().get(id);
		// is there a gameserver registered with this id?
		if (gsi != null)
		{
			// does the hex id match?
			if (Arrays.equals(gsi.getHexId(), hexId))
			{
				// check to see if this GS is already connected
				synchronized (gsi)
				{
					if (gsi.isAuthed())
					{
						forceClose(LoginServerFail.REASON_ALREADY_LOGGED_IN);
					}
					else
					{
						attachGameServerInfo(gsi, gameServerAuth);
					}
				}
			}
			else
			{
				// there is already a server registered with the desired id and different hex id
				// try to register this one with an alternative id
				if (Config.ACCEPT_NEW_GAMESERVER && gameServerAuth.acceptAlternateID())
				{
					gsi = new GameServerInfo(id, hexId, this);
					if (GameServerTable.getInstance().registerWithFirstAvailableId(gsi))
					{
						attachGameServerInfo(gsi, gameServerAuth);
						GameServerTable.getInstance().registerServerOnDB(gsi);
					}
					else
					{
						forceClose(LoginServerFail.REASON_NO_FREE_ID);
					}
				}
				// server id is already taken, and we cant get a new one for you
				else
				{
					forceClose(LoginServerFail.REASON_WRONG_HEXID);
				}
			}
		}
		else
		{
			// can we register on this id?
			if (Config.ACCEPT_NEW_GAMESERVER)
			{
				gsi = new GameServerInfo(id, hexId, this);
				if (GameServerTable.getInstance().register(id, gsi))
				{
					attachGameServerInfo(gsi, gameServerAuth);
					GameServerTable.getInstance().registerServerOnDB(gsi);
				}
				// some one took this ID meanwhile
				else
				{
					forceClose(LoginServerFail.REASON_ID_RESERVED);
				}
			}
			else
			{
				forceClose(LoginServerFail.REASON_WRONG_HEXID);
			}
		}
	}
	
	public boolean hasAccountOnGameServer(String account)
	{
		return accountsOnGameServer.contains(account);
	}
	
	public int getPlayerCount()
	{
		return accountsOnGameServer.size();
	}
	
	/**
	 * Attachs a GameServerInfo to this Thread
	 * <li>Updates the GameServerInfo values based on GameServerAuth packet</li>
	 * <li><b>Sets the GameServerInfo as Authed</b></li>
	 * @param gsi            The GameServerInfo to be attached.
	 * @param gameServerAuth The server info.
	 */
	private void attachGameServerInfo(GameServerInfo gsi, GameServerAuth gameServerAuth)
	{
		setGameServerInfo(gsi);
		gsi.setGameServerThread(this);
		gsi.setPort(gameServerAuth.getPort());
		
		if (!gameServerAuth.getHostName().equals("*"))
		{
			try
			{
				gsi.setHostName(InetAddress.getByName(gameServerAuth.getHostName()).getHostAddress());
			}
			catch (UnknownHostException e)
			{
				LOG.warning("Couldn't resolve hostname \"" + gameServerAuth.getHostName() + "\"");
				gsi.setHostName(connectionIp);
			}
		}
		else
		{
			gsi.setHostName(connectionIp);
		}
		
		gsi.setMaxPlayers(gameServerAuth.getMaxPlayers());
		gsi.setAuthed(true);
		
		LOG.info("Hooked [" + getServerId() + "] " + ServerNameTable.getInstance().getServerName(getServerId()) + " gameserver on: " + gsi.getHostName());
	}
	
	private void forceClose(int reason)
	{
		sendPacket(new LoginServerFail(reason));
		
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			LOG.finer("GameServerThread: Failed disconnecting banned server, server already disconnected.");
		}
	}
	
	/**
	 * @param  ipAddress
	 * @return           true if the given IP is banned.
	 */
	public static boolean isBannedGameserverIP(String ipAddress)
	{
		InetAddress netAddress = null;
		try
		{
			netAddress = InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return LoginController.getInstance().isBannedAddress(netAddress);
	}
	
	public void kickPlayer(String account)
	{
		sendPacket(new KickPlayer(account));
	}
	
	/**
	 * @return the isAuthed.
	 */
	public boolean isAuthed()
	{
		return (gsi == null) ? false : gsi.isAuthed();
	}
	
	public void setGameServerInfo(GameServerInfo gsi)
	{
		this.gsi = gsi;
	}
	
	public GameServerInfo getGameServerInfo()
	{
		return gsi;
	}
	
	/**
	 * @return the connectionIpAddress.
	 */
	public String getConnectionIpAddress()
	{
		return connectionIpAddress;
	}
	
	/**
	 * @return the server id.
	 */
	private int getServerId()
	{
		return (gsi == null) ? -1 : gsi.getId();
	}
}