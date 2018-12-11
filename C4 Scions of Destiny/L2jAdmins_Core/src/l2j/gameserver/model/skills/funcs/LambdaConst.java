package l2j.gameserver.model.skills.funcs;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class LambdaConst extends Lambda
{
	private final double value;
	
	public LambdaConst(double value)
	{
		this.value = value;
	}
	
	@Override
	public double calc(Env env)
	{
		return value;
	}
}
