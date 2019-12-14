package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class FuncMaxCpMul extends Func
{
	private static final FuncMaxCpMul fmcm_instance = new FuncMaxCpMul();
	
	public static Func getInstance()
	{
		return fmcm_instance;
	}
	
	private FuncMaxCpMul()
	{
		super(StatsType.MAX_CP, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		env.mulValue(BaseStatsType.CON.calcBonus(env.getPlayer()));
	}
}
