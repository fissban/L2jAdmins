package l2j.gameserver.model.skills.conditions;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionLogicOr extends Condition
{
	public List<Condition> conditions = new ArrayList<>();
	
	public void add(Condition condition)
	{
		if (condition == null)
		{
			return;
		}
		if (getListener() != null)
		{
			condition.setListener(this);
		}
		conditions.add(condition);
	}
	
	@Override
	public void setListener(Condition listener)
	{
		if (listener != null)
		{
			for (Condition c : conditions)
			{
				c.setListener(this);
			}
		}
		else
		{
			for (Condition c : conditions)
			{
				c.setListener(null);
			}
		}
		super.setListener(listener);
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		for (Condition c : conditions)
		{
			if (c.test(env))
			{
				return true;
			}
		}
		return false;
	}
}
