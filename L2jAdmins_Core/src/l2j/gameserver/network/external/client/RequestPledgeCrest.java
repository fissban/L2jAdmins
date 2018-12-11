package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PledgeCrest;

/**
 * This class ...
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeCrest extends AClientPacket
{
	private int crestId;
	
	@Override
	protected void readImpl()
	{
		crestId = 0;
		try
		{
			crestId = readD();
		}
		catch (Exception e)
		{
		}
	}
	
	@Override
	public void runImpl()
	{
		if (crestId == 0)
		{
			return;
		}
		
		if (Config.DEBUG)
		{
			LOG.fine("crestid " + crestId + " requested");
		}
		
		byte[] data = CrestData.getCrest(CrestType.PLEDGE, crestId);
		if (data != null)
		{
			sendPacket(new PledgeCrest(crestId, data));
		}
		else
		{
			if (Config.DEBUG)
			{
				LOG.fine("crest is missing:" + crestId);
			}
		}
	}
}
