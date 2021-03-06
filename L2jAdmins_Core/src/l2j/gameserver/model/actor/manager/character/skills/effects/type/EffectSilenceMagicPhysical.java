package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

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
		getEffected().startMagicalMuted();
		getEffected().startPhysicalMuted();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopMagicalMuted();
		getEffected().stopPhysicalMuted();
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
