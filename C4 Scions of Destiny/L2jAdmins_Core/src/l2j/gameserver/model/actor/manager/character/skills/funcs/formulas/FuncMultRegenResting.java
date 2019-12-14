package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerState;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionPlayerState.CheckPlayerState;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class FuncMultRegenResting extends Func
{
	private static final FuncMultRegenResting[] instancies = new FuncMultRegenResting[StatsType.NUM_STATS];
	
	public static Func getInstance(StatsType stat)
	{
		int pos = stat.ordinal();
		
		if (instancies[pos] == null)
		{
			instancies[pos] = new FuncMultRegenResting(stat);
		}
		
		return instancies[pos];
	}
	
	private FuncMultRegenResting(StatsType stat)
	{
		super(stat, 0x20, null);
		setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
	}
	
	@Override
	public void calc(Env env)
	{
		if (!cond.test(env))
		{
			return;
		}
		
		env.mulValue(1.45);
	}
}
