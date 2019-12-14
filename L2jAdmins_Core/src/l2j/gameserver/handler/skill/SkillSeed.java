package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.effects.type.EffectSeed;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;

/**
 * @author fissban
 */
public class SkillSeed implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SEED
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
		
		// Update Seeds Effects
		for (var object : targets)
		{
			var target = (L2Character) object;
			if (target.isAlikeDead() && (skill.getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
			{
				continue;
			}
			
			var oldEffect = (EffectSeed) target.getEffect(skill.getId());
			if (oldEffect == null)
			{
				skill.getEffects(player, target);
			}
			else
			{
				oldEffect.increasePowerSeed();
			}
			
			for (var e : target.getAllEffects())
			{
				if (e.getEffectType() == EffectType.SEED)
				{
					e.rescheduleEffect();
				}
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
