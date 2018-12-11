package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 60 d6 6d c0 4b door id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 00 00 00 00 ?? format dddd rev 377 ID:%d X:%d Y:%d Z:%d ddddd rev 419
 * @version $Revision: 1.3.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class DoorInfo extends AServerPacket
{
	private final L2DoorInstance door;
	
	public DoorInfo(L2DoorInstance door)
	{
		this.door = door;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x4c);
		writeD(door.getObjectId());
		writeD(door.getId());
	}
}
