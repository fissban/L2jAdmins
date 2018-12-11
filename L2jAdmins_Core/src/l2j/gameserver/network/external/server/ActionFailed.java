package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ActionFailed extends AServerPacket
{
	/**
	 * Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
	 */
	public static final ActionFailed STATIC_PACKET = new ActionFailed();
	
	@Override
	public void writeImpl()
	{
		writeC(0x25);
	}
}
