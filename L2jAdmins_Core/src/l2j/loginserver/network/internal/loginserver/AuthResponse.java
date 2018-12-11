package l2j.loginserver.network.internal.loginserver;

import l2j.loginserver.datatable.ServerNameTable;
import l2j.loginserver.network.AServerPacket;

public class AuthResponse extends AServerPacket
{
	public AuthResponse(int serverId)
	{
		writeC(0x02);
		writeC(serverId);
		writeS(ServerNameTable.getInstance().getServerName(serverId));
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}