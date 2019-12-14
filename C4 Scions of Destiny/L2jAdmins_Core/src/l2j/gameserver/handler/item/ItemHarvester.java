package l2j.gameserver.handler.item;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class ItemHarvester implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5125
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		if (CastleManorManager.getInstance().isDisabled())
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if ((activeChar.getTarget() == null) || !(activeChar.getTarget() instanceof L2MonsterInstance))
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2MonsterInstance target = (L2MonsterInstance) activeChar.getTarget();
		
		if ((target == null) || !target.isDead())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(2098, 1); // harvesting skill
		if (skill != null)
		{
			activeChar.useMagic(skill, false, false);
		}
		
	}
}
