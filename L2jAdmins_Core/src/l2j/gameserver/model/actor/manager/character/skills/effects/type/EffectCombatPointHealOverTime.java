package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

public class EffectCombatPointHealOverTime extends Effect
{
	public EffectCombatPointHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.COMBAT_POINT_HEAL_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double cp = getEffected().getCurrentCp();
		double maxcp = getEffected().getStat().getMaxCp();
		cp += calc();
		if (cp > maxcp)
		{
			cp = maxcp;
		}
		getEffected().setCurrentCp(cp);
		StatusUpdate sump = new StatusUpdate(getEffected().getObjectId());
		sump.addAttribute(StatusUpdateType.CUR_CP, (int) cp);
		getEffected().sendPacket(sump);
		return true;
	}
}
