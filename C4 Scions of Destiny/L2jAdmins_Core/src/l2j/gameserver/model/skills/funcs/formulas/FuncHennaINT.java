package l2j.gameserver.model.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;

/**
 * @author fissban
 */
public class FuncHennaINT extends Func
{
	private static final FuncHennaINT fh_instance = new FuncHennaINT();
	
	public static Func getInstance()
	{
		return fh_instance;
	}
	
	private FuncHennaINT()
	{
		super(StatsType.STAT_INT, 0x10, null);
	}
	
	@Override
	public void calc(Env env)
	{
		L2PcInstance pc = (L2PcInstance) env.getPlayer();
		if (pc != null)
		{
			env.incValue(pc.getHennaStatINT());
		}
	}
}
