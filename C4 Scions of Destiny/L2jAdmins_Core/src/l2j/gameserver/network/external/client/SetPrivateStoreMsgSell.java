package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PrivateStoreMsgSell;

/**
 * This class ...
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreMsgSell extends AClientPacket
{
	private String storeMsg;
	
	@Override
	protected void readImpl()
	{
		storeMsg = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((player == null) || (player.getPrivateStore().getSellList() == null))
		{
			return;
		}
		
		player.getPrivateStore().getSellList().setTitle(storeMsg);
		sendPacket(new PrivateStoreMsgSell(player));
	}
}
