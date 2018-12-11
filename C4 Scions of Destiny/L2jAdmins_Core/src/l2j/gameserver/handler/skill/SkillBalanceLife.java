package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

/**
 * @author earendil
 */
public class SkillBalanceLife implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.BALANCE_LIFE
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var fullHP = 0;
		var currentHPs = 0;
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			
			// We should not heal if char is dead
			if ((target == null) || target.isDead())
			{
				continue;
			}
			
			fullHP += target.getStat().getMaxHp();
			currentHPs += target.getCurrentHp();
			
			var percentHP = currentHPs / fullHP;
			var newHP = target.getStat().getMaxHp() * percentHP;
			var totalHeal = newHP - target.getCurrentHp();
			
			target.setCurrentHp(newHP);
			
			if (totalHeal > 0)
			{
				target.setLastHealAmount((int) totalHeal);
			}
			
			var statusUpdate = new StatusUpdate(target.getObjectId());
			statusUpdate.addAttribute(StatusUpdateType.CUR_HP, (int) target.getCurrentHp());
			target.sendPacket(statusUpdate);
			
			target.sendMessage("HP of the party has been balanced.");
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
