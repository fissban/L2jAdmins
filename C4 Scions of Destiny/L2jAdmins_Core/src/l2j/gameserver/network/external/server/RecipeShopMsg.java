package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeShopMsg extends AServerPacket
{
	private final L2PcInstance cha;
	
	public RecipeShopMsg(L2PcInstance player)
	{
		cha = player;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xdb);
		writeD(cha.getObjectId());
		writeS(cha.getPrivateStore().getCreateList().getStoreName()); // cha.getTradeList().getSellStoreName());
	}
}
