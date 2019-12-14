package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class SkillCreateItem implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.CREATE_ITEM
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		var player = (L2PcInstance) activeChar;
		
		if (player.isAlikeDead())
		{
			return;
		}
		
		if ((skill.getCreateItemId() == 0) || (skill.getCreateItemCount() == 0))
		{
			player.sendPacket(SystemMessage.S1_NOT_AVAILABLE);
			return;
		}
		
		giveItems(player, skill.getCreateItemId(), skill.getCreateItemCount() * (Rnd.nextInt(skill.getRandomCount()) + 1));
		
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param itemId
	 * @param count
	 */
	private static void giveItems(L2PcInstance activeChar, int itemId, int count)
	{
		var item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
		item.setCount(count);
		activeChar.getInventory().addItem("Skill", item, activeChar, activeChar);
		
		if (count > 1)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(item.getId()).addNumber(count));
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(item.getId()));
		}
		
		activeChar.sendPacket(new ItemList(activeChar, false));
	}
}
