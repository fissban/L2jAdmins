package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectNegateEffectSpring extends Effect
{
	public EffectNegateEffectSpring(Env env, EffectTemplate template)
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
			if (getSkill().getNegateIds().contains(e.getSkill().getId()))
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
