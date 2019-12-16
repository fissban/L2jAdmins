package l2j.gameserver.model.actor.manager.character.skills.funcs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import l2j.gameserver.model.actor.manager.character.skills.conditions.Condition;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;

/**
 * @author mkizub
 */
public final class FuncTemplate
{
	public Condition attachCond;
	public Condition applayCond;
	public final Class<?> func;
	public final Constructor<?> constructor;
	public final StatsType stat;
	public final int order;
	public final Lambda lambda;
	
	public FuncTemplate(Condition attachCond, Condition applayCond, String func, StatsType stat, int order, Lambda lambda)
	{
		this.attachCond = attachCond;
		this.applayCond = applayCond;
		this.stat = stat;
		this.order = order;
		this.lambda = lambda;
		
		try
		{
			this.func = Class.forName("l2j.gameserver.model.actor.manager.character.skills.funcs.Func" + func);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		try
		{
			constructor = this.func.getConstructor(new Class[]
			{
				StatsType.class, // stats to update
				Integer.TYPE, // order of execution
				Object.class, // owner
				Lambda.class
				// value for function
			});
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public Func getFunc(Env env, Object owner)
	{
		if ((attachCond != null) && !attachCond.test(env))
		{
			return null;
		}
		try
		{
			Func f = (Func) constructor.newInstance(stat, order, owner, lambda);
			if (applayCond != null)
			{
				f.setCondition(applayCond);
			}
			return f;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
