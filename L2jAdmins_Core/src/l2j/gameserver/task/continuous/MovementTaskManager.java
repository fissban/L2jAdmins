package l2j.gameserver.task.continuous;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * Updates position of moving {@link L2Character} periodically. Task created as separate Thread with MAX_PRIORITY.
 * @author Forsaiken, Hasha
 */
public final class MovementTaskManager extends AbstractTask implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(MovementTaskManager.class.getName());
	
	// Update the position of all moving characters each MILLIS_PER_UPDATE.
	private static final int MILLIS_PER_UPDATE = 100;
	
	private final Set<L2Character> characters = ConcurrentHashMap.newKeySet();
	
	protected MovementTaskManager()
	{
		fixedSchedule(this, MILLIS_PER_UPDATE, MILLIS_PER_UPDATE);
		UtilPrint.result("MovementTaskManager", "Started", "OK");
	}
	
	/**
	 * Add a {@link L2Character} to MovementTask in order to update its location every MILLIS_PER_UPDATE ms.
	 * @param cha The L2Character to add to movingObjects of GameTimeController
	 */
	public final void add(final L2Character cha)
	{
		characters.add(cha);
	}
	
	@Override
	public final void run()
	{
		try
		{
			// For all moving characters.
			for (var character : characters)
			{
				// Update character position, final position isn't reached yet.
				if (!character.updatePosition())
				{
					continue;
				}
				
				// Destination reached, remove from map.
				characters.remove(character);
				
				// Get character AI, if AI doesn't exist, skip.
				final CharacterAI ai = character.getAI();
				if (ai != null)
				{
					// Inform AI about arrival.
					ThreadPoolManager.getInstance().execute(() -> ai.notifyEvent(CtrlEventType.ARRIVED));
				}
			}
		}
		catch (final Exception e)
		{
			LOG.log(Level.WARNING, "", e);
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
