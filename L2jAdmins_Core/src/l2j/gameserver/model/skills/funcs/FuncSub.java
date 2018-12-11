package l2j.gameserver.model.skills.funcs;

import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

public class FuncSub extends Func
{
	private final Lambda lambda;
	
	public FuncSub(StatsType stat, int order, Object owner, Lambda lambda)
	{
		super(stat, order, owner);
		this.lambda = lambda;
	}
	
	@Override
	public void calc(Env env)
	{
		if ((cond == null) || cond.test(env))
		{
			env.decValue(lambda.calc(env));
		}
	}
}
