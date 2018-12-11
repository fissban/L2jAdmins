package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AServerPacket;

/**
 * ddddd
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopItemInfo extends AServerPacket
{
	
	private final int shopId;
	private final int recipeId;
	
	public RecipeShopItemInfo(int shopId, int recipeId)
	{
		this.shopId = shopId;
		this.recipeId = recipeId;
	}
	
	@Override
	public void writeImpl()
	{
		if (!(L2World.getInstance().getObject(shopId) instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().getObject(shopId);
		writeC(0xda);
		writeD(shopId);
		writeD(recipeId);
		writeD(manufacturer != null ? (int) manufacturer.getCurrentMp() : 0);
		writeD(manufacturer != null ? manufacturer.getStat().getMaxMp() : 0);
		writeD(0xffffffff);
	}
}
