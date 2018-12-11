package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class ConditionItemId extends Condition
{
	private int itemId;
	
	public ConditionItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getItem() == null)
		{
			return false;
		}
		return env.getItem().getId() == itemId;
	}
}
