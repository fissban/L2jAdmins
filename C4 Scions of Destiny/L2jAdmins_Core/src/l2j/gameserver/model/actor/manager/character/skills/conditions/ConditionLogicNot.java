package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionLogicNot extends Condition
{
	private Condition condition;
	
	public ConditionLogicNot(Condition condition)
	{
		this.condition = condition;
		if (getListener() != null)
		{
			condition.setListener(this);
		}
	}
	
	@Override
	public void setListener(Condition listener)
	{
		if (listener != null)
		{
			condition.setListener(this);
		}
		else
		{
			condition.setListener(null);
		}
		super.setListener(listener);
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return !condition.test(env);
	}
}
