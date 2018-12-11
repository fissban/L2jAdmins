package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ShowMiniMap;

/**
 * sample format d
 * @version $Revision: 1 $ $Date: 2005/04/10 00:17:44 $
 */
public class RequestShowMiniMap extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.sendPacket(new ShowMiniMap(1665));
	}
}
