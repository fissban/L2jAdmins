package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectBleed extends Effect
{
	public EffectBleed(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLEED;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double damage = calc();
		if (damage >= (getEffected().getCurrentHp() - 1))
		{
			if (getEffected().getCurrentHp() <= 1)
			{
				return true;
			}
			
			damage = getEffected().getCurrentHp() - 1;
		}
		
		boolean awake = !(getEffected() instanceof L2Attackable);
		
		getEffected().reduceCurrentHp(damage, getEffector(), awake);
		
		getEffected().getAI().clientStartAutoAttack();
		
		return true;
	}
}
