package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class SunRise extends AServerPacket
{
	public static final SunRise STATIC_PACKET = new SunRise();
	
	@Override
	public void writeImpl()
	{
		writeC(0x1c);
	}
}
