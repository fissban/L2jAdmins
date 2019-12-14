package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public class EffectSleep extends Effect
{
	public EffectSleep(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SLEEP;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startSleeping();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopSleeping(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		getEffected().stopSleeping(false);
		return false;
	}
}
