package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.2.2.2.7 $ $Date: 2005/04/05 19:41:13 $
 */
public class ItemScrollOfResurrection implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			737,
			3936,
			3959,
			6387
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
		
		if (activeChar.isAllSkillsDisabled())
		{
			return;
		}
		
		L2Character target = (L2Character) activeChar.getTarget();
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return;
		}
		
		if (!target.isDead())
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return;
		}
		
		if (!(target instanceof L2PcInstance) && !(target instanceof L2PetInstance))
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return;
		}
		
		if (activeChar.isSitting())
		{
			return;
		}
		
		if (activeChar.isFestivalParticipant())
		{
			activeChar.sendMessage("Resurrection inside festival is prohibited.");
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("You cannot use this item during an Olympiad match.");
			return;
		}
		
		int skillId = 0;
		
		switch (item.getId())
		{
			case 737:
				skillId = 2014; // Scroll of Resurrection
				break;
			case 3936:
				skillId = 2049; // Blessed Scroll of Resurrection
				break;
			case 3959:
				skillId = 2062; // L2Day - Blessed Scroll of Resurrection
				break;
			case 6387:
				if (!(target instanceof L2PetInstance))
				{
					return;
				}
				skillId = 2179; // Blessed Scroll of Resurrection: For Pets
				break;
		}
		
		if (!activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, true))
		{
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(skillId, 1);
		
		if (skill != null)
		{
			activeChar.useMagic(skill, true, true);
		}
	}
}
