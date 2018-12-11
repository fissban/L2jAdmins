package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AllyInfo;

/**
 * This class ...
 * @version $Revision: 1479 $ $Date: 2005-11-09 00:47:42 +0100 (mer., 09 nov. 2005) $
 */
public class RequestAllyInfo extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		AllyInfo ai = new AllyInfo(getClient().getActiveChar());
		sendPacket(ai);
	}
}
