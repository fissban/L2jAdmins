package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * This class ...
 * @author  DrLecter
 * @version 0.1 10/08/05 0039 Created for quest items with special needs. Mainly the c3 quests.
 */
public class ItemAdvQuestItems implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5944,
			5955,
			5966,
			5967,
			5968,
			5969,
			6007,
			6008,
			6009,
			6010
		};
	}
	
	private static final int[] AWARD_PARCH =
	{
		5922,
		5923,
		5924,
		5925,
		5926,
		5927,
		5928,
		5929,
		5930,
		5931,
		5932,
		5933,
		5934,
		5935,
		5936,
		5937,
		5938,
		5939,
		5940,
		5941,
		5942,
		5943
	};
	private static final int[] AWARD_GBOOK =
	{
		5942,
		5943,
		5945,
		5946,
		5947,
		5948,
		5949,
		5950,
		5951,
		5952,
		5953,
		5954
	};
	private static final int[] AWARD_PAPY1 =
	{
		5970,
		5971,
		5977,
		5978,
		5979,
		5986,
		5993,
		5994,
		5995,
		5997,
		5983,
		6001
	};
	private static final int[] AWARD_PAPY2 =
	{
		5970,
		5971,
		5975,
		5976,
		5980,
		5985,
		5993,
		5994,
		5995,
		5997,
		5983,
		6001
	};
	private static final int[] AWARD_PAPY3 =
	{
		5973,
		5974,
		5981,
		5984,
		5989,
		5990,
		5991,
		5992,
		5996,
		5998,
		5999,
		6000,
		5988,
		5983,
		6001
	};
	private static final int[] AWARD_PAPY4 =
	{
		5970,
		5971,
		5982,
		5987,
		5989,
		5990,
		5991,
		5992,
		5996,
		5998,
		5999,
		6000,
		5972,
		6001
	};
	private static final int[] AWARD_POUCH =
	{
		6011,
		6012,
		6013,
		6014,
		6015,
		6016,
		6018,
		6019,
		6020
	};
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		L2PcInstance activeChar = (L2PcInstance) playable;
		int itemId = item.getId();
		int itemToCreateId = itemId;
		
		switch (itemId)
		{
			case 5944:
				itemToCreateId = AWARD_PARCH[Rnd.nextInt(AWARD_PARCH.length)];
				break;
			case 5955:
				itemToCreateId = AWARD_GBOOK[Rnd.nextInt(AWARD_GBOOK.length)];
				break;
			case 5966:
				itemToCreateId = AWARD_PAPY1[Rnd.nextInt(AWARD_PAPY1.length)];
				break;
			case 5967:
				itemToCreateId = AWARD_PAPY2[Rnd.nextInt(AWARD_PAPY2.length)];
				break;
			case 5968:
				itemToCreateId = AWARD_PAPY3[Rnd.nextInt(AWARD_PAPY3.length)];
				break;
			case 5969:
				itemToCreateId = AWARD_PAPY4[Rnd.nextInt(AWARD_PAPY4.length)];
				break;
			case 6007:
			case 6008:
			case 6009:
			case 6010:
				itemToCreateId = AWARD_POUCH[Rnd.nextInt(AWARD_POUCH.length)];
				break;
		}
		
		activeChar.getInventory().destroyItemByItemId("Extract", itemId, 1, activeChar, null);
		activeChar.getInventory().addItem("Extract", itemToCreateId, 1, activeChar, item);
		activeChar.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemToCreateId).addNumber(1));
		activeChar.sendPacket(new ItemList(activeChar, false));
	}
}
