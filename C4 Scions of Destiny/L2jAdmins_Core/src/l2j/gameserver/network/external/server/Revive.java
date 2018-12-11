package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 0c 9b da 12 40 ....@ format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:40 $
 */
public class Revive extends AServerPacket
{
	private final int objectId;
	
	public Revive(L2Object obj)
	{
		objectId = obj.getObjectId();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x07);
		writeD(objectId);
	}
}
