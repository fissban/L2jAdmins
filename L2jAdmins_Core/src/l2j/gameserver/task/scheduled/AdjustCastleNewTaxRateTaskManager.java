package l2j.gameserver.task.scheduled;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class AdjustCastleNewTaxRateTaskManager extends AbstractTask implements Runnable
{
	public AdjustCastleNewTaxRateTaskManager()
	{
		specificSchedule(this, 1, "12:00:00");
		UtilPrint.result("AdjustCastleNewTaxRateTaskManager", "", "OK");
	}
	
	@Override
	public void run()
	{
		// Castles are enabled to apply their next task rate
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			if (castle.isApplyNewTaxRate())
			{
				castle.setTaxRateInDB(castle.getNextTaxRatePorcent());
			}
		}
		
		LOG.config("Adjust new global tax rate for castle: Started.");
	}
	
	public final static AdjustCastleNewTaxRateTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdjustCastleNewTaxRateTaskManager INSTANCE = new AdjustCastleNewTaxRateTaskManager();
	}
}
