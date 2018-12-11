package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * Fromat: (ch) (just a trigger)
 * @author -Wooden-
 */
public class ExMailArrived extends AServerPacket
{
	public static final ExMailArrived STATIC_PACKET = new ExMailArrived();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2d);
	}
}
