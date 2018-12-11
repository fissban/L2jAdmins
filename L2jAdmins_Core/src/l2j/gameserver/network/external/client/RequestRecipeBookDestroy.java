package l2j.gameserver.network.external.client;

import l2j.gameserver.data.RecipeData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.RecipeBookItemList;

public class RequestRecipeBookDestroy extends AClientPacket
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
		if (activeChar != null)
		{
			RecipeList rp = RecipeData.getRecipeList(recipeId - 1);
			if (rp == null)
			{
				return;
			}
			
			activeChar.unregisterRecipeList(recipeId);
			
			RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), activeChar.getStat().getMaxMp());
			if (rp.isDwarvenRecipe())
			{
				response.addRecipes(activeChar.getDwarvenRecipeBookList());
			}
			else
			{
				response.addRecipes(activeChar.getCommonRecipeBookList());
			}
			
			activeChar.sendPacket(response);
		}
	}
}
