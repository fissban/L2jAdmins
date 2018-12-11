package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 1e 9b da 12 40 ....@ format d
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class DeleteObject extends AServerPacket
{
	private final int objectId;
	
	public DeleteObject(L2Object obj)
	{
		objectId = obj.getObjectId();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x12);
		writeD(objectId);
		writeD(0x00); // c2
	}
}
