package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 * @author  godson
 */
public class ExOlympiadMode extends AServerPacket
{
	private final int mode;
	
	/**
	 * @param mode (0 = return, 1 = side 1, 2 = side 2, 3 = spectate)
	 */
	public ExOlympiadMode(int mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2b);
		writeC(mode);
	}
}
