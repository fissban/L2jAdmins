package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

public class EffectParalyze extends Effect
{
	public EffectParalyze(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PARALYZE;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startParalyze();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopParalyze();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
