package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMaxHpMul extends Func
{
	private static final FuncMaxHpMul fmhm_instance = new FuncMaxHpMul();
	
	public static Func getInstance()
	{
		return fmhm_instance;
	}
	
	private FuncMaxHpMul()
	{
		super(StatsType.MAX_HP, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2RaidBossInstance || env.getPlayer() instanceof L2GrandBossInstance)
		{
			env.mulValue(BaseStatsType.CON.calcBonus(43));
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMaxHP());
			return;
		}
		else
		{
			env.mulValue(BaseStatsType.CON.calcBonus(env.getPlayer()));
		}
	}
}
