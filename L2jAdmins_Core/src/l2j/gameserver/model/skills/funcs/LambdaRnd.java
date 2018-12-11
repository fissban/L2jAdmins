package l2j.gameserver.model.skills.funcs;

import l2j.gameserver.model.skills.stats.Env;
import l2j.util.Rnd;

/**
 * @author mkizub
 */
public final class LambdaRnd extends Lambda
{
	private final Lambda max;
	private final boolean linear;
	
	public LambdaRnd(Lambda max, boolean linear)
	{
		this.max = max;
		this.linear = linear;
	}
	
	@Override
	public double calc(Env env)
	{
		if (linear)
		{
			return max.calc(env) * Rnd.nextDouble();
		}
		return max.calc(env) * Rnd.nextGaussian();
	}
}
