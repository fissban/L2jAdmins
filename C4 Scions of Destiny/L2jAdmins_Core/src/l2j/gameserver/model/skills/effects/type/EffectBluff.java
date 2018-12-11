package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2NpcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.network.external.server.BeginRotation;
import l2j.gameserver.network.external.server.StopRotation;

/**
 * @author decad
 */
public class EffectBluff extends Effect
{
	public EffectBluff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLUFF;
	}
	
	@Override
	public void onStart()
	{
		if (getEffected() instanceof L2NpcInstance)
		{
			return;
		}
		
		if ((getEffected() instanceof L2Npc) && (((L2Npc) getEffected()).getId() == 12024))
		{
			return;
		}
		
		if (getEffected() instanceof L2SiegeSummonInstance)
		{
			return;
		}
		
		getEffected().broadcastPacket(new BeginRotation(getEffected(), getEffected().getHeading(), 1, 65535));
		getEffected().broadcastPacket(new StopRotation(getEffected(), getEffector().getHeading(), 65535));
		getEffected().setHeading(getEffector().getHeading());
		getEffected().setTarget(null);
		getEffected().abortAttack();
		getEffected().abortCast();
		getEffected().getAI().setIntention(CtrlIntentionType.IDLE, getEffector());
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
