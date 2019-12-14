package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

public class EffectMpConsumePerLevel extends Effect
{
	public EffectMpConsumePerLevel(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MP_CONSUME_PER_LEVEL;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		final double base = calc();
		final double consume = ((getEffected().getLevel() - 1) / 7.5) * base * getPeriod();
		
		if (consume > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		getEffected().reduceCurrentMp(consume);
		return true;
	}
}
