package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.items.enums.ParpedollType;

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
