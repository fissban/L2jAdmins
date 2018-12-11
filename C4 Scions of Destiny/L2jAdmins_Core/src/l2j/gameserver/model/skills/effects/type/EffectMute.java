package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

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
		
		getEffected().startMuted();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		
		getEffected().stopMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
