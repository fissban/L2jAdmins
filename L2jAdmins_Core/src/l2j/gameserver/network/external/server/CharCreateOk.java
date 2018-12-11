package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharCreateOk extends AServerPacket
{
	public static final CharCreateOk STATIC_PACKET = new CharCreateOk();
	
	@Override
	public void writeImpl()
	{
		writeC(0x19);
		writeD(0x01);
	}
}
