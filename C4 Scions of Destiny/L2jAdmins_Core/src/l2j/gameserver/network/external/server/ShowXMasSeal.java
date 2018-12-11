package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowXMasSeal extends AServerPacket
{
	private final int item;
	
	public ShowXMasSeal(int item)
	{
		this.item = item;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xF2);
		writeD(item);
	}
}
