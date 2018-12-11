package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * 0000: 75 7a 07 80 49 63 27 00 4a ea 01 00 00 c1 37 fe uz..Ic'.J.....7.
 * <p>
 * 0010: ff 9e c3 03 00 8f f3 ff ff .........
 * <p>
 * <p>
 * format dddddd (player id, target id, distance, startx, starty, startz)
 * <p>
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/04/06 16:13:46 $
 */
public class MoveToPawn extends AServerPacket
{
	private final int chaObjId;
	private final int targetObjId;
	private final int distance;
	private final int x, y, z, tx, ty, tz;
	
	public MoveToPawn(L2Character cha, L2Character target, int distance)
	{
		chaObjId = cha.getObjectId();
		targetObjId = target.getObjectId();
		this.distance = distance;
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
		
		tx = target.getX();
		ty = target.getY();
		tz = target.getZ();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x60);
		
		writeD(chaObjId);
		writeD(targetObjId);
		writeD(distance);
		
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(tx);
		writeD(ty);
		writeD(tz);
	}
}
