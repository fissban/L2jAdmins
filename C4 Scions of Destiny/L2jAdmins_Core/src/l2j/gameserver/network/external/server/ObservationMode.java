package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ObservationMode extends AServerPacket
{
	// ddSS
	private final int x, y, z;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public ObservationMode(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xdf);
		writeD(x);
		writeD(y);
		writeD(z);
		writeC(0x00);
		writeC(0xc0);
		writeC(0x00);
	}
}
