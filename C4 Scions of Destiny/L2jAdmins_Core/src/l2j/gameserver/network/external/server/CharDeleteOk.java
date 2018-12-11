package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class CharDeleteOk extends AServerPacket
{
	public static final CharDeleteOk STATIC_PACKET = new CharDeleteOk();
	
	@Override
	public final void writeImpl()
	{
		writeC(0x23);
	}
}
