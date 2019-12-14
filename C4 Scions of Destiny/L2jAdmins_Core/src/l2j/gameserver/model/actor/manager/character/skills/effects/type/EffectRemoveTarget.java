package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author -Nemesiss-
 */
public class EffectRemoveTarget extends Effect
{
	public EffectRemoveTarget(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.REMOVE_TARGET;
	}
	
	@Override
	public void onStart()
	{
		getEffected().setTarget(null);
		getEffected().abortAttack();
		getEffected().abortCast();
		getEffected().getAI().setIntention(CtrlIntentionType.IDLE, getEffector());
	}
	
	@Override
	public void onExit()
	{
		// nothing
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
