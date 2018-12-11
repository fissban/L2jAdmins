package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * format ddddd sample 0000: 59 1a 95 20 48 44 17 02 00 03 f0 fc ff 98 f1 ff Y.. HD.......... 0010: ff c1 1a 00 00 .....
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class StopMove extends AServerPacket
{
	private final int objectId;
	private final int x;
	private final int y;
	private final int z;
	private final int heading;
	
	public StopMove(L2Character cha)
	{
		this(cha.getObjectId(), cha.getX(), cha.getY(), cha.getZ(), cha.getHeading());
	}
	
	public StopMove(int objectId, int x, int y, int z, int heading)
	{
		this.objectId = objectId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x47);
		writeD(objectId);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(heading);
	}
}
