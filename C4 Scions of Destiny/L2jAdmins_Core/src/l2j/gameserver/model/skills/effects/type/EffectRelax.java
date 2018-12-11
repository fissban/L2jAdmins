package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

public class EffectRelax extends Effect
{
	public EffectRelax(Env env, EffectTemplate template)
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
		if (getEffected().getCurrentHp() == getEffected().getStat().getMaxHp())
		{
			getEffected().sendPacket(SystemMessage.SKILL_DEACTIVATED_HP_FULL);
			return;
		}
		
		if (getEffected() instanceof L2PcInstance)
		{
			((L2PcInstance) getEffected()).sitDown();
		}
		
		super.onStart();
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		if (getEffected() instanceof L2PcInstance)
		{
			if (!((L2PcInstance) getEffected()).isSitting())
			{
				return false;
			}
		}
		
		if (getEffected().getCurrentHp() == getEffected().getStat().getMaxHp())
		{
			getEffected().sendPacket(SystemMessage.SKILL_DEACTIVATED_HP_FULL);
			return false;
		}
		
		double manaDam = calc();
		if (manaDam > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
}
