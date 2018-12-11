package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;

public final class GGAuth extends ALoginServerPacket
{
	public static final int SKIP_GG_AUTH_REQUEST = 0x0b;
	
	private final int response;
	
	public GGAuth(int response)
	{
		this.response = response;
	}
	
	@Override
	public void write()
	{
		writeC(0x0b);
		writeD(response);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
	}
}