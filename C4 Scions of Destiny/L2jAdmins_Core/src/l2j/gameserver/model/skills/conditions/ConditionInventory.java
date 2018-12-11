package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public abstract class ConditionInventory extends Condition
{
	final ParpedollType slot;
	
	public ConditionInventory(ParpedollType slot)
	{
		this.slot = slot;
	}
	
	@Override
	public abstract boolean testImpl(Env env);
}
