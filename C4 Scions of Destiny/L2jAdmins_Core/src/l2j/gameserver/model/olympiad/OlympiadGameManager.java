package l2j.gameserver.model.olympiad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.instancemanager.zone.ZoneOlympiadStadiumManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.util.UtilPrint;

/**
 * @author GodKratos, DS
 */
public class OlympiadGameManager implements Runnable
{
	private static final Logger LOG = Logger.getLogger(OlympiadGameManager.class.getName());
	
	private volatile boolean battleStarted = false;
	private List<OlympiadGameTask> tasks = null;
	
	protected OlympiadGameManager()
	{
		final Collection<OlympiadStadiumZone> zones = ZoneOlympiadStadiumManager.getAll();
		if ((zones == null) || zones.isEmpty())
		{
			throw new Error("No olympiad stadium zones defined !");
		}
		
		tasks = new ArrayList<>(zones.size());
		
		zones.forEach(z -> tasks.add(new OlympiadGameTask(z)));
		
		UtilPrint.result("OlympiadGameManager", "Loaded stadiums ", tasks.size());
	}
	
	public List<OlympiadGameTask> getOlympiadTasks()
	{
		return tasks;
	}
	
	protected final boolean isBattleStarted()
	{
		return battleStarted;
	}
	
	protected final void startBattle()
	{
		battleStarted = true;
	}
	
	@Override
	public final void run()
	{
		if (Olympiad.getInstance().isOlympiadEnd())
		{
			return;
		}
		
		if (Olympiad.getInstance().inCompPeriod())
		{
			AbstractOlympiadGame newGame;
			
			List<List<Integer>> readyClassed = OlympiadManager.getInstance().hasEnoughRegisteredClassed();
			boolean readyNonClassed = OlympiadManager.getInstance().hasEnoughRegisteredNonClassed();
			
			int i = -1;
			if ((readyClassed != null) || readyNonClassed)
			{
				i++;
				
				for (OlympiadGameTask task : tasks)
				{
					synchronized (task)
					{
						if (!task.isRunning())
						{
							// Fair arena distribution
							// 0,2,4,6,8.. arenas checked for classed or teams first
							if ((readyClassed != null) && ((i % 2) == 0))
							{
								// if no ready teams found check for classed
								newGame = OlympiadGameClassed.createGame(i, readyClassed);
								if (newGame != null)
								{
									task.attachGame(newGame);
									continue;
								}
								readyClassed = null;
							}
							// 1,3,5,7,9.. arenas used for non-classed
							// also other arenas will be used for non-classed if no classed or teams available
							if (readyNonClassed)
							{
								newGame = OlympiadGameNonClassed.createGame(i, OlympiadManager.getInstance().getRegisteredNonClassBased());
								if (newGame != null)
								{
									task.attachGame(newGame);
									continue;
								}
								readyNonClassed = false;
							}
						}
					}
					
					// stop generating games if no more participants
					if ((readyClassed == null) && !readyNonClassed)
					{
						break;
					}
				}
			}
		}
		else
		{
			// not in competition period
			if (isAllTasksFinished())
			{
				OlympiadManager.getInstance().clearRegistered();
				battleStarted = false;
				LOG.log(Level.INFO, "Olympiad: All current games finished.");
			}
		}
	}
	
	public final boolean isAllTasksFinished()
	{
		for (OlympiadGameTask task : tasks)
		{
			if (task.isRunning())
			{
				return false;
			}
		}
		return true;
	}
	
	public final OlympiadGameTask getOlympiadTask(int id)
	{
		if ((id < 0) || (id >= tasks.size()))
		{
			return null;
		}
		
		return tasks.get(id);
	}
	
	public final int getNumberOfStadiums()
	{
		return tasks.size();
	}
	
	public final void notifyCompetitorDamage(L2PcInstance player, int damage)
	{
		if (player == null)
		{
			return;
		}
		
		final int id = player.getOlympiadGameId();
		if ((id < 0) || (id >= tasks.size()))
		{
			return;
		}
		
		final AbstractOlympiadGame game = tasks.get(id).getGame();
		if (game != null)
		{
			game.addDamage(player, damage);
		}
	}
	
	public static final OlympiadGameManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadGameManager INSTANCE = new OlympiadGameManager();
	}
}
