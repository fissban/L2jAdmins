package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.network.AServerPacket;

/**
 * format dddd sample 0000: 3a 69 08 10 48 02 c1 00 00 f7 56 00 00 89 ea ff :i..H.....V..... 0010: ff 0c b2 d8 61 ....a
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TeleportToLocation extends AServerPacket
{
	private final int targetId;
	private final int x;
	private final int y;
	private final int z;
	
	public TeleportToLocation(L2Object cha, int x, int y, int z)
	{
		targetId = cha.getObjectId();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x28);
		writeD(targetId);
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
