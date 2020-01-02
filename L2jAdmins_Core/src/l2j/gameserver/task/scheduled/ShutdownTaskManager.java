package l2j.gameserver.task.scheduled;

import l2j.gameserver.Shutdown;
import l2j.gameserver.task.AbstractTask;

/**
 * @author Layane
 */
public class ShutdownTaskManager extends AbstractTask implements Runnable
{
	public ShutdownTaskManager()
	{
		//
	}
	
	@Override
	public void run()
	{
		Shutdown.getInstance().startShutdown(50, false);
	}
	
	public final static ShutdownTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ShutdownTaskManager INSTANCE = new ShutdownTaskManager();
	}
}
