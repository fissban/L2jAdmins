package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PrivateStoreMsgSell extends AServerPacket
{
	private final L2PcInstance player;
	private String storeMsg;
	
	public PrivateStoreMsgSell(L2PcInstance player)
	{
		this.player = player;
		if (player.getPrivateStore().getSellList() != null)
		{
			storeMsg = player.getPrivateStore().getSellList().getTitle();
		}
		else
		{
			storeMsg = "";
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x9c);
		writeD(player.getObjectId());
		
		writeS(storeMsg);
	}
}
