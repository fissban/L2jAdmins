
package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

public class EffectPetrification extends Effect
{
	public EffectPetrification(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PETRIFICATION;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startParalyze();
		getEffected().setIsInvul(true);
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopParalyze();
		getEffected().setIsInvul(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
