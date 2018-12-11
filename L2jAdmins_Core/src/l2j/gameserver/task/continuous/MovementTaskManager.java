package l2j.gameserver.task.continuous;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.util.UtilPrint;

/**
 * Updates position of moving {@link L2Character} periodically. Task created as separate Thread with MAX_PRIORITY.
 * @author Forsaiken, Hasha
 */
public final class MovementTaskManager extends Thread
{
	protected static final Logger LOG = Logger.getLogger(MovementTaskManager.class.getName());
	
	// Update the position of all moving characters each MILLIS_PER_UPDATE.
	private static final int MILLIS_PER_UPDATE = 100;
	
	private final Map<Integer, L2Character> characters = new ConcurrentHashMap<>();
	
	protected MovementTaskManager()
	{
		super("MovementTaskManager");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		super.start();
		UtilPrint.result("MovementTaskManager", "Started", "OK");
	}
	
	/**
	 * Add a {@link L2Character} to MovementTask in order to update its location every MILLIS_PER_UPDATE ms.
	 * @param cha The L2Character to add to movingObjects of GameTimeController
	 */
	public final void add(final L2Character cha)
	{
		characters.putIfAbsent(cha.getObjectId(), cha);
	}
	
	@Override
	public final void run()
	{
		long time = System.currentTimeMillis();
		
		while (true)
		{
			// set next check time
			time += MILLIS_PER_UPDATE;
			
			try
			{
				// For all moving characters.
				for (Iterator<Entry<Integer, L2Character>> iterator = characters.entrySet().iterator(); iterator.hasNext();)
				{
					// Get entry of current iteration.
					Entry<Integer, L2Character> entry = iterator.next();
					
					// Get character.
					L2Character character = entry.getValue();
					
					// Update character position, final position isn't reached yet.
					if (!character.updatePosition())
					{
						continue;
					}
					
					// Destination reached, remove from map.
					iterator.remove();
					
					// Get character AI, if AI doesn't exist, skip.
					final CharacterAI ai = character.getAI();
					if (ai == null)
					{
						continue;
					}
					
					// Inform AI about arrival.
					ThreadPoolManager.getInstance().execute(() ->
					{
						try
						{
							ai.notifyEvent(CtrlEventType.ARRIVED);
						}
						catch (final Throwable e)
						{
							LOG.log(Level.WARNING, "", e);
						}
					});
				}
			}
			catch (final Throwable e)
			{
				LOG.log(Level.WARNING, "", e);
			}
			
			// Sleep thread till next tick.
			long sleepTime = time - System.currentTimeMillis();
			if (sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (final InterruptedException e)
				{
					
				}
			}
		}
	}
	
	public static final MovementTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MovementTaskManager INSTANCE = new MovementTaskManager();
	}
}
