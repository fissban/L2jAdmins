package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMoveSpeed extends Func
{
	private static final FuncMoveSpeed fms_instance = new FuncMoveSpeed();
	
	public static Func getInstance()
	{
		return fms_instance;
	}
	
	private FuncMoveSpeed()
	{
		super(StatsType.RUN_SPEED, 0x30, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetSpeed());
			return;
		}
		else if (env.getPlayer() instanceof L2Playable)
		{
			env.mulValue(BaseStatsType.DEX.calcBonus(env.getPlayer()));
		}
		else
		{
			env.mulValue(env.getPlayer().getLevelMod());
		}
	}
}
