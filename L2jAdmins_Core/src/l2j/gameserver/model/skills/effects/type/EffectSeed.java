package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

public class EffectSeed extends Effect
{
	int powerSeed = 1;
	
	public EffectSeed(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SEED;
	}
	
	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
	
	public int getPowerSeed()
	{
		return powerSeed;
	}
	
	public void increasePowerSeed()
	{
		powerSeed++;
	}
}
