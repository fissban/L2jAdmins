package l2j.loginserver.network.internal.gameserver;

import java.util.logging.Logger;

import l2j.loginserver.network.AClientPacket;

public class GameServerAuth extends AClientPacket
{
	protected static Logger LOG = Logger.getLogger(GameServerAuth.class.getName());
	
	private final byte[] hexId;
	private final int desiredId;
	private final boolean hostReserved;
	private final boolean acceptAlternativeId;
	private final int maxPlayers;
	private final int port;
	private final String hostName;
	
	public GameServerAuth(byte[] decrypt)
	{
		super(decrypt);
		
		desiredId = readC();
		acceptAlternativeId = (readC() == 0 ? false : true);
		hostReserved = (readC() == 0 ? false : true);
		hostName = readS();
		port = readH();
		maxPlayers = readD();
		int size = readD();
		hexId = readB(size);
	}
	
	public byte[] getHexID()
	{
		return hexId;
	}
	
	public boolean getHostReserved()
	{
		return hostReserved;
	}
	
	public int getDesiredID()
	{
		return desiredId;
	}
	
	public boolean acceptAlternateID()
	{
		return acceptAlternativeId;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getPort()
	{
		return port;
	}
}