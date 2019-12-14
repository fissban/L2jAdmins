package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMAtkMod extends Func
{
	private static final FuncMAtkMod fma_instance = new FuncMAtkMod();
	
	public static Func getInstance()
	{
		return fma_instance;
	}
	
	private FuncMAtkMod()
	{
		super(StatsType.MAGICAL_ATTACK, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		double intb = 0;
		
		if (env.getPlayer() instanceof L2RaidBossInstance)
		{
			intb = BaseStatsType.INT.calcBonus(21);
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMAtk());
			return;
		}
		else
		{
			intb = BaseStatsType.INT.calcBonus(env.getPlayer());
		}
		
		double lvlb = env.getPlayer().getLevelMod();
		
		env.mulValue((lvlb * lvlb) * (intb * intb));
	}
}
