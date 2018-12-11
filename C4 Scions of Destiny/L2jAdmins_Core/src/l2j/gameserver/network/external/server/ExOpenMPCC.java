package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author chris_00 opens the CommandChannel Information window
 */
public class ExOpenMPCC extends AServerPacket
{
	public static final ExOpenMPCC STATIC_PACKET = new ExOpenMPCC();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x25);
	}
}
