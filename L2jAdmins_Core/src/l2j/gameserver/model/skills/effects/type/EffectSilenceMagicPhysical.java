package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

public class EffectSilenceMagicPhysical extends Effect
{
	public EffectSilenceMagicPhysical(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SILENCE_MAGIC_PHYSICAL;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startMuted();
		getEffected().startPhysicalMuted();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopMuted();
		getEffected().stopPhysicalMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
