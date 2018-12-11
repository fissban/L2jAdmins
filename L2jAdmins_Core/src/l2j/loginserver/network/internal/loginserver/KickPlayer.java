package l2j.loginserver.network.internal.loginserver;

import l2j.loginserver.network.AServerPacket;

public class KickPlayer extends AServerPacket
{
	public KickPlayer(String account)
	{
		writeC(0x04);
		writeS(account);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}