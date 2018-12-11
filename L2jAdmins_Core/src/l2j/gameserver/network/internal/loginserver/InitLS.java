package l2j.gameserver.network.internal.loginserver;

import l2j.gameserver.network.ALoginPacket;

public class InitLS extends ALoginPacket
{
	private final int rev;
	private final byte[] key;
	
	public InitLS(byte[] decrypt)
	{
		super(decrypt);
		rev = readD();
		int size = readD();
		key = readB(size);
	}
	
	public int getRevision()
	{
		return rev;
	}
	
	public byte[] getRSAKey()
	{
		return key;
	}
}
