package l2j.gameserver.model.entity.castle.siege.task;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.model.entity.castle.siege.Siege;

/**
 * @author fissban
 */
public class SiegeEndTask implements Runnable
{
	private static final Logger LOG = Logger.getLogger(SiegeEndTask.class.getName());
	private Siege siege;
	
	public SiegeEndTask(Siege siege)
	{
		this.siege = siege;
	}
	
	@Override
	public void run()
	{
		if (!siege.isInProgress())
		{
			return;
		}
		
		try
		{
			long timeRemaining = siege.getSiegeEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			if (timeRemaining > 3600000)
			{
				siege.scheduleEndSiegeTask(timeRemaining - 3600000); // Prepare task for 1 hr left.
			}
			else if ((timeRemaining <= 3600000) && (timeRemaining > 600000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege conclusion.", true);
				siege.scheduleEndSiegeTask(timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege conclusion.", true);
				siege.scheduleEndSiegeTask(timeRemaining - 300000); // Prepare task for 5 minute left.
			}
			else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege conclusion.", true);
				siege.scheduleEndSiegeTask(timeRemaining - 10000); // Prepare task for 10 seconds count down
			}
			else if ((timeRemaining <= 10000) && (timeRemaining > 0))
			{
				siege.announceToPlayer(siege.getCastle().getName() + " siege " + Math.round(timeRemaining / 1000) + " second(s) left!", true);
				siege.scheduleEndSiegeTask(timeRemaining); // Prepare task for second count down
			}
			else
			{
				siege.endSiege();
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
			e.printStackTrace();
		}
	}
}
