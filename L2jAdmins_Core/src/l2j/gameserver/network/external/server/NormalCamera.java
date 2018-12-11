package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class NormalCamera extends AServerPacket
{
	@Override
	public void writeImpl()
	{
		writeC(0xc8);
	}
}
