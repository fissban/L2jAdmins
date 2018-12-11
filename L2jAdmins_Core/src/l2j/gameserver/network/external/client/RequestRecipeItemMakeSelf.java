package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.recipes.RecipeController;
import l2j.gameserver.network.AClientPacket;

/**
 * @author Administrator
 */
public class RequestRecipeItemMakeSelf extends AClientPacket
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
		
		if (activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendMessage("Cannot make items while trading.");
			return;
		}
		
		if (activeChar.getPrivateStore().isInCraftMode())
		{
			activeChar.sendMessage("Currently in Craft Mode.");
			return;
		}
		
		RecipeController.getInstance().requestMakeItem(activeChar, recipeId);
	}
}
