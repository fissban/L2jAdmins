package l2j.loginserver.network.internal.loginserver;

import l2j.loginserver.network.AServerPacket;

public class PlayerAuthResponse extends AServerPacket
{
	public PlayerAuthResponse(String account, boolean response)
	{
		writeC(0x03);
		writeS(account);
		writeC(response ? 1 : 0);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}