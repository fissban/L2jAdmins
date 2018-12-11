package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class ExOlympiadMatchEnd extends AServerPacket
{
	public static final ExOlympiadMatchEnd STATIC_PACKET = new ExOlympiadMatchEnd();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2c);
	}
}
