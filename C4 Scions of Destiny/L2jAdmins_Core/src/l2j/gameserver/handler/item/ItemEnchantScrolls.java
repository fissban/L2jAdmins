package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ChooseInventoryItem;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemEnchantScrolls implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			// S grade
			959,
			960,
			961,
			962,
			6577,
			6578,
			// A grade
			729,
			730,
			731,
			732,
			6569,
			6570,
			// B grade
			947,
			948,
			949,
			950,
			6571,
			6572,
			// C grade
			951,
			952,
			953,
			954,
			6573,
			6574,
			// D grade
			955,
			956,
			957,
			958,
			6575,
			6576,
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (activeChar.isCastingNow())
		{
			return;
		}
		
		activeChar.setActiveEnchantItem(item);
		activeChar.sendPacket(SystemMessage.SELECT_ITEM_TO_ENCHANT);
		activeChar.sendPacket(new ChooseInventoryItem(item.getId()));
		return;
	}
}
