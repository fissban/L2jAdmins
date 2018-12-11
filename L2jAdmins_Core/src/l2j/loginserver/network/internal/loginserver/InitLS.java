package l2j.loginserver.network.internal.loginserver;

import l2j.loginserver.LoginServer;
import l2j.loginserver.network.AServerPacket;

public class InitLS extends AServerPacket
{
	public InitLS(byte[] publickey)
	{
		writeC(0x00);
		writeD(LoginServer.PROTOCOL_REV);
		writeD(publickey.length);
		writeB(publickey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}