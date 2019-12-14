package l2j.gameserver.model.actor.manager.character.skills.stats;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.manager.character.skills.funcs.Func;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REG_HP_RATE...). In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR>
 * FuncAtkAccuracy -> Math.sqrt(player.getDEX())*6+_player.getLevel()<BR>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>. Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order. The result of the calculation is stored in the
 * value property of an Env class instance.<BR>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR>
 */
public final class Calculator
{
	/** Table of Func object */
	private Func[] functions;
	
	/**
	 * Constructor of Calculator (Init value : emptyFuncs).
	 */
	public Calculator()
	{
		functions = new Func[0];
	}
	
	/**
	 * Constructor of Calculator (Init value : Calculator c).
	 * @param c
	 */
	public Calculator(Calculator c)
	{
		functions = c.functions;
	}
	
	/**
	 * @return true if Funcs in the Calculator is empty.
	 */
	public boolean isEmpty()
	{
		return functions.length == 0;
	}
	
	/**
	 * Add a Func to the Calculator.
	 * @param f
	 */
	public synchronized void addFunc(Func f)
	{
		Func[] funcs = functions;
		Func[] tmp = new Func[funcs.length + 1];
		
		final int order = f.order;
		int i;
		
		for (i = 0; (i < funcs.length) && (order >= funcs[i].order); i++)
		{
			tmp[i] = funcs[i];
		}
		
		tmp[i] = f;
		
		for (; i < funcs.length; i++)
		{
			tmp[i + 1] = funcs[i];
		}
		
		functions = tmp;
	}
	
	/**
	 * Remove a Func from the Calculator.
	 * @param f
	 */
	public synchronized void removeFunc(Func f)
	{
		Func[] funcs = functions;
		Func[] tmp = new Func[funcs.length - 1];
		
		int i;
		
		for (i = 0; (i < funcs.length) && (f != funcs[i]); i++)
		{
			tmp[i] = funcs[i];
		}
		
		if (i == funcs.length)
		{
			return;
		}
		
		for (i++; i < funcs.length; i++)
		{
			tmp[i - 1] = funcs[i];
		}
		
		if (tmp.length == 0)
		{
			functions = new Func[0];
		}
		else
		{
			functions = tmp;
		}
	}
	
	/**
	 * Remove each Func with the specified owner of the Calculator.
	 * @param  owner
	 * @return
	 */
	public synchronized List<StatsType> removeOwner(Object owner)
	{
		List<StatsType> modifiedStats = new ArrayList<>();
		
		for (Func func : functions)
		{
			if (func.owner == owner)
			{
				modifiedStats.add(func.stat);
				removeFunc(func);
			}
		}
		
		return modifiedStats;
	}
	
	/**
	 * Run each Func of the Calculator.
	 * @param env
	 */
	public void calc(Env env)
	{
		for (Func func : functions)
		{
			func.calc(env);
		}
	}
}
