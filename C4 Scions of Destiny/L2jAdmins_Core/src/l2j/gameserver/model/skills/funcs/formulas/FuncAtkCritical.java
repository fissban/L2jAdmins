package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncAtkCritical extends Func
{
	private static final FuncAtkCritical fac_instance = new FuncAtkCritical();
	
	public static Func getInstance()
	{
		return fac_instance;
	}
	
	private FuncAtkCritical()
	{
		super(StatsType.PHYSICAL_CRITICAL_RATE, 0x09, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2SummonInstance)
		{
			env.setValue(40);
			return;
		}
		
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetCritical());
			return;
		}
		
		if (env.getPlayer() instanceof L2PcInstance)
		{
			if (env.getPlayer().getActiveWeaponInstance() == null)
			{
				env.setValue(40);
				return;
			}
		}
		
		env.mulValue(BaseStatsType.DEX.calcBonus(env.getPlayer()));
		env.mulValue(10);
		
		env.setBaseValue(env.getValue());
	}
}
