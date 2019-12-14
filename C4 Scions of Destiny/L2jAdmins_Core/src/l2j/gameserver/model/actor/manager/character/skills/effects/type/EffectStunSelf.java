package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

public class EffectStunSelf extends Effect
{
	public EffectStunSelf(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.STUN_SELF;
	}
	
	@Override
	public void onStart()
	{
		getEffector().startStunning();
	}
	
	@Override
	public void onExit()
	{
		getEffector().stopStunning(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
