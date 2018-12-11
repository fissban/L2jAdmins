package l2j.gameserver.network.external.client;

import l2j.gameserver.data.GmListData;
import l2j.gameserver.network.AClientPacket;

/**
 * This class handles RequestGmLista packet triggered by /gmlist command
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGmList extends AClientPacket
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
		
		GmListData.getInstance().sendListToPlayer(getClient().getActiveChar());
	}
}
