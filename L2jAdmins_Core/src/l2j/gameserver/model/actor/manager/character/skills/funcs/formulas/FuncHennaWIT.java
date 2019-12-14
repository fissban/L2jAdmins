package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class FuncHennaWIT extends Func
{
	private static final FuncHennaWIT fh_instance = new FuncHennaWIT();
	
	public static Func getInstance()
	{
		return fh_instance;
	}
	
	private FuncHennaWIT()
	{
		super(StatsType.STAT_WIT, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		L2PcInstance pc = (L2PcInstance) env.getPlayer();
		if (pc != null)
		{
			env.incValue(pc.getHennaStatWIT());
		}
	}
}
