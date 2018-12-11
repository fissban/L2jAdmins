package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ItemList;

/**
 * This class ...
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestItemList extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		if ((getClient() != null) && (getClient().getActiveChar() != null) && !getClient().getActiveChar().isInventoryDisabled())
		{
			sendPacket(new ItemList(getClient().getActiveChar(), true));
		}
	}
}
