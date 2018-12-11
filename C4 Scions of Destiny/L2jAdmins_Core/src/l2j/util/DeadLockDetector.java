package l2j.util;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.Shutdown;
import l2j.gameserver.data.AnnouncementsData;

/**
 * @author -Nemesiss- L2M
 */
public class DeadLockDetector extends Thread
{
	private static Logger log = Logger.getLogger(DeadLockDetector.class.getName());
	
	private static final int sleepTime = Config.DEADLOCK_CHECK_INTERVAL * 1000;
	
	private final ThreadMXBean tmx;
	
	public DeadLockDetector()
	{
		super("DeadLockDetector");
		tmx = ManagementFactory.getThreadMXBean();
	}
	
	@Override
	public final void run()
	{
		boolean deadlock = false;
		while (!deadlock)
		{
			try
			{
				long[] ids = tmx.findDeadlockedThreads();
				
				if (ids != null)
				{
					deadlock = true;
					ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
					String info = "DeadLock Found!\n";
					for (ThreadInfo ti : tis)
					{
						info += ti.toString();
					}
					
					for (ThreadInfo ti : tis)
					{
						LockInfo[] locks = ti.getLockedSynchronizers();
						MonitorInfo[] monitors = ti.getLockedMonitors();
						if ((locks.length == 0) && (monitors.length == 0))
						{
							continue;
						}
						
						ThreadInfo dl = ti;
						info += "Java-level deadlock:\n";
						info += "\t" + dl.getThreadName() + " is waiting to lock " + dl.getLockInfo().toString() + " which is held by " + dl.getLockOwnerName() + "\n";
						while ((dl = tmx.getThreadInfo(new long[]
						{
							dl.getLockOwnerId()
						}, true, true)[0]).getThreadId() != ti.getThreadId())
						{
							info += "\t" + dl.getThreadName() + " is waiting to lock " + dl.getLockInfo().toString() + " which is held by " + dl.getLockOwnerName() + "\n";
						}
					}
					log.warning(info);
					
					if (Config.RESTART_ON_DEADLOCK)
					{
						AnnouncementsData an = AnnouncementsData.getInstance();
						an.announceToAll("Server has stability issues - restarting now.");
						Shutdown.getInstance().startTelnetShutdown("DeadLockDetector - Auto Restart", 60, true);
					}
				}
				Thread.sleep(sleepTime);
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "DeadLockDetector: ", e);
			}
		}
	}
}
