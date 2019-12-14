package l2j.gameserver.model.actor.manager.character.skills.funcs.formulas;

import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionUsingItemType;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.items.enums.WeaponType;

/**
 * @author fissban
 */
public class FuncBowAtkRange extends Func
{
	private static final FuncBowAtkRange fbar_instance = new FuncBowAtkRange();
	
	public static Func getInstance()
	{
		return fbar_instance;
	}
	
	private FuncBowAtkRange()
	{
		super(StatsType.PHYSICAL_ATTACK_RANGE, 0x10, null);
		setCondition(new ConditionUsingItemType(WeaponType.BOW.mask()));
	}
	
	@Override
	public void calc(Env env)
	{
		if (!cond.test(env))
		{
			return;
		}
		env.incValue(450);
	}
}
