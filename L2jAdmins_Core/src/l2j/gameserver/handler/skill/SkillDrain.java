package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

/**
 * @author fissban
 */
public class SkillDrain implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.DRAIN
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		var sps = activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		var bss = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			if (target.isAlikeDead() && (skill.getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
			{
				continue;
			}
			
			// No effect on invulnerable chars unless they cast it themselves.
			if ((activeChar != target) && target.isInvul())
			{
				continue;
			}
			
			var mcrit = Formulas.calcMCrit(activeChar.getStat().getMCriticalHit(target, skill));
			var damage = (int) Formulas.calcMagicDam(activeChar, target, skill, sps, bss, mcrit);
			
			var drain = 0;
			var currentCp = (int) target.getCurrentCp();
			var currentHp = (int) target.getCurrentHp();
			
			if (currentCp > 0)
			{
				if (damage < currentCp)
				{
					drain = 0;
				}
				else
				{
					drain = damage - currentCp;
				}
			}
			
			else if (damage > currentHp)
			{
				drain = currentHp;
			}
			else
			{
				drain = damage;
			}
			
			var hpAdd = skill.getAbsorbAbs() + (skill.getAbsorbPart() * drain);
			var hp = ((activeChar.getCurrentHp() + hpAdd) > activeChar.getStat().getMaxHp() ? activeChar.getStat().getMaxHp() : (activeChar.getCurrentHp() + hpAdd));
			
			activeChar.setCurrentHp(hp);
			
			var statusUpdate = new StatusUpdate(activeChar.getObjectId());
			statusUpdate.addAttribute(StatusUpdateType.CUR_HP, (int) hp);
			activeChar.sendPacket(statusUpdate);
			
			// Check to see if we should damage the target
			if ((damage > 0) && (!target.isDead() || (skill.getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)))
			{
				target.reduceCurrentHp(damage, activeChar);
				
				if (Formulas.calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}
				
				activeChar.sendDamageMessage(target, damage, mcrit, false, false);
			}
			
			// Check to see if we should do the decay right after the cast
			if (target.isDead() && (skill.getTargetType() == SkillTargetType.TARGET_CORPSE_MOB) && (target instanceof L2Npc))
			{
				((L2Npc) target).endDecayTask();
			}
		}
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
