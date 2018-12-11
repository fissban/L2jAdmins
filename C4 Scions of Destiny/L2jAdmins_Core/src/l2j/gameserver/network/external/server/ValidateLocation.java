package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * 0000: 76 7a 07 80 49 ea 01 00 00 c1 37 fe uz..Ic'.J.....7.
 * <p>
 * 0010: ff 9e c3 03 00 8f f3 ff ff .........
 * <p>
 * <p>
 * format dddddd (player id, target id, distance, startx, starty, startz)
 * <p>
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ValidateLocation extends AServerPacket
{
	private final L2Character cha;
	
	public ValidateLocation(L2Character cha)
	{
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x61);
		
		writeD(cha.getObjectId());
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
		writeD(cha.getHeading());
	}
}
