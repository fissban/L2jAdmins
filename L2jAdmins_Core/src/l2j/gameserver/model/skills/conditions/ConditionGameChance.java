package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;
import l2j.util.Rnd;

/**
 * @author Advi
 */
public class ConditionGameChance extends Condition
{
	private int chance;
	
	public ConditionGameChance(int chance)
	{
		this.chance = chance;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return Rnd.get(100) < chance;
	}
}
