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
public class FuncPAtkMod extends Func
{
	private static final FuncPAtkMod fpa_instance = new FuncPAtkMod();
	
	public static Func getInstance()
	{
		return fpa_instance;
	}
	
	private FuncPAtkMod()
	{
		super(StatsType.PHYSICAL_ATTACK, 0x30, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetPAtk());
		}
		else if (env.getPlayer() instanceof L2RaidBossInstance)
		{
			env.mulValue(BaseStatsType.STR.calcBonus(40) * env.getPlayer().getLevelMod());
		}
		else
		{
			env.mulValue(BaseStatsType.STR.calcBonus(env.getPlayer()) * env.getPlayer().getLevelMod());
		}
	}
}
