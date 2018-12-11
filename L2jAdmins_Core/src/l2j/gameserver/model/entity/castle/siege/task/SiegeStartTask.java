package l2j.gameserver.model.entity.castle.siege.task;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.model.entity.castle.siege.Siege;

/**
 * @author fissban
 */
public class SiegeStartTask implements Runnable
{
	private static final Logger LOG = Logger.getLogger(SiegeEndTask.class.getName());
	private Siege siege;
	
	public SiegeStartTask(Siege siege)
	{
		this.siege = siege;
	}
	
	@Override
	public void run()
	{
		if (siege.isInProgress())
		{
			return;
		}
		
		try
		{
			long timeRemaining = siege.getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
			if (timeRemaining > 86400000)
			{
				siege.scheduleStartSiegeTask(timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
			}
			else if ((timeRemaining <= 86400000) && (timeRemaining > 13600000))
			{
				siege.announceToPlayer("The registration term for " + siege.getCastle().getName() + " has ended.", false);
				siege.closeRegistration();
				siege.clearSiegeWaitingClan();
				siege.scheduleStartSiegeTask(timeRemaining - 13600000); // Prepare task for 1 hr left before siege start.
			}
			else if ((timeRemaining <= 13600000) && (timeRemaining > 600000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege begin.", false);
				siege.scheduleStartSiegeTask(timeRemaining - 600000); // Prepare task for 10 minute left.
			}
			else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege begin.", false);
				siege.scheduleStartSiegeTask(timeRemaining - 300000); // Prepare task for 5 minute left.
			}
			else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
			{
				siege.announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + siege.getCastle().getName() + " siege begin.", false);
				siege.scheduleStartSiegeTask(timeRemaining - 10000); // Prepare task for 10 seconds count down
			}
			else if ((timeRemaining <= 10000) && (timeRemaining > 0))
			{
				siege.announceToPlayer(siege.getCastle().getName() + " siege " + Math.round(timeRemaining / 1000) + " second(s) to start!", false);
				siege.scheduleStartSiegeTask(timeRemaining); // Prepare task for second count down
			}
			else
			{
				siege.startSiege();
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
			e.printStackTrace();
		}
	}
}
