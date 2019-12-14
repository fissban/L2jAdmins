package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

public class EffectSilentMove extends Effect
{
	public EffectSilentMove(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SILENT_MOVE;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		if (getEffected() instanceof L2Playable)
		{
			((L2Playable) getEffected()).setSilentMoving(true);
		}
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		
		if (getEffected() instanceof L2Playable)
		{
			((L2Playable) getEffected()).setSilentMoving(false);
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		final double manaDam = calc();
		if (manaDam > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
}
