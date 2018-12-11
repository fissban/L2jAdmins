package l2j.gameserver.task.scheduled;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * Updates all data for the Seven Signs and Festival of Darkness engines, when time is elapsed.
 * @author Tempy
 */
public class SevenSignsUpdateTaskManager extends AbstractTask implements Runnable
{
	public SevenSignsUpdateTaskManager()
	{
		fixedSchedule(this, 900000, 1800000);
		UtilPrint.result("SevenSignsUpdateTaskManager", "", "OK");
	}
	
	@Override
	public void run()
	{
		try
		{
			SevenSignsManager.getInstance().saveSevenSignsData(null, true);
			
			if (!SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				SevenSignsFestival.getInstance().saveFestivalData(false);
			}
			
			LOG.info("SevenSigns: Data updated successfully.");
		}
		catch (Exception e)
		{
			LOG.warning("SevenSigns: Failed to save Seven Signs configuration: " + e);
		}
	}
	
	public final static SevenSignsUpdateTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SevenSignsUpdateTaskManager INSTANCE = new SevenSignsUpdateTaskManager();
	}
}
