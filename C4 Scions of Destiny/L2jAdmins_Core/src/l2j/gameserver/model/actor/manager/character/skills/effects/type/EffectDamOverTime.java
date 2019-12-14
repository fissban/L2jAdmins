package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

public class EffectDamOverTime extends Effect
{
	public EffectDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double damage = calc();
		if (damage >= (getEffected().getCurrentHp() - 1))
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_HP);
				return false;
			}
			
			if (getEffected().getCurrentHp() <= 1)
			{
				return true;
			}
			
			damage = getEffected().getCurrentHp() - 1;
		}
		
		boolean awake = !(getEffected() instanceof L2Attackable) && !((getSkill().getTargetType() == SkillTargetType.TARGET_SELF) && getSkill().isToggle());
		
		getEffected().reduceCurrentHp(damage, getEffector(), awake);
		
		if (!getSkill().isToggle())
		{
			AttackStanceTaskManager.getInstance().add(getEffected());
		}
		
		return true;
	}
}
