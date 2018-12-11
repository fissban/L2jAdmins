package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.templates.PcTemplate;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban, zarie
 */
public class FuncMaxMpAdd extends Func
{
	private static final FuncMaxMpAdd fmma_instance = new FuncMaxMpAdd();
	
	public static Func getInstance()
	{
		return fmma_instance;
	}
	
	private FuncMaxMpAdd()
	{
		super(StatsType.MAX_MP, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMaxMP());
			return;
		}
		
		PcTemplate t = (PcTemplate) env.getPlayer().getTemplate();
		int lvl = env.getPlayer().getLevel() - t.getClassBaseLevel();
		double mpmod = t.getLvlMpMod() * lvl;
		double mpmax = (t.getLvlMpAdd() + mpmod) * lvl;
		double mpmin = (t.getLvlMpAdd() * lvl) + mpmod;
		env.incValue((mpmax + mpmin) / 2);
	}
}
