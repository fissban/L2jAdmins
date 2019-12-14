package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class FuncMAtkCritical extends Func
{
	private static final FuncMAtkCritical fac_instance = new FuncMAtkCritical();
	
	public static Func getInstance()
	{
		return fac_instance;
	}
	
	private FuncMAtkCritical()
	{
		super(StatsType.MAGICAL_CRITICAL_RATE, 0x30, null);
	}
	
	@Override
	public void calc(Env env)
	{
		L2Character p = env.getPlayer();
		if (p instanceof L2Summon)
		{
			env.setValue(5);
		}
		else if ((p instanceof L2PcInstance) && (p.getActiveWeaponInstance() != null))
		{
			env.mulValue(BaseStatsType.WIT.calcBonus(p));
		}
	}
}
