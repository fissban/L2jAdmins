package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionPlayerRace extends Condition
{
	final Race race;
	
	public ConditionPlayerRace(Race race)
	{
		this.race = race;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.getPlayer() instanceof L2PcInstance))
		{
			return false;
		}
		return ((L2PcInstance) env.getPlayer()).getRace() == race;
	}
}
