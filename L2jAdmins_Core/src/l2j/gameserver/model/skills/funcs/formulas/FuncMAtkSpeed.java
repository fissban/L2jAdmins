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
public class FuncMAtkSpeed extends Func
{
	private static final FuncMAtkSpeed fas_instance = new FuncMAtkSpeed();
	
	public static Func getInstance()
	{
		return fas_instance;
	}
	
	private FuncMAtkSpeed()
	{
		super(StatsType.MAGICAL_ATTACK_SPEED, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2RaidBossInstance || env.getPlayer() instanceof L2GrandBossInstance)
		{
			env.mulValue(BaseStatsType.WIT.calcBonus(20));
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetCastSpeed());
		}
		else
		{
			env.mulValue(BaseStatsType.WIT.calcBonus(env.getPlayer()));
		}
	}
}
