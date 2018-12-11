package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * 0000: 01 7a 73 10 4c b2 0b 00 00 a3 fc 00 00 e8 f1 ff .zs.L........... 0010: ff bd 0b 00 00 b3 fc 00 00 e8 f1 ff ff ............. ddddddd
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class CharMoveToLocation extends AServerPacket
{
	private final int objectId, x, y, z, xDst, yDst, zDst;
	
	public CharMoveToLocation(L2Character cha)
	{
		objectId = cha.getObjectId();
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
		xDst = cha.getXdestination();
		yDst = cha.getYdestination();
		zDst = cha.getZdestination();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x01);
		
		writeD(objectId);
		
		writeD(xDst);
		writeD(yDst);
		writeD(zDst);
		
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
