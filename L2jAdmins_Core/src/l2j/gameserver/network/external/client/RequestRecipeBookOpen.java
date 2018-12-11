package l2j.gameserver.network.external.client;

import l2j.gameserver.model.recipes.RecipeController;
import l2j.gameserver.network.AClientPacket;

public class RequestRecipeBookOpen extends AClientPacket
{
	private boolean isDwarvenCraft;
	
	@Override
	protected void readImpl()
	{
		isDwarvenCraft = (readD() == 0);
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		if (getClient().getActiveChar().getPrivateStore().isInStoreMode())
		{
			getClient().getActiveChar().sendMessage("Cannot use recipe book while trading.");
			return;
		}
		
		RecipeController.getInstance().requestBookOpen(getClient().getActiveChar(), isDwarvenCraft);
	}
}
