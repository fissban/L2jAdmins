package l2j.gameserver.model.actor.manager.character.skills.funcs;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public final class LambdaCalc extends Lambda
{
	private final List<Func> funcs;
	
	public LambdaCalc()
	{
		funcs = new ArrayList<>();
	}
	
	@Override
	public double calc(Env env)
	{
		double saveValue = env.getValue();
		try
		{
			env.setValue(0);
			for (Func f : funcs)
			{
				f.calc(env);
			}
			
			return env.getValue();
		}
		finally
		{
			env.setValue(saveValue);
		}
	}
	
	public void addFunc(Func f)
	{
		funcs.add(f);
	}
	
	public List<Func> getFuncs()
	{
		return funcs;
	}
}
