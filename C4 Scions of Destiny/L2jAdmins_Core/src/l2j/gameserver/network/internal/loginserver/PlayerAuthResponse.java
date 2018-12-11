package l2j.gameserver.network.internal.loginserver;

import l2j.gameserver.network.ALoginPacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends ALoginPacket
{
	private final String account;
	private final boolean authed;
	
	/**
	 * @param decrypt
	 */
	public PlayerAuthResponse(byte[] decrypt)
	{
		super(decrypt);
		
		account = readS();
		authed = readC() != 0;
	}
	
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return account;
	}
	
	/**
	 * @return Returns the authed state.
	 */
	public boolean isAuthed()
	{
		return authed;
	}
}
