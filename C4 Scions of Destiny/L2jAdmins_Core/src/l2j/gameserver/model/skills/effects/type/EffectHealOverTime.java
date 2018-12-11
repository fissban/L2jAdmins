package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

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
		StatusUpdate suhp = new StatusUpdate(getEffected().getObjectId());
		suhp.addAttribute(StatusUpdateType.CUR_HP, (int) hp);
		getEffected().sendPacket(suhp);
		return true;
	}
}
