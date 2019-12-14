package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.PrivateStoreManageListSell;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreManageSell extends AClientPacket
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
		// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
		if (player.isAlikeDead())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isMounted())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((player.getPrivateStore().getStoreType() == PcStoreType.SELL) || (player.getPrivateStore().getStoreType() == (PcStoreType.SELL_MANAGE)) || (player.getPrivateStore().getStoreType() == PcStoreType.PACKAGE_SELL))
		{
			player.getPrivateStore().setStoreType(PcStoreType.NONE);
		}
		
		if (player.getPrivateStore().getStoreType() == PcStoreType.NONE)
		{
			if (player.isSitting())
			{
				player.standUp();
			}
			player.getPrivateStore().setStoreType(PcStoreType.SELL_MANAGE);
			player.sendPacket(new PrivateStoreManageListSell(player));
		}
	}
}
