package l2j.gameserver.model.skills.funcs;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class LambdaStats extends Lambda
{
	public enum LambdaStatsType
	{
		PLAYER_LEVEL,
		TARGET_LEVEL,
		PLAYER_MAX_HP,
		PLAYER_MAX_MP
	}
	
	private final LambdaStatsType stat;
	
	public LambdaStats(LambdaStatsType stat)
	{
		this.stat = stat;
	}
	
	@Override
	public double calc(Env env)
	{
		switch (stat)
		{
			case PLAYER_LEVEL:
				return (env.getPlayer() == null) ? 1 : env.getPlayer().getLevel();
			
			case TARGET_LEVEL:
				return (env.getTarget() == null) ? 1 : env.getTarget().getLevel();
			
			case PLAYER_MAX_HP:
				return (env.getPlayer() == null) ? 1 : env.getPlayer().getStat().getMaxHp();
			
			case PLAYER_MAX_MP:
				return (env.getPlayer() == null) ? 1 : env.getPlayer().getStat().getMaxMp();
		}
		return 0;
	}
}
