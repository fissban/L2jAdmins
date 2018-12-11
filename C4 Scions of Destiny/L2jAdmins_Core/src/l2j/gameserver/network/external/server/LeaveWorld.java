package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class LeaveWorld extends AServerPacket
{
	public static final LeaveWorld STATIC_PACKET = new LeaveWorld();
	
	@Override
	public void writeImpl()
	{
		writeC(0x7e);
	}
}
