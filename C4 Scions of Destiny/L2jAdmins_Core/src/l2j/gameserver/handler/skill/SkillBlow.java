package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Steuf, fissban
 */
public class SkillBlow implements ISkillHandler
{
	
	private final static byte CHANCE_FRONT = 50;
	private final static byte CHANCE_SIDE = 60;
	private final static byte CHANCE_BEHIND = 70;
	// Bonus damage when the ability to result in "critical"
	private static final int CIRITICAL_SKILL_POWER = 2;
	//
	private static final int SKILL_LETHAL_BLOW = 344;
	
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.BLOW
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			
			if (target.isAlikeDead())
			{
				continue;
			}
			
			var successChance = CHANCE_SIDE;
			
			if (activeChar.isBehindTarget())
			{
				successChance = CHANCE_BEHIND;
			}
			else if (activeChar.isInFrontOfTarget())
			{
				successChance = CHANCE_FRONT;
			}
			
			// If skill requires CRIT or skill requires BEHIND, calculate chance based on DEX, Position and on self BUFF
			var success = true;
			
			switch (skill.getBlowDamageCondition())
			{
				case BEHIND:
					success = (successChance == CHANCE_BEHIND);
					break;
				
				case CRIT:
					success = (success && Formulas.calcBlow(activeChar, target, successChance));
					break;
			}
			
			if (success)
			{
				var ss = activeChar.isChargedShot(ShotType.SOULSHOTS);
				var shld = Formulas.calcShldUse(activeChar, target);
				
				var damage = (int) Formulas.calcBlowDamage(activeChar, target, skill, shld, ss);
				
				// Crit rate base crit rate for skill, modified with STR bonus
				if (Formulas.calcCrit(skill.getBaseCritRate() * 10 * BaseStatsType.STR.calcBonus(activeChar)))
				{
					damage *= CIRITICAL_SKILL_POWER;
				}
				
				if (damage > 0)
				{
					// Manage attack or cast break of the target (calculating rate, sending message...)
					if (Formulas.calcAtkBreak(target, damage))
					{
						target.breakAttack();
						target.breakCast();
					}
				}
				
				var olympiadMode = (activeChar instanceof L2PcInstance) && ((L2PcInstance) activeChar).isInOlympiadMode() && (target instanceof L2PcInstance) && ((L2PcInstance) target).isInOlympiadMode();
				
				if ((skill.getId() == SKILL_LETHAL_BLOW) && !olympiadMode)
				{
					Formulas.calcLethalStrike(activeChar, target, skill.getMagicLevel());
				}
				else
				{
					target.reduceCurrentHp(damage, activeChar);
					
					if (activeChar instanceof L2PcInstance)
					{
						activeChar.sendDamageMessage(target, damage, false, true, false);
						activeChar.sendPacket(new PlaySound("skillsound.critical_hit_02"));
					}
				}
			}
			else
			{
				activeChar.sendPacket(SystemMessage.ATTACK_FAILED);
			}
			
			var effect = activeChar.getEffect(skill.getId());
			
			// Self Effect
			if ((effect != null) && effect.isSelfEffect())
			{
				effect.exit();
			}
			
			skill.getEffectsSelf(activeChar);
			
			activeChar.setChargedShot(ShotType.SOULSHOTS, false);
			
			// notify the AI that it is attacked
			target.getAI().notifyEvent(CtrlEventType.ATTACKED, activeChar);
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
