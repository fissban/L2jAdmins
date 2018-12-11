package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class EffectNegateEffect extends Effect
{
	public EffectNegateEffect(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL_DEBUFF;
	}
	
	@Override
	public void onStart()
	{
		for (Effect e : getEffected().getAllEffects())
		{
			if (getSkill().getNegateStats().contains(e.getEffectType()))
			{
				if (getSkill().getEffectPower() >= Rnd.get(1, 100))
				{
					e.exit();
				}
			}
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
