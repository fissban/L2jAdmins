package l2j.loginserver.network.internal.gameserver;

import l2j.loginserver.network.AClientPacket;

public class PlayerLogout extends AClientPacket
{
	private final String account;
	
	public PlayerLogout(byte[] decrypt)
	{
		super(decrypt);
		account = readS();
	}
	
	public String getAccount()
	{
		return account;
	}
}