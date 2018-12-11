package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AllyCrest;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAllyCrest extends AClientPacket
{
	private int crestId;
	
	@Override
	protected void readImpl()
	{
		crestId = readD();
	}
	
	@Override
	public void runImpl()
	{
		byte[] data = CrestData.getCrest(CrestType.ALLY, crestId);
		if (data != null)
		{
			sendPacket(new AllyCrest(crestId, data));
		}
	}
}
