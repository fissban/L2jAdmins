package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.RecipeItemMakeInfo;

/**
 */
public class RequestRecipeItemMakeInfo extends AClientPacket
{
	private int recipeId;
	
	@Override
	protected void readImpl()
	{
		recipeId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		sendPacket(new RecipeItemMakeInfo(recipeId, activeChar));
	}
}
