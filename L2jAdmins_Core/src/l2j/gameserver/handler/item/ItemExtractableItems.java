package l2j.gameserver.handler.item;

import java.util.logging.Logger;

import l2j.gameserver.data.ExtractableItemsData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ExtractableItemHolder;
import l2j.gameserver.model.holder.ExtractableProductItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author FBIagent 11/12/2006
 */
public class ItemExtractableItems implements IItemHandler
{
	private static final Logger LOG = Logger.getLogger(ItemData.class.getName());
	
	@Override
	public int[] getItemIds()
	{
		return ExtractableItemsData.getInstance().itemIDs();
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		final L2PcInstance activeChar = (L2PcInstance) playable;
		
		final int itemID = item.getId();
		final ExtractableItemHolder exitem = ExtractableItemsData.getInstance().getExtractableItem(itemID);
		
		if (exitem == null)
		{
			return;
		}
		
		int createItemID = 0;
		int createAmount = 0;
		final int rndNum = Rnd.get(100);
		int chanceFrom = 0;
		
		// calculate extraction
		for (final ExtractableProductItemHolder expi : exitem.getProductItems())
		{
			final int chance = expi.getChance();
			
			if ((rndNum >= chanceFrom) && (rndNum <= (chance + chanceFrom)))
			{
				createItemID = expi.getId();
				createAmount = expi.getAmmount();
				break;
			}
			
			chanceFrom += chance;
		}
		
		if (createItemID <= 0)
		{
			activeChar.sendMessage("Nothing happened.");
			return;
		}
		
		if (ItemData.getInstance().createDummyItem(createItemID) == null)
		{
			LOG.warning("createItemID " + createItemID + " doesn't have template!");
			activeChar.sendMessage("Nothing happened.");
			return;
		}
		
		if (!activeChar.getInventory().destroyItemByItemId("Extract", itemID, 1, activeChar.getTarget(), true))
		{
			return;
		}
		
		if (ItemData.getInstance().createDummyItem(createItemID).isStackable())
		{
			activeChar.getInventory().addItem("Extract", createItemID, createAmount, item, false);
		}
		else
		{
			for (int i = 0; i < createAmount; i++)
			{
				activeChar.getInventory().addItem("Extract", createItemID, createAmount, item, false);
			}
		}
		
		SystemMessage sm;
		
		if (createAmount > 1)
		{
			sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(createItemID).addNumber(createAmount);
		}
		else
		{
			sm = new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(createItemID);
		}
		activeChar.sendPacket(sm);
	}
}
