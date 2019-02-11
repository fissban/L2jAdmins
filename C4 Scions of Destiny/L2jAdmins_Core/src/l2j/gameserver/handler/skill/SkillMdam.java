package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.Config;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.util.lib.Log;

public class SkillMdam implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.MDAM
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
		
		for (var target2 : targets)
		{
			var target = (L2Character) target2;
			
			if ((activeChar instanceof L2PcInstance) && (target instanceof L2PcInstance) && ((L2PcInstance) target).isFakeDeath())
			{
				target.stopFakeDeath(true);
			}
			else if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			var mcrit = Formulas.calcMCrit(activeChar.getStat().getMCriticalHit(target, skill));
			
			var damage = (int) Formulas.calcMagicDam(activeChar, target, skill, sps, bss, mcrit);
			
			if (damage > 0)
			{
				// Manage attack or cast break of the target (calculating rate, sending message...)
				if (Formulas.calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}
				
				// reduce target HP
				target.reduceCurrentHp(damage, activeChar);
				
				// send damage message
				activeChar.sendDamageMessage(target, damage, mcrit, false, false);
				
				// apply effects
				if (skill.hasEffects())
				{
					if (Formulas.calcEffectSuccess(activeChar, target, skill, skill.getEffectPower(), true, false, sps, bss))
					{
						if (Formulas.calculateSkillReflect(skill, target))
						{
							target = activeChar;
						}
						
						// activate attacked effects, if any
						target.stopEffect(skill.getId());
						if (target.getEffect(skill.getId()) != null)
						{
							target.removeEffect(target.getEffect(skill.getId()));
						}
						
						skill.getEffects(activeChar, target);
					}
				}
			}
			
			// log damage
			if (Config.LOG_VERY_HIGH_DAMAGE)
			{
				if ((damage > Config.LOG_DMG) && (activeChar instanceof L2PcInstance))
				{
					var name = "";
					if (target instanceof L2RaidBossInstance)
					{
						name = "RaidBoss ";
					}
					if (target instanceof L2Npc)
					{
						name += target.getName() + "(" + ((L2Npc) target).getTemplate().getId() + ")";
					}
					if (target instanceof L2PcInstance)
					{
						name = target.getName() + "(" + target.getObjectId() + ") ";
					}
					name += target.getLevel() + " lvl";
					Log.add(activeChar.getName() + "(" + activeChar.getObjectId() + ") " + activeChar.getLevel() + " lvl did damage " + damage + " with skill " + skill.getName() + "(" + skill.getId() + ") to " + name, "damage_mdam");
				}
			}
		}
		
		var effect = activeChar.getEffect(skill.getId());
		if ((effect != null) && effect.isSelfEffect())
		{
			effect.exit();
		}
		
		skill.getEffectsSelf(activeChar);
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
