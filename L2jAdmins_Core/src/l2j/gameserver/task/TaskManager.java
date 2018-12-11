package l2j.gameserver.task;

import java.util.logging.Logger;

import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import l2j.gameserver.task.continuous.DecayTaskManager;
import l2j.gameserver.task.continuous.GameTimeTaskManager;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;
import l2j.gameserver.task.continuous.KnownListUpdateTaskManager;
import l2j.gameserver.task.continuous.MovementTaskManager;
import l2j.gameserver.task.scheduled.AdjustCastleNewDayTaskManager;
import l2j.gameserver.task.scheduled.AdjustCastleNewTaxRateTaskManager;
import l2j.gameserver.task.scheduled.GarbageCollector;
import l2j.gameserver.task.scheduled.OlympiadSaveTaskManager;
import l2j.gameserver.task.scheduled.RecomTaskManager;
import l2j.gameserver.task.scheduled.RestartTaskManager;
import l2j.gameserver.task.scheduled.SevenSignsUpdateTaskManager;
import l2j.gameserver.task.scheduled.ShutdownTaskManager;

/**
 * @author fissban
 */
public final class TaskManager
{
	protected static final Logger LOG = Logger.getLogger(TaskManager.class.getName());
	
	// TODO
	// Can create an XML with the task to be loaded and some data from them.
	
	public TaskManager()
	{
		//
	}
	
	public void init()
	{
		// scheduled
		AdjustCastleNewDayTaskManager.getInstance();
		AdjustCastleNewTaxRateTaskManager.getInstance();
		OlympiadSaveTaskManager.getInstance();
		RecomTaskManager.getInstance();
		RestartTaskManager.getInstance();
		SevenSignsUpdateTaskManager.getInstance();
		ShutdownTaskManager.getInstance();
		GarbageCollector.getInstance();
		// continuous
		GameTimeTaskManager.getInstance();
		KnownListUpdateTaskManager.getInstance();
		DecayTaskManager.getInstance();
		MovementTaskManager.getInstance();
		AttackStanceTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
	}
	
	public static TaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TaskManager INSTANCE = new TaskManager();
	}
}
