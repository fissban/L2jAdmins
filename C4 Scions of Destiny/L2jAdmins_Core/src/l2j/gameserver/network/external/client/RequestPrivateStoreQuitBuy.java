package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreQuitBuy extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		player.getPrivateStore().setStoreType(PcStoreType.NONE);
		player.standUp();
		player.broadcastUserInfo();
	}
}
