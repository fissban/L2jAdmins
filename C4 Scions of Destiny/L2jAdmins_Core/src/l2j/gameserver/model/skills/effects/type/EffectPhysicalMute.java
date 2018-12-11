package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

public class EffectPhysicalMute extends Effect
{
	public EffectPhysicalMute(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PHYSICAL_MUTE;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		getEffected().startPhysicalMuted();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		
		getEffected().stopPhysicalMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
