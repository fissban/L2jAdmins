package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class ManagePledgePower extends AServerPacket
{
	private final int privs;
	
	public ManagePledgePower(int privs)
	{
		this.privs = privs;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x30);
		writeD(0x00);
		writeD(0x00);
		writeD(privs);
	}
}
