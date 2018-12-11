package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.SystemMessage;

public class SkillCombatPointHeal implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.COMBATPOINTHEAL
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		for (var target2 : targets)
		{
			if (!(target2 instanceof L2Character))
			{
				continue;
			}
			
			var target = (L2Character) target2;
			
			if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			var cp = skill.getPower();
			
			target.sendPacket(new SystemMessage(SystemMessage.S1_CP_WILL_BE_RESTORED).addNumber((int) cp));
			target.setCurrentCp(cp + target.getCurrentCp());
			
			var statusUpdate = new StatusUpdate(target.getObjectId());
			statusUpdate.addAttribute(StatusUpdateType.CUR_CP, (int) target.getCurrentCp());
			target.sendPacket(statusUpdate);
			
			var bss = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
			var sps = activeChar.isChargedShot(ShotType.SPIRITSHOTS);
			
			activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
