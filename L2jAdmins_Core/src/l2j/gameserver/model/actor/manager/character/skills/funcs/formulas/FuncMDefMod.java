package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.BaseStatsType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.items.enums.ParpedollType;

/**
 * @author fissban, zarie
 */
public class FuncMDefMod extends Func
{
	private static final FuncMDefMod fmm_instance = new FuncMDefMod();
	
	public static Func getInstance()
	{
		return fmm_instance;
	}
	
	private FuncMDefMod()
	{
		super(StatsType.MAGICAL_DEFENCE, 0x20, null);
	}
	
	@Override
	public void calc(Env env)
	{
		if (env.getPlayer() instanceof L2PcInstance)
		{
			L2PcInstance p = env.getPlayer().getActingPlayer();
			if (p.getInventory().getPaperdollItem(ParpedollType.LFINGER) != null)
			{
				env.decValue(5);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.RFINGER) != null)
			{
				env.decValue(5);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.LEAR) != null)
			{
				env.decValue(9);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.REAR) != null)
			{
				env.decValue(9);
			}
			if (p.getInventory().getPaperdollItem(ParpedollType.NECK) != null)
			{
				env.decValue(13);
			}
			
			env.mulValue(BaseStatsType.MEN.calcBonus(env.getPlayer()) * env.getPlayer().getLevelMod());
		}
		else if (env.getPlayer() instanceof L2PetInstance)
		{
			env.setValue(((L2PetInstance) env.getPlayer()).getPetData().getPetMDef());
			return;
		}
		else
		{
			env.mulValue(BaseStatsType.MEN.calcBonus(10) * env.getPlayer().getLevelMod());
		}
	}
}
