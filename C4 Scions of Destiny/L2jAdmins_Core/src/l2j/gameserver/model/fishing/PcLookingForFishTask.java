package l2j.gameserver.model.fishing;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.fishing.enums.PcFishLureType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class PcLookingForFishTask implements Runnable
{
	private L2PcInstance fisher;
	private PcFishLureType lureType;
	private int fishType;
	private int fishGutsCheck;
	private long endTaskTime;
	
	public PcLookingForFishTask(L2PcInstance fisher, int fishWaitTime, int fishGutsCheck, int fishType, PcFishLureType lureType)
	{
		this.fisher = fisher;
		endTaskTime = System.currentTimeMillis() + fishWaitTime + 10000;
		this.fishGutsCheck = fishGutsCheck;
		this.fishType = fishType;
		this.lureType = lureType;
	}
	
	@Override
	public void run()
	{
		if (System.currentTimeMillis() >= endTaskTime)
		{
			fisher.getFishing().endFishing(false);
			return;
		}
		
		if (fishType == -1)
		{
			return;
		}
		
		if (fishGutsCheck > Rnd.get(1000))
		{
			fisher.getFishing().stopLookingForFishTask();
			fisher.getFishing().startCombat(lureType);
		}
	}
}
