package l2j.loginserver.model;

public class ServerData
{
	private final int status;
	private final String hostName;
	
	private final int serverId;
	private final int port;
	private final int currentPlayers;
	private final int maxPlayers;
	private final int ageLimit;
	private final boolean isPvp;
	private final boolean isTestServer;
	private final boolean isShowingBrackets;
	private final boolean isShowingClock;
	
	public ServerData(int status, String hostName, GameServerInfo gsi)
	{
		this.status = status;
		this.hostName = hostName;
		
		serverId = gsi.getId();
		port = gsi.getPort();
		currentPlayers = gsi.getCurrentPlayerCount();
		maxPlayers = gsi.getMaxPlayers();
		ageLimit = gsi.getAgeLimit();
		isPvp = gsi.isPvp();
		isTestServer = gsi.isTestServer();
		isShowingBrackets = gsi.isShowingBrackets();
		isShowingClock = gsi.isShowingClock();
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getServerId()
	{
		return serverId;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public int getCurrentPlayers()
	{
		return currentPlayers;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public int getAgeLimit()
	{
		return ageLimit;
	}
	
	public boolean isPvp()
	{
		return isPvp;
	}
	
	public boolean isTestServer()
	{
		return isTestServer;
	}
	
	public boolean isShowingBrackets()
	{
		return isShowingBrackets;
	}
	
	public boolean isShowingClock()
	{
		return isShowingClock;
	}
}