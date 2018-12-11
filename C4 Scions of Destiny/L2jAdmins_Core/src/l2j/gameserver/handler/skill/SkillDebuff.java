package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.Formulas;

public class SkillDebuff implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.DEBUFF,
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var ss = skill.useSoulShot() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		var sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		var bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			
			if (skill.isOffensive())
			{
				if (Formulas.calcEffectSuccess(activeChar, target, skill, skill.getEffectPower(), true, ss, sps, bss))
				{
					if (Formulas.calculateSkillReflect(skill, target))
					{
						target = activeChar;
					}
					
					if (skill.isHotSpringsDisease())
					{
						for (Effect e : target.getAllEffects())
						{
							if (e == null)
							{
								continue;
							}
							
							if (e.getSkill().getId() == skill.getId())
							{
								if (e.getSkill().getLevel() < 10)
								{
									skill = SkillData.getInstance().getSkill(skill.getId(), e.getSkill().getLevel() + 1);
									e.exit();
								}
							}
						}
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
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
