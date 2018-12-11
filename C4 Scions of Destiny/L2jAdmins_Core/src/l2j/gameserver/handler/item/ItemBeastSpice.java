package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2FeedableBeastInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemBeastSpice implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			6643, // Golden Spice
			6644,// Crystal Spice
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			playable.sendPacket(SystemMessage.ITEM_NOT_FOR_PETS);
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (!(activeChar.getTarget() instanceof L2FeedableBeastInstance))
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return;
		}
		
		int itemId = item.getId();
		
		if (itemId == 6643) // Golden Spice
		{
			activeChar.useMagic(SkillData.getInstance().getSkill(2188, 1), false, false);
		}
		else if (itemId == 6644) // Crystal Spice
		{
			activeChar.useMagic(SkillData.getInstance().getSkill(2189, 1), false, false);
		}
	}
}
