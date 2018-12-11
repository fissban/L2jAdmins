package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.templates.PcTemplate;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMaxHpAdd extends Func
{
	private static final FuncMaxHpAdd fmha_instance = new FuncMaxHpAdd();
	
	public static Func getInstance()
	{
		return fmha_instance;
	}
	
	private FuncMaxHpAdd()
	{
		super(StatsType.MAX_HP, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMaxHP());
			return;
		}
		
		PcTemplate t = (PcTemplate) env.getPlayer().getTemplate();
		int lvl = env.getPlayer().getLevel() - t.getClassBaseLevel();
		double hpmod = t.getLvlHpMod() * lvl;
		double hpmax = (t.getLvlHpAdd() + hpmod) * lvl;
		double hpmin = (t.getLvlHpAdd() * lvl) + hpmod;
		env.incValue((hpmax + hpmin) / 2);
	}
}
