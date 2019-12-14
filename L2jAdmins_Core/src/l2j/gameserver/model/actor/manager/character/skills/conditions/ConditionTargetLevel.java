package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionTargetLevel extends Condition
{
	private int level;
	
	public ConditionTargetLevel(int level)
	{
		this.level = level;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getTarget() == null)
		{
			return false;
		}
		return env.getTarget().getLevel() >= level;
	}
}
