package l2j.gameserver.network.internal.loginserver;

import l2j.gameserver.network.ALoginPacket;

public class KickPlayer extends ALoginPacket
{
	private final String account;
	
	/**
	 * @param decrypt
	 */
	public KickPlayer(byte[] decrypt)
	{
		super(decrypt);
		account = readS();
	}
	
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return account;
	}
}
