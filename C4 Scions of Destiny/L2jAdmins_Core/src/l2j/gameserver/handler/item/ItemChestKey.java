package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2ChestInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author AlterEgo
 */
public class ItemChestKey implements IItemHandler
{
	public static final int INTERACTION_DISTANCE = 100;
	
	private static int[] ITEM_IDS =
	{
		/*
		 * C4 UNUSED: chest key 5197, 5198, 5199, 5200, 5201, 5202, 5203, 5204,
		 */
		// deluxe key
		6665,
		6666,
		6667,
		6668,
		6669,
		6670,
		6671,
		6672
	};
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		L2Object target = activeChar.getTarget();
		
		if ((target == null) || !(target instanceof L2ChestInstance))
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			L2ChestInstance chest = (L2ChestInstance) target;
			if (chest.isDead() || chest.isInteracted())
			{
				activeChar.sendMessage("The chest is empty.");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// box key skill
			activeChar.useMagic(SkillData.getInstance().getSkill(2229, item.getId() - 6664), false, false);
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
