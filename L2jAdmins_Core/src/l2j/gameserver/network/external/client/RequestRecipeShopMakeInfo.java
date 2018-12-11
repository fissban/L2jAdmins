package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.RecipeShopItemInfo;

/**
 * This class ... cdd
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRecipeShopMakeInfo extends AClientPacket
{
	private int playerObjectId;
	private int recipeId;
	
	@Override
	protected void readImpl()
	{
		playerObjectId = readD();
		recipeId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		player.sendPacket(new RecipeShopItemInfo(playerObjectId, recipeId));
	}
}
