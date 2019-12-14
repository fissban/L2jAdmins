package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.actor.manager.character.templates.PcTemplate;

/**
 * @author fissban
 */
public class FuncMaxCpAdd extends Func
{
	private static final FuncMaxCpAdd fmca_instance = new FuncMaxCpAdd();
	
	public static Func getInstance()
	{
		return fmca_instance;
	}
	
	private FuncMaxCpAdd()
	{
		super(StatsType.MAX_CP, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		PcTemplate t = (PcTemplate) env.getPlayer().getTemplate();
		int lvl = env.getPlayer().getLevel() - t.getClassBaseLevel();
		double cpmod = t.getLvlCpMod() * lvl;
		double cpmax = (t.getLvlCpAdd() + cpmod) * lvl;
		double cpmin = (t.getLvlCpAdd() * lvl) + cpmod;
		env.incValue((cpmax + cpmin) / 2);
	}
}
