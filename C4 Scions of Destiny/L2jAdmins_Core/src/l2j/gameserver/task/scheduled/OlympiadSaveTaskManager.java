package l2j.gameserver.task.scheduled;

import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * Updates all data of Olympiad nobles in db
 * @author godson
 */
public class OlympiadSaveTaskManager extends AbstractTask implements Runnable
{
	public OlympiadSaveTaskManager()
	{
		fixedSchedule(this, 900000, 1800000);
		UtilPrint.result("OlympiadSaveTaskManager", "", "OK");
	}
	
	@Override
	public void run()
	{
		if (Olympiad.getInstance().inCompPeriod())
		{
			Olympiad.getInstance().saveOlympiadStatus();
			
			LOG.info("TaskOlympiadSave: Data updated successfully.");
		}
	}
	
	public final static OlympiadSaveTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadSaveTaskManager INSTANCE = new OlympiadSaveTaskManager();
	}
}
