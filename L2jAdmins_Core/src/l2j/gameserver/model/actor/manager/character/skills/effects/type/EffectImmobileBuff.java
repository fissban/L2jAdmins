package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public class EffectImmobileBuff extends Effect
{
	public EffectImmobileBuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public void onStart()
	{
		getEffected().setIsImmobilized(true);
	}
	
	@Override
	public void onExit()
	{
		getEffected().removeEffect(this);
		getEffected().setIsImmobilized(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
