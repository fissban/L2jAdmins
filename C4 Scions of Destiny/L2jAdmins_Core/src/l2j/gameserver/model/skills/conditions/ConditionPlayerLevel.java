package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionPlayerLevel extends Condition
{
	final int level;
	
	public ConditionPlayerLevel(int level)
	{
		this.level = level;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return env.getPlayer().getLevel() >= level;
	}
}
