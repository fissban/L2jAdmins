package l2j.gameserver.network.thread;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.external.server.AuthLoginFail;
import l2j.gameserver.network.external.server.AuthLoginFail.AuthLoginFailType;
import l2j.gameserver.network.external.server.CharSelectInfo;
import l2j.gameserver.network.internal.gameserver.AuthRequest;
import l2j.gameserver.network.internal.gameserver.BlowFishKey;
import l2j.gameserver.network.internal.gameserver.ChangeAccessLevel;
import l2j.gameserver.network.internal.gameserver.PlayerAuthRequest;
import l2j.gameserver.network.internal.gameserver.PlayerInGame;
import l2j.gameserver.network.internal.gameserver.PlayerLogout;
import l2j.gameserver.network.internal.gameserver.ServerStatus;
import l2j.gameserver.network.internal.loginserver.AuthResponse;
import l2j.gameserver.network.internal.loginserver.InitLS;
import l2j.gameserver.network.internal.loginserver.KickPlayer;
import l2j.gameserver.network.internal.loginserver.LoginServerFail;
import l2j.gameserver.network.internal.loginserver.PlayerAuthResponse;
import l2j.mmocore.AbstractThread;
import l2j.util.Rnd;
import l2j.util.crypt.NewCrypt;

public class LoginServerThread extends AbstractThread
{
	protected static final Logger LOG = Logger.getLogger(LoginServerThread.class.getName());
	
	private static final int REVISION = 0x0102;
	
	private final Map<String, GameClient> clients = new ConcurrentHashMap<>();
	
	private RSAPublicKey publicKey;
	private Socket loginSocket;
	
	private byte[] hexId;
	private int serverId;
	
	private int status;
	private String serverName;
	
