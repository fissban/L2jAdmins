package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author mkizub
 */
public class EffectFakeDeath extends Effect
{
	public EffectFakeDeath(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FAKE_DEATH;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startFakeDeath();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopFakeDeath(false);
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
		
		double manaDam = calc();
		
		if (manaDam > getEffected().getCurrentMp())
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_MP);
				return false;
			}
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
}
