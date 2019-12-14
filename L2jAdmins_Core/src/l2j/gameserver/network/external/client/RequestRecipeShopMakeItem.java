package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.model.recipes.RecipeController;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.util.Util;

/**
 * @author Administrator
 */
public class RequestRecipeShopMakeItem extends AClientPacket
{
	private int targetId;
	private int recipeId;
	@SuppressWarnings("unused")
	private int unknow;
	
	@Override
	protected void readImpl()
	{
		targetId = readD();
		recipeId = readD();
		unknow = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().getObject(targetId);
		
		if (manufacturer == null)
		{
			return;
		}
		
		if (activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendMessage("Cannot make items while trading.");
			return;
		}
		
		if (manufacturer.getPrivateStore().getStoreType() != PcStoreType.MANUFACTURE)
		{
			// activeChar.sendMessage("Cannot make items while trading.");
			return;
		}
		
		if (activeChar.getPrivateStore().isInCraftMode() || manufacturer.getPrivateStore().isInCraftMode())
		{
			activeChar.sendMessage("Currently in Craft Mode.");
			return;
		}
		
		if (Util.checkIfInRange(150, activeChar, manufacturer, true))
		{
			RecipeController.getInstance().requestManufactureItem(manufacturer, recipeId, activeChar);
		}
	}
}
