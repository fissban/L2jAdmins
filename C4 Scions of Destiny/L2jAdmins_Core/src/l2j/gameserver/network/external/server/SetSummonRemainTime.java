package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * format (c) dd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:40 $
 */
public class SetSummonRemainTime extends AServerPacket
{
	private final int maxTime;
	private final int remainingTime;
	
	public SetSummonRemainTime(int maxTime, int remainingTime)
	{
		this.remainingTime = remainingTime;
		this.maxTime = maxTime;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xd1);
		writeD(maxTime);
		writeD(remainingTime);
	}
}
