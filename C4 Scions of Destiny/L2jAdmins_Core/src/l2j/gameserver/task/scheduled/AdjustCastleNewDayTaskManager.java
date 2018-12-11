package l2j.gameserver.task.scheduled;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * Charge class to initialize the variables required daily.
 * @author fissban
 */
public class AdjustCastleNewDayTaskManager extends AbstractTask implements Runnable
{
	public AdjustCastleNewDayTaskManager()
	{
		specificSchedule(this, 1, "00:00:00");
		UtilPrint.result("AdjustCastleNewDayTaskManager", "", "OK");
	}
	
	@Override
	public void run()
	{
		// Castles are enabled to apply their next task rate.
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			castle.setNewDay();
		}
		
		LOG.config("Adjust new day global task: Started.");
	}
	
	public final static AdjustCastleNewDayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdjustCastleNewDayTaskManager INSTANCE = new AdjustCastleNewDayTaskManager();
	}
}
