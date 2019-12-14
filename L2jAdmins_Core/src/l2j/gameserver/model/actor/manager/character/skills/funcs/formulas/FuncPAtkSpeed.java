package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncPAtkSpeed extends Func
{
	private static final FuncPAtkSpeed fas_instance = new FuncPAtkSpeed();
	
	public static Func getInstance()
	{
		return fas_instance;
	}
	
	private FuncPAtkSpeed()
	{
		super(StatsType.PHYSICAL_ATTACK_SPEED, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetAtkSpeed());
		}
		else
		{
			env.mulValue(BaseStatsType.DEX.calcBonus(env.getPlayer()));
		}
	}
}
