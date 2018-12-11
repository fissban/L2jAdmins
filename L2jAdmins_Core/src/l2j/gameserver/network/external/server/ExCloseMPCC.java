package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author chris_00 close the CommandChannel Information window
 */
public class ExCloseMPCC extends AServerPacket
{
	public static final ExCloseMPCC STATIC_PACKET = new ExCloseMPCC();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x26);
	}
}
