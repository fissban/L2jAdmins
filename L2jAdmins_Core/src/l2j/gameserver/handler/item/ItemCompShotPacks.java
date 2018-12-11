package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:30:07 $
 */
public class ItemCompShotPacks implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5134,
			5135,
			5136,
			5137,
			5138,
			5139,
			5140,
			5141,
			5142,
			5143,
			5144,
			5145,
			5250,
			5251,
			5252,
			5253,
			5254,
			5255,
			5256,
			5257,
			5258,
			5259,
			5260,
			5261,
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		int itemId = item.getId();
		int itemToCreateId = 0;
		int amount = 0; // default regular pack
		
		if ((itemId >= 5134) && (itemId <= 5139)) // SS
		{
			if (itemId == 5134)
			{
				itemToCreateId = 1835;
			}
			else
			{
				itemToCreateId = itemId - 3672;
			}
			
			amount = 300;
		}
		else if ((itemId >= 5140) && (itemId <= 5145)) // SpS
		{
			itemToCreateId = itemId - 2631;
			amount = 300;
		}
		else if ((itemId >= 5250) && (itemId <= 5255)) // Greater SS
		{
			if (itemId == 5250)
			{
				itemToCreateId = 1835;
			}
			else
			{
				itemToCreateId = itemId - 3788;
			}
			
			amount = 1000;
		}
		else if ((itemId >= 5256) && (itemId <= 5261)) // Greater SpS
		{
			itemToCreateId = itemId - 2747;
			amount = 1000;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		activeChar.getInventory().destroyItem("Extract", item, activeChar, null);
		activeChar.getInventory().addItem("Extract", itemToCreateId, amount, activeChar, item);
		activeChar.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemToCreateId).addNumber(amount));
		activeChar.sendPacket(new ItemList(activeChar, false));
	}
}
