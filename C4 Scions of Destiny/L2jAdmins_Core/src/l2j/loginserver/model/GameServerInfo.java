package l2j.loginserver.model;

import l2j.loginserver.GameServerThread;
import l2j.loginserver.network.internal.gameserver.ServerStatus;

public class GameServerInfo
{
	private int id;
	private final byte[] hexId;
	private boolean isAuthed;
	
	private GameServerThread gst;
	private int status;
	
	private String hostName;
	private int port;
	
	private boolean isPvp;
	private boolean isTestServer;
	private boolean isShowingClock;
	private boolean isShowingBrackets;
	
	private int ageLimit;
	private int maxPlayers;
	
	public GameServerInfo(int id, byte[] hexId, GameServerThread gst)
	{
		this.id = id;
		this.hexId = hexId;
		this.gst = gst;
		status = ServerStatus.STATUS_DOWN;
	}
	
	public GameServerInfo(int id, byte[] hexId)
	{
		this(id, hexId, null);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public byte[] getHexId()
	{
		return hexId;
	}
	
	public boolean isAuthed()
	{
		return isAuthed;
	}
	
	public void setAuthed(boolean isAuthed)
	{
		this.isAuthed = isAuthed;
	}
	
	public GameServerThread getGameServerThread()
	{
		return gst;
	}
	
	public void setGameServerThread(GameServerThread gst)
	{
		this.gst = gst;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
	
	public boolean isPvp()
	{
		return isPvp;
	}
	
	public void setPvp(boolean isPvp)
	{
		this.isPvp = isPvp;
	}
	
	public boolean isTestServer()
	{
		return isTestServer;
	}
	
	public void setTestServer(boolean isTestServer)
	{
		this.isTestServer = isTestServer;
	}
	
	public boolean isShowingClock()
	{
		return isShowingClock;
	}
	
	public void setShowingClock(boolean isShowingClock)
	{
		this.isShowingClock = isShowingClock;
	}
	
	public boolean isShowingBrackets()
	{
		return isShowingBrackets;
	}
	
	public void setShowingBrackets(boolean isShowingBrackets)
	{
		this.isShowingBrackets = isShowingBrackets;
	}
	
	public int getAgeLimit()
	{
		return ageLimit;
	}
	
	public void setAgeLimit(int ageLimit)
	{
		this.ageLimit = ageLimit;
	}
	
	public void setDown()
	{
		setAuthed(false);
		setPort(0);
		setGameServerThread(null);
		setStatus(ServerStatus.STATUS_DOWN);
	}
	
	public int getCurrentPlayerCount()
	{
		return (gst == null) ? 0 : gst.getPlayerCount();
	}
}