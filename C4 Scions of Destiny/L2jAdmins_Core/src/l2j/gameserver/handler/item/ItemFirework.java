package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.0.0.0.0.0 $ $Date: 2005/09/02 19:41:13 $
 */
public class ItemFirework implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			6403,
			6406,
			6407
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return; // prevent Class cast exception
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		int itemId = item.getId();
		
		if (!activeChar.tryToUseAction(FloodProtectorType.FIREWORK))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addItemName(itemId));
			return;
		}
		
		int skillId = 0;
		
		switch (itemId)
		{
			case 6403:// elven_firecracker, xml: 2023
				skillId = 2023;
				break;
			case 6406:// firework, xml: 2024
				skillId = 2024;
				break;
			case 6407:// large_firework, xml: 2025
				skillId = 2025;
				break;
		}
		
		activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, skillId, 1, 1, 0));
		
		Skill skill = SkillData.getInstance().getSkill(skillId, 1);
		if (skill != null)
		{
			if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				return;
			}
			
			activeChar.useMagic(skill, false, false);
		}
	}
}
