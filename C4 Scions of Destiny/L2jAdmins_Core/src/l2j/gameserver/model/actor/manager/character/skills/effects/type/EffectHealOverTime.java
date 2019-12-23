package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.ExRegenMax;

public class EffectHealOverTime extends Effect
{
	public EffectHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL_OVER_TIME;
	}
	
	@Override
	public void onStart()
	{
		// If effected is a player, send a hp regen effect packet.
		if (getEffected() instanceof L2PcInstance && getTotalCount() > 0 && getPeriod() > 0)
		{
			getEffected().sendPacket(new ExRegenMax(getTotalCount() * getPeriod(), getPeriod(), calc()));
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		if (getEffected() instanceof L2DoorInstance)
		{
			return false;
		}
		
		double hp = getEffected().getCurrentHp();
		double maxhp = getEffected().getStat().getMaxHp();
		hp += calc();
		if (hp > maxhp)
		{
			hp = maxhp;
		}
		
		getEffected().setCurrentHp(hp);
		// send packet for update hp
		((L2PcInstance) getEffected()).updateCurHp();
		return true;
	}
}
