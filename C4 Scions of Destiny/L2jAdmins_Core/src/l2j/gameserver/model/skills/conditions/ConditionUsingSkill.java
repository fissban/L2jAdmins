package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class ConditionUsingSkill extends Condition
{
	final int skillId;
	
	public ConditionUsingSkill(int skillId)
	{
		this.skillId = skillId;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getSkill() == null)
		{
			return false;
		}
		return env.getSkill().getId() == skillId;
	}
}
