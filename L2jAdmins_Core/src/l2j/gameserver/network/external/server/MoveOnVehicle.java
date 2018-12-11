package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class MoveOnVehicle extends AServerPacket
{
	private final int id;
	private final int x, y, z;
	private final L2PcInstance player;
	
	public MoveOnVehicle(int vehicleID, L2PcInstance player, int x, int y, int z)
	{
		id = vehicleID;
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x71);
		
		writeD(player.getObjectId());
		writeD(id);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(player.getX());
		writeD(player.getY());
		writeD(player.getZ());
	}
}
