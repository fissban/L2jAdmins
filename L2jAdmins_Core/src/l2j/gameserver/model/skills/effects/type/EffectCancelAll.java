package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author Fissban
 */
public class EffectCancelAll extends Effect
{
	public EffectCancelAll(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL_ALL;
	}
	
	@Override
	public void onStart()
	{
		for (Effect e : getEffected().getAllEffects())
		{
			if (e != null)
			{
				e.exit();
			}
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
