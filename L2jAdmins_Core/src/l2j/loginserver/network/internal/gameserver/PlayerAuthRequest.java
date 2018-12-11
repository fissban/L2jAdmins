package l2j.loginserver.network.internal.gameserver;

import l2j.loginserver.network.AClientPacket;
import l2j.loginserver.network.SessionKey;

public class PlayerAuthRequest extends AClientPacket
{
	private final String account;
	private final SessionKey sessionKey;
	
	public PlayerAuthRequest(byte[] decrypt)
	{
		super(decrypt);
		
		account = readS();
		
		int playKey1 = readD();
		int playKey2 = readD();
		int loginKey1 = readD();
		int loginKey2 = readD();
		
		sessionKey = new SessionKey(loginKey1, loginKey2, playKey1, playKey2);
	}
	
	public String getAccount()
	{
		return account;
	}
	
	public SessionKey getKey()
	{
		return sessionKey;
	}
}