package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ChairSit extends AServerPacket
{
	private final int activeCharObjId;
	private final int staticObjId;
	
	/**
	 * @param player
	 * @param staticObjectId
	 */
	public ChairSit(L2PcInstance player, int staticObjectId)
	{
		activeCharObjId = player.getObjectId();
		staticObjId = staticObjectId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe1);
		writeD(activeCharObjId);
		writeD(staticObjId);
	}
}
