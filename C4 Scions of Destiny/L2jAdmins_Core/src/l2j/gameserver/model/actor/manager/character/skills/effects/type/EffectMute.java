package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

public class EffectMute extends Effect
{
	public EffectMute(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MUTE;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		getEffected().startMagicalMuted();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		
		getEffected().stopMagicalMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
