package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * format ddddd sample 0000: 39 0b 07 10 48 3e 31 10 48 3a f6 00 00 91 5b 00 9...H>1.H:....[. 0010: 00 4c f1 ff ff .L...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TargetSelected extends AServerPacket
{
	private final int objectId;
	private final int targetId;
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * @param objectId
	 * @param targetId
	 * @param x
	 * @param y
	 * @param z
	 */
	public TargetSelected(int objectId, int targetId, int x, int y, int z)
	{
		this.objectId = objectId;
		this.targetId = targetId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x29);
		writeD(objectId);
		writeD(targetId);
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
