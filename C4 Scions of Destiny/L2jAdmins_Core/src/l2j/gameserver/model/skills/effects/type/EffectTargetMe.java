package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectTargetMe extends Effect
{
	public EffectTargetMe(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.TARGET_ME;
	}
	
	@Override
	public void onStart()
	{
		int raius = getSkill().getSkillRadius();
		
		if (raius > 80)
		{
			for (L2Character character : getEffector().getKnownList().getObjectTypeInRadius(L2Character.class, getSkill().getSkillRadius()))
			{
				if (getEffector().isAutoAttackable(character))
				{
					targetMe(character);
				}
			}
		}
		else
		{
			targetMe(getEffector());
		}
	}
	
	private void targetMe(L2Character character)
	{
		if (character instanceof L2Attackable)
		{
			character.getAI().notifyEvent(CtrlEventType.AGGRESSION, getEffector(), 9999999);
		}
		else
		{
			character.abortAttack();
			character.setTarget(getEffected());
		}
	}
	
	@Override
	public void onExit()
	{
		//
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
