package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * sample
 * <p>
 * 7d c1 b2 e0 4a 00 00 00 00
 * <p>
 * format cdd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinAlly extends AServerPacket
{
	private L2PcInstance requestor;
	
	/**
	 * @param requestor
	 */
	public AskJoinAlly(L2PcInstance requestor)
	{
		this.requestor = requestor;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa8);
		writeD(requestor.getObjectId());
		writeS(requestor.getName());
		writeS("");
		writeS(requestor.getClan().getAllyName());
	}
}
