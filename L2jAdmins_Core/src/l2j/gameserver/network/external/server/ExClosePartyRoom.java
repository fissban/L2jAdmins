package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author Gnacik
 */
public class ExClosePartyRoom extends AServerPacket
{
	public static final ExClosePartyRoom STATIC_PACKET = new ExClosePartyRoom();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x0f);
	}
}
