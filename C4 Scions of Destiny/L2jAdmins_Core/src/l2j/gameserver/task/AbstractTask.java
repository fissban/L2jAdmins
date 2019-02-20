package l2j.gameserver.task;

import java.util.Calendar;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;

/**
 * @author Layane, fissban
 */
public abstract class AbstractTask
{
	protected static final Logger LOG = Logger.getLogger(AbstractTask.class.getName());
	
	/**
	 * @param task
	 * @param delay
	 */
	public void generalSchedule(Runnable task, long delay)
	{
		ThreadPoolManager.schedule(task, delay);
	}
	
	/**
	 * @param task
	 * @param delay
	 * @param interval
	 */
	public void fixedSchedule(Runnable task, long delay, long interval)
	{
		ThreadPoolManager.scheduleAtFixedRate(task, delay, interval);
	}
	
	/**
	 * @param task
	 * @param interval -> It defined every few days the script runs
	 * @param time     -> format "12:00:00"
	 */
	public void specificSchedule(Runnable task, int interval, String time)
	{
		long countDay = interval * 86400000L;
		
		// parse time
		int hour = Integer.parseInt(time.split(":")[0]);
		int minutes = Integer.parseInt(time.split(":")[1]);
		int seconds = Integer.parseInt(time.split(":")[2]);
		
		Calendar check = Calendar.getInstance();
		// check.setTimeInMillis(task.getLastActivation() + interval);
		
		Calendar min = Calendar.getInstance();
		min.set(Calendar.HOUR_OF_DAY, hour);
		min.set(Calendar.MINUTE, minutes);
		min.set(Calendar.SECOND, seconds);
		
		long delay = min.getTimeInMillis() - System.currentTimeMillis();
		
		if (check.after(min) || (delay < 0))
		{
			delay += countDay;
		}
		
		ThreadPoolManager.scheduleAtFixedRate(task, delay, countDay);
	}
}
