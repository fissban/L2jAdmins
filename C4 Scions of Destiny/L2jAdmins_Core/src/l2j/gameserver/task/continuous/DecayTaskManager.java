package l2j.gameserver.task.continuous;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * @author la2 Lets drink to code!
 */
public class DecayTaskManager extends AbstractTask implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(DecayTaskManager.class.getName());
	protected Map<L2Character, Long> decayTasks = new ConcurrentHashMap<>();
	
	private static final int NPC_DECAY_TIME = 8500;
	private static final int RAID_DECAY_TIME = 30000;
	private static final int SPOILED_AND_SEED_DECAY_TIME = 18500;
	
	public DecayTaskManager()
	{
		fixedSchedule(this, 10000, 5000);
		UtilPrint.result("DecayTaskManager", "Started", "OK");
	}
	
	public void addDecayTask(L2Character actor)
	{
		decayTasks.put(actor, System.currentTimeMillis());
	}
	
	public void addDecayTask(L2Character actor, int interval)
	{
		decayTasks.put(actor, System.currentTimeMillis() + interval);
	}
	
	public void cancelDecayTask(L2Character actor)
	{
		try
		{
			decayTasks.remove(actor);
		}
		catch (NoSuchElementException e)
		{
		}
	}
	
	@Override
	public void run()
	{
		Long currentTime = System.currentTimeMillis();
		int delay = NPC_DECAY_TIME;
		
		try
		{
			for (Iterator<Entry<L2Character, Long>> iterator = decayTasks.entrySet().iterator(); iterator.hasNext();)
			{
				// Get entry of current iteration.
				Entry<L2Character, Long> entry = iterator.next();
				
				L2Character actor = entry.getKey();
				long time = entry.getValue();
				
				if (actor instanceof L2RaidBossInstance)
				{
					delay = RAID_DECAY_TIME;
				}
				else if (actor instanceof L2Attackable)
				{
					final L2Attackable npc = ((L2Attackable) actor);
					
					if (npc.isSpoil() || npc.isSeeded())
					{
						delay = SPOILED_AND_SEED_DECAY_TIME;
					}
				}
				
				if ((currentTime - time) > delay)
				{
					actor.deleteMe();
					iterator.remove();
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error in DecayScheduler: " + e.getMessage() + e);
			e.printStackTrace();
		}
	}
	
	public static DecayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DecayTaskManager INSTANCE = new DecayTaskManager();
	}
}
