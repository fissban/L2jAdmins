package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMaxMpMul extends Func
{
	private static final FuncMaxMpMul fmmm_instance = new FuncMaxMpMul();
	
	public static Func getInstance()
	{
		return fmmm_instance;
	}
	
	private FuncMaxMpMul()
	{
		super(StatsType.MAX_MP, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMaxMP());
			return;
		}
		env.mulValue(BaseStatsType.MEN.calcBonus(env.getPlayer()));
	}
}