	public LoginServerThread()
	{
		super("LoginServerThread");
		hexId = Config.HEX_ID;
		if (hexId == null)
		{
			hexId = generateHex(16);
		}
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				// Connection
				LOG.info("Connecting to login on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
				
				loginSocket = new Socket(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT);
				in = loginSocket.getInputStream();
				out = new BufferedOutputStream(loginSocket.getOutputStream());
				
				// init Blowfish
				blowfishKey = generateHex(40);
				blowfish = new NewCrypt(CRYPT);
				
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
							var init = new InitLS(data);
							
							if (init.getRevision() != REVISION)
							{
								// TODO: revision mismatch
								LOG.warning("/!\\ Revision mismatch between LS and GS /!\\");
								break;
							}
							try
							{
								var kfac = KeyFactory.getInstance("RSA");
								var modulus = new BigInteger(init.getRSAKey());
								var kspec1 = new RSAPublicKeySpec(modulus, RSAKeyGenParameterSpec.F4);
								publicKey = (RSAPublicKey) kfac.generatePublic(kspec1);
							}
							catch (GeneralSecurityException e)
							{
								LOG.warning("Troubles while init the public key send by login");
								break;
							}
							// send the blowfish key through the rsa encryption
							sendPacket(new BlowFishKey(blowfishKey, publicKey));
							
							// now, only accept packet with the new encryption
							blowfish = new NewCrypt(blowfishKey);
							
							sendPacket(new AuthRequest(Config.REQUEST_ID, Config.ACCEPT_ALTERNATE_ID, hexId, Config.EXTERNAL_HOSTNAME, Config.PORT_GAME, Config.RESERVE_HOST_ON_LOGIN, Config.MAXIMUM_ONLINE_USERS));
							break;
						
						case 0x01:
							var lsf = new LoginServerFail(data);
							LOG.info("Damn! Registration Failed: " + lsf.getReasonString());
							// login will close the connection here
							break;
						
						case 0x02:
							var aresp = new AuthResponse(data);
							serverId = aresp.getServerId();
							serverName = aresp.getServerName();
							Config.saveHexid(hexToString(hexId));
							LOG.info("Registered on login as Server " + serverId + " : " + serverName);
							
							var st = new ServerStatus();
							st.addAttribute(ServerStatus.SERVER_LIST_SQUARE_BRACKET, Config.SERVER_LIST_BRACKET ? ServerStatus.ON : ServerStatus.OFF);
							st.addAttribute(ServerStatus.SERVER_LIST_CLOCK, Config.SERVER_LIST_CLOCK ? ServerStatus.ON : ServerStatus.OFF);
							st.addAttribute(ServerStatus.TEST_SERVER, Config.SERVER_LIST_TESTSERVER ? ServerStatus.ON : ServerStatus.OFF);
							st.addAttribute(ServerStatus.SERVER_LIST_STATUS, Config.SERVER_GMONLY ? ServerStatus.STATUS_GM_ONLY : ServerStatus.STATUS_AUTO);
							sendPacket(st);
							
							if (!L2World.getInstance().getAllPlayers().isEmpty())
							{
								var playerList = new ArrayList<String>();
								for (var player : L2World.getInstance().getAllPlayers())
								{
									playerList.add(player.getAccountName());
								}
								
								sendPacket(new PlayerInGame(playerList));
							}
							break;
						
						case 0x03:
							var par = new PlayerAuthResponse(data);
							
							var client = clients.get(par.getAccount());
							
							if (client != null)
							{
								if (par.isAuthed())
								{
									sendPacket(new PlayerInGame(par.getAccount()));
									
									client.setState(GameClientState.AUTHED);
									client.sendPacket(new CharSelectInfo(par.getAccount(), client.getSessionId().playOkID1));
								}
								else
								{
									client.sendPacket(new AuthLoginFail(AuthLoginFailType.SYSTEM_ERROR_LOGIN_LATER));
									client.closeNow();
								}
							}
							break;
						
						case 0x04:
							var kp = new KickPlayer(data);
							doKickPlayer(kp.getAccount());
							break;
					}
				}
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				LOG.info("Disconnected from Login, Trying to reconnect:");
				LOG.info(e.toString());
			}
			finally
			{
				try
				{
					loginSocket.close();
				}
				catch (Exception e)
				{
					//
				}
			}
			
			try
			{
				Thread.sleep(5000); // 5 seconds temp.
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}
	
	public void addClient(String account, GameClient client)
	{
		var existingClient = clients.putIfAbsent(account, client);
		
		if (client.isDetached())
		{
			return;
		}
		
		if (existingClient == null)
		{
			sendPacket(new PlayerAuthRequest(client.getAccountName(), client.getSessionId()));
		}
		else
		{
			client.closeNow();
			existingClient.closeNow();
		}
	}
	
	public void sendLogout(String account)
	{
		try
		{
			sendPacket(new PlayerLogout(account));
		}
		catch (Exception e)
		{
			LOG.warning("Error while sending logout packet to login");
		}
		finally
		{
			clients.remove(account);
		}
	}
	
	public void sendAccessLevel(String account, int level)
	{
		sendPacket(new ChangeAccessLevel(account, level));
	}
	
	private String hexToString(byte[] hex)
	{
		return new BigInteger(hex).toString(16);
	}
	
	public void doKickPlayer(String account)
	{
		var client = clients.get(account);
		if (client != null)
		{
			client.closeNow();
		}
	}
	
	public static byte[] generateHex(int size)
	{
		var array = new byte[size];
		Rnd.nextBytes(array);
		return array;
	}
	
	/**
	 * server_gm_only ???????
	 * @param id
	 * @param value
	 */
	public void sendServerStatus(int id, int value)
	{
		ServerStatus ss = new ServerStatus();
		ss.addAttribute(id, value);
		sendPacket(ss);
	}
	
	/**
	 * @return
	 */
	public String getStatusString()
	{
		return ServerStatus.STATUS_STRINGS[status];
	}
	
	/**
	 * @return
	 */
	public boolean isClockShown()
	{
		return Config.SERVER_LIST_CLOCK;
	}
	
	/**
	 * @return
	 */
	public boolean isBracketShown()
	{
		return Config.SERVER_LIST_BRACKET;
	}
	
	/**
	 * @return the serverName.
	 */
	public String getServerName()
	{
		return serverName;
	}
	
	public void setServerStatus(int status)
	{
		switch (status)
		{
			case ServerStatus.STATUS_AUTO:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_AUTO);
				this.status = status;
				break;
			case ServerStatus.STATUS_DOWN:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_DOWN);
				this.status = status;
				break;
			case ServerStatus.STATUS_FULL:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_FULL);
				this.status = status;
				break;
			case ServerStatus.STATUS_GM_ONLY:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GM_ONLY);
				this.status = status;
				break;
			case ServerStatus.STATUS_GOOD:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_GOOD);
				this.status = status;
				break;
			case ServerStatus.STATUS_NORMAL:
				sendServerStatus(ServerStatus.SERVER_LIST_STATUS, ServerStatus.STATUS_NORMAL);
				this.status = status;
				break;
			default:
				throw new IllegalArgumentException("Status does not exists:" + status);
		}
	}
	
	public static LoginServerThread getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LoginServerThread INSTANCE = new LoginServerThread();
	}
}
