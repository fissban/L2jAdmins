package l2j.gameserver.handler.item;

import l2j.gameserver.data.RecipeData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.2.5.2.5 $ $Date: 2005/04/06 16:13:51 $
 */
public class ItemRecipes implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return RecipeData.getAllRecipeds();
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		RecipeList rp = RecipeData.getRecipeByItemId(item.getId());
		if (rp == null)
		{
			return;
		}
		
		if (activeChar.hasRecipeList(rp.getId()))
		{
			activeChar.sendPacket(SystemMessage.RECIPE_ALREADY_REGISTERED);
		}
		else
		{
			if (rp.isDwarvenRecipe())
			{
				if (activeChar.hasDwarvenCraft())
				{
					if (rp.getLevel() > activeChar.getDwarvenCraft())
					{
						// can't add recipe, because create item level too low
						activeChar.sendPacket(SystemMessage.CREATE_LVL_TOO_LOW_TO_REGISTER);
					}
					else if (activeChar.getDwarvenRecipeBookList().size() >= activeChar.getDwarfRecipeLimit())
					{
						// Up to $s1 recipes can be registered.
						activeChar.sendPacket(new SystemMessage(SystemMessage.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(activeChar.getDwarfRecipeLimit()));
					}
					else
					{
						activeChar.registerDwarvenRecipeList(rp);
						activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_ADDED).addItemName(item.getId()));
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessage.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
				}
			}
			else
			{
				if (activeChar.hasCommonCraft())
				{
					if (rp.getLevel() > activeChar.getCommonCraft())
					{
						// can't add recipe, because create item level too low
						activeChar.sendPacket(SystemMessage.CREATE_LVL_TOO_LOW_TO_REGISTER);
					}
					else if (activeChar.getCommonRecipeBookList().size() >= activeChar.getCommonRecipeLimit())
					{
						// Up to $s1 recipes can be registered.
						activeChar.sendPacket(new SystemMessage(SystemMessage.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(activeChar.getCommonRecipeLimit()));
					}
					else
					{
						activeChar.registerCommonRecipeList(rp);
						activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_ADDED).addItemName(item.getId()));
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessage.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
				}
			}
		}
	}
}
