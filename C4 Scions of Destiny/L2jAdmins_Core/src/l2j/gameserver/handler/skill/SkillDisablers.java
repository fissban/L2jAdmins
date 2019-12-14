package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.enums.NpcRaceType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author drunk_
 */
public class SkillDisablers implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.AGGDAMAGE,
			SkillType.AGGREDUCE,
			SkillType.AGGREDUCE_CHAR,
			SkillType.AGGREMOVE,
			SkillType.MUTE
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var weaponInst = activeChar.getActiveWeaponInstance();
		
		if (activeChar instanceof L2PcInstance)
		{
			if ((weaponInst == null) && skill.isOffensive())
			{
				activeChar.sendMessage("You must equip a weapon before casting a spell.");
				return;
			}
		}
		
		var sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		var bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (var object : targets)
		{
			if (object == null)
			{
				continue;
			}
			
			// Get a target
			if (!(object instanceof L2Character))
			{
				continue;
			}
			
			var target = (L2Character) object;
			
			if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			switch (skill.getSkillType())
			{
				case MUTE:
					if (Formulas.calcEffectSuccess(activeChar, target, skill, skill.getEffectPower(), true, false, sps, bss))
					{
						if (Formulas.calculateSkillReflect(skill, target))
						{
							target = activeChar;
						}
						
						if (target == null)
						{
							return;
						}
						
						// stop same type effect if available
						for (var e : target.getAllEffects())
						{
							if (e.getSkill().getSkillType() == skill.getSkillType())
							{
								e.exit();
							}
						}
						
						skill.getEffects(activeChar, target);
					}
					break;
				case AGGDAMAGE:
					if (target instanceof L2MonsterInstance)
					{
						target.getAI().notifyEvent(CtrlEventType.AGGRESSION, activeChar, (int) skill.getPower());
					}
					
					skill.getEffects(activeChar, target);
					
					break;
				case AGGREDUCE:
					// these skills needs to be rechecked
					if (target instanceof L2MonsterInstance)
					{
						skill.getEffects(activeChar, target);
						
						var aggdiff = ((L2MonsterInstance) target).getHating(activeChar) - target.calcStat(StatsType.AGGRESSION, ((L2MonsterInstance) target).getHating(activeChar), target, skill);
						
						if (skill.getPower() > 0)
						{
							target.getAI().notifyEvent(CtrlEventType.AGGRESSION, null, -(int) skill.getPower());
						}
						else if (aggdiff > 0)
						{
							target.getAI().notifyEvent(CtrlEventType.AGGRESSION, null, -(int) aggdiff);
						}
					}
					break;
				case AGGREDUCE_CHAR:
					// these skills needs to be rechecked
					if (Formulas.calcEffectSuccess(activeChar, target, skill, skill.getEffectPower(), true, false, sps, bss))
					{
						if (target instanceof L2MonsterInstance)
						{
							target.getAI().notifyEvent(CtrlEventType.AGGRESSION, activeChar, -((L2MonsterInstance) target).getHating(activeChar));
						}
						
						skill.getEffects(activeChar, target);
					}
					break;
				case AGGREMOVE:
					// these skills needs to be rechecked
					if (target instanceof L2MonsterInstance)
					{
						L2MonsterInstance monster = (L2MonsterInstance) target;
						if (Formulas.calcEffectSuccess(activeChar, monster, skill, skill.getEffectPower(), true, false, sps, bss))
						{
							if (skill.getTargetType() == SkillTargetType.TARGET_UNDEAD)
							{
								if (monster.getTemplate().getRace() == NpcRaceType.UNDEAD)
								{
									monster.stopHating(target);
									monster.getAI().notifyEvent(CtrlEventType.AGGRESSION, monster.getMostHated(), -monster.getHating(monster.getMostHated()));
								}
							}
							else
							{
								monster.getAI().notifyEvent(CtrlEventType.AGGRESSION, monster.getMostHated(), -monster.getHating(monster.getMostHated()));
							}
						}
					}
					break;
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
