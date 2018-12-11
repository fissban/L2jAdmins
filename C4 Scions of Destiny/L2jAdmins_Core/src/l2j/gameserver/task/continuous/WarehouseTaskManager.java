package l2j.gameserver.task.continuous;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.task.AbstractTask;

/**
 * @author -Nemesiss-
 */
public class WarehouseTaskManager extends AbstractTask implements Runnable
{
	protected final Map<L2PcInstance, Long> cachedWh = new ConcurrentHashMap<>();
	protected final long cacheTime;
	
	public WarehouseTaskManager()
	{
		cacheTime = Config.WAREHOUSE_CACHE_TIME * 60 * 1000;
		fixedSchedule(this, 120000, 60000);
	}
	
	public void addCacheTask(L2PcInstance pc)
	{
		cachedWh.put(pc, System.currentTimeMillis());
	}
	
	public void remCacheTask(L2PcInstance pc)
	{
		cachedWh.remove(pc);
	}
	
	@Override
	public void run()
	{
		long currentTime = System.currentTimeMillis();
		for (Iterator<Entry<L2PcInstance, Long>> iterator = cachedWh.entrySet().iterator(); iterator.hasNext();)
		{
			// Get entry of current iteration.
			Entry<L2PcInstance, Long> entry = iterator.next();
			
			L2PcInstance player = entry.getKey();
			long time = entry.getValue();
			
			if ((currentTime - time) > cacheTime)
			{
				// Clear player Warehouse
				player.clearWarehouse();
				// Remove player from list.
				iterator.remove();
			}
		}
	}
	
	public static WarehouseTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WarehouseTaskManager INSTANCE = new WarehouseTaskManager();
	}
}
