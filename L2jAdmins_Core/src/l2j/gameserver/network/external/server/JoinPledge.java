package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class JoinPledge extends AServerPacket
{
	private final int pledgeId;
	
	public JoinPledge(int pledgeId)
	{
		this.pledgeId = pledgeId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x33);
		writeD(pledgeId);
	}
}
