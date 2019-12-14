package l2j.gameserver.model.actor.manager.character.skills.funcs;

import l2j.gameserver.model.actor.manager.character.skills.conditions.Condition;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * A Func object is a component of a Calculator created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REG_HP_RATE...). In fact, each calculator is a table of Func object in which each Func represents a mathematics function :<BR>
 * FuncAtkAccuracy -> Math.sqrt(player.getDEX())*6+_player.getLevel()<BR>
 * When the calc method of a calculator is launched, each mathematics function is called according to its priority <B>_order</B>. Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order. The result of the calculation is stored in the
 * value property of an Env class instance.
 */
public abstract class Func
{
	/** Statistics, that is affected by this function (See L2Character.CALCULATOR_XXX constants) */
	public final StatsType stat;
	/**
	 * Order of functions calculation. Functions with lower order are executed first. Functions with the same order are executed in unspecified order. Usually add/substruct functions has lowest order, then bonus/penalty functions (multiply/divide) are applied, then functions that do more complex
	 * calculations (non-linear functions).
	 */
	public final int order;
	/** Owner can be an armor, weapon, skill, system event, quest, etc Used to remove all functions added by this owner. */
	public final Object owner;
	/** Function may be disabled by attached condition. */
	public Condition cond;
	
	/**
	 * Constructor of Func.
	 * @param stat
	 * @param order
	 * @param owner
	 */
	public Func(StatsType stat, int order, Object owner)
	{
		this.stat = stat;
		this.order = order;
		this.owner = owner;
	}
	
	/**
	 * Add a condition to the Func.
	 * @param cond
	 */
	public void setCondition(Condition cond)
	{
		this.cond = cond;
	}
	
	/**
	 * Run the mathematics function of the Func.
	 * @param env
	 */
	public abstract void calc(Env env);
}
