package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public abstract class Condition
{
	private Condition listener;
	private String msg;
	
	private boolean result;
	
	public final void setMessage(String msg)
	{
		this.msg = msg;
	}
	
	public final String getMessage()
	{
		return msg;
	}
	
	public void setListener(Condition listener)
	{
		this.listener = listener;
		notifyChanged();
	}
	
	public Condition getListener()
	{
		return listener;
	}
	
	public final boolean test(Env env)
	{
		boolean res = testImpl(env);
		if ((listener != null) && (res != result))
		{
			result = res;
			notifyChanged();
		}
		return res;
	}
	
	public abstract boolean testImpl(Env env);
	
	public void notifyChanged()
	{
		if (listener != null)
		{
			listener.notifyChanged();
		}
	}
}
