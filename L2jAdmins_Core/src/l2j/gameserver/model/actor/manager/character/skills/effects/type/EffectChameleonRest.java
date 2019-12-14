package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

public class EffectChameleonRest extends Effect
{
	public EffectChameleonRest(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RELAXING;
	}
	
	@Override
	public void onStart()
	{
		if (getEffected() instanceof L2PcInstance)
		{
			((L2PcInstance) getEffected()).setSilentMoving(true);
			((L2PcInstance) getEffected()).sitDown();
		}
		else
		{
			getEffected().getAI().setIntention(CtrlIntentionType.REST);
		}
		
		super.onStart();
	}
	
	@Override
	public void onExit()
	{
		if (getEffected() instanceof L2PcInstance)
		{
			((L2PcInstance) getEffected()).setSilentMoving(false);
		}
		
		super.onExit();
	}
	
	@Override
	public boolean onActionTime()
	{
		L2PcInstance effected = (L2PcInstance) getEffected();
		
		if (getEffected().isDead())
		{
			return false;
		}
		
		// Only cont skills shouldn't end
		if (!effected.isSitting())
		{
			return false;
		}
		
		double manaDam = calc();
		if (manaDam > effected.getCurrentMp())
		{
			effected.sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		effected.reduceCurrentMp(manaDam);
		
		return true;
	}
}
