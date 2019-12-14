package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author earendil
 */
public class EffectNoblesseBless extends Effect
{
	public EffectNoblesseBless(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.NOBLESSE_BLESSING;
	}
	
	@Override
	public void onStart()
	{
		if (getEffected() instanceof L2Playable)
		{
			((L2Playable) getEffected()).startNoblesseBlessing();
		}
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof L2Playable)
		{
			((L2Playable) getEffected()).stopNoblesseBlessing();
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
