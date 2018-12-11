package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.Config;
import l2j.gameserver.handler.SkillHandler;
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
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.lib.Log;

public class SkillPdam implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.PDAM,
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		var ss = activeChar.isChargedShot(ShotType.SOULSHOTS);
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			
			if ((activeChar instanceof L2PcInstance) && (target instanceof L2PcInstance) && ((L2PcInstance) target).isFakeDeath())
			{
				target.stopFakeDeath(true);
			}
			else if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			var shld = Formulas.calcShldUse(activeChar, target);
			var crit = false;
			if (skill.getBaseCritRate() > 0)
			{
				crit = Formulas.calcCrit(skill.getBaseCritRate() * 10 * BaseStatsType.STR.calcBonus(activeChar));
			}
			
			var damage = Formulas.calcPhysDam(activeChar, target, skill, shld, false, ss);
			
			if (crit)
			{
				damage *= 2;
			}
			
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
					Log.add(activeChar.getName() + "(" + activeChar.getObjectId() + ") " + activeChar.getLevel() + " lvl did damage " + damage + " with skill " + skill.getName() + "(" + skill.getId() + ") to " + name, "damage_pdam");
				}
			}
			
			if (damage > 0)
			{
				activeChar.sendDamageMessage(target, (int) damage, false, crit, false);
				
				if (skill.hasEffects())
				{
					if (Formulas.calcEffectSuccess(activeChar, target, skill, skill.getEffectPower(), true, ss, false, false))
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
				
				target.reduceCurrentHp(damage, activeChar);
			}
			else
			{
				activeChar.sendPacket(SystemMessage.ATTACK_FAILED);
			}
			
			// Sonic Rage & Raging Force
			if (skill.getMaxCharges() > 0)
			{
				if (activeChar instanceof L2PcInstance)
				{
					if (((L2PcInstance) activeChar).getCharges() < skill.getMaxCharges())
					{
						((L2PcInstance) activeChar).addCharge(1);
					}
				}
			}
			
			if (skill.getId() == 343)
			{
				Formulas.calcLethalStrike(activeChar, target, skill.getMagicLevel());
			}
			else if (skill.getId() == 348)
			{
				// check for other effects
				var handler = SkillHandler.getHandler(SkillType.SPOIL);
				if (handler != null)
				{
					handler.useSkill(activeChar, skill, targets);
				}
			}
			
			var effect = activeChar.getEffect(skill.getId());
			if ((effect != null) && effect.isSelfEffect())
			{
				effect.exit();
			}
			
			skill.getEffectsSelf(activeChar);
		}
		
		activeChar.setChargedShot(ShotType.SOULSHOTS, false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
