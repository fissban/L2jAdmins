package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncAtkEvasion extends Func
{
	private static final FuncAtkEvasion fae_instance = new FuncAtkEvasion();
	
	public static Func getInstance()
	{
		return fae_instance;
	}
	
	private FuncAtkEvasion()
	{
		super(StatsType.EVASION_RATE, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetEvasion());
			return;
		}
		
		// [Square(DEX)]*6 + lvl;
		env.incValue(Math.sqrt(env.getPlayer().getStat().getDEX()) * 6);
		env.incValue(env.getPlayer().getLevel());
	}
	
}
