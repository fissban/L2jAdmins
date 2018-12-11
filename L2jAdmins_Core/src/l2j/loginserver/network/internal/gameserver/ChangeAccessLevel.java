package l2j.loginserver.network.internal.gameserver;

import l2j.loginserver.network.AClientPacket;

public class ChangeAccessLevel extends AClientPacket
{
	private final int level;
	private final String account;
	
	public ChangeAccessLevel(byte[] decrypt)
	{
		super(decrypt);
		level = readD();
		account = readS();
	}
	
	public String getAccount()
	{
		return account;
	}
	
	public int getLevel()
	{
		return level;
	}
}