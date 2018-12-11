package l2j.gameserver.task.scheduled;

import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class GarbageCollector extends AbstractTask implements Runnable
{
	public GarbageCollector()
	{
		fixedSchedule(this, 1800000, 3600000);
		UtilPrint.result("GarbageCollector", "", "OK");
	}
	
	@Override
	public void run()
	{
		Runtime.getRuntime().gc();
	}
	
	public final static GarbageCollector getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GarbageCollector INSTANCE = new GarbageCollector();
	}
}
