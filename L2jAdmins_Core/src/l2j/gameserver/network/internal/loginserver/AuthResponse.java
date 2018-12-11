package l2j.gameserver.network.internal.loginserver;

import l2j.gameserver.network.ALoginPacket;

/**
 * @author -Wooden-
 */
public class AuthResponse extends ALoginPacket
{
	private final int serverId;
	private final String serverName;
	
	/**
	 * @param decrypt
	 */
	public AuthResponse(byte[] decrypt)
	{
		super(decrypt);
		serverId = readC();
		serverName = readS();
	}
	
	/**
	 * @return Returns the serverId.
	 */
	public int getServerId()
	{
		return serverId;
	}
	
	/**
	 * @return Returns the serverName.
	 */
	public String getServerName()
	{
		return serverName;
	}
}
