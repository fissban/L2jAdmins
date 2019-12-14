package l2j.gameserver.model.actor.manager.character.skills.funcs;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

public class FuncBaseMul extends Func
{
	private final Lambda lambda;
	
	public FuncBaseMul(StatsType stat, int order, Object owner, Lambda lambda)
	{
		super(stat, order, owner);
		this.lambda = lambda;
	}
	
	@Override
	public void calc(Env env)
	{
		if ((cond == null) || cond.test(env))
		{
			env.incValue(env.getBaseValue() * lambda.calc(env));
		}
	}
}
