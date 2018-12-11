package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinPledge extends AServerPacket
{
	private final int requestorId;
	private final String pledgeName;
	
	public AskJoinPledge(L2PcInstance requestor)
	{
		requestorId = requestor.getObjectId();
		pledgeName = requestor.getClan().getName();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x32);
		writeD(requestorId);
		writeS(pledgeName);
	}
}
