package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mr
 */
public class ConditionPlayerHp extends Condition
{
	final int hp;
	
	public ConditionPlayerHp(int hp)
	{
		this.hp = hp;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return ((env.getPlayer().getCurrentHp() * 100) / env.getPlayer().getStat().getMaxHp()) <= hp;
	}
}
