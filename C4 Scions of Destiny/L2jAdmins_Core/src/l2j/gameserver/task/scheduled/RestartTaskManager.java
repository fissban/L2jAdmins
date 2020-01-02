package l2j.gameserver.task.scheduled;

import l2j.gameserver.Shutdown;
import l2j.gameserver.task.AbstractTask;

/**
 * @author Layane
 */
public final class RestartTaskManager extends AbstractTask implements Runnable
{
	public RestartTaskManager()
	{
		//
	}
	
	@Override
	public void run()
	{
		Shutdown.getInstance().startShutdown(50, true);
	}
	
	public final static RestartTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RestartTaskManager INSTANCE = new RestartTaskManager();
	}
}
