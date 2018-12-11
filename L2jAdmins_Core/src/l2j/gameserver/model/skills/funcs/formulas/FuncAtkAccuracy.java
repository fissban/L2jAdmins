package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncAtkAccuracy extends Func
{
	private static final FuncAtkAccuracy faa_instance = new FuncAtkAccuracy();
	
	public static Func getInstance()
	{
		return faa_instance;
	}
	
	private FuncAtkAccuracy()
	{
		super(StatsType.ACCURACY_COMBAT, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		// [Square(DEX)]*6 + lvl + weapon hitbonus;
		env.incValue(Math.sqrt(env.getPlayer().getStat().getDEX()) * 6);
		env.incValue(env.getPlayer().getLevel());
		
		if (env.getPlayer() instanceof L2SummonInstance)
		{
			env.incValue((env.getPlayer().getLevel() < 60) ? 4 : 5);
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetAccuracy());
		}
	}
	
}
