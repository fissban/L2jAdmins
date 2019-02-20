package l2j.gameserver.model.entity.clanhalls;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.entity.clanhalls.task.ClanHallFunctionTask;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;

/**
 * @author fissban
 */
public class ClanHallFunction
{
	protected static final Logger LOG = Logger.getLogger(ClanHallFunction.class.getName());
	
	private ClanHall ch;
	private ClanHallFunctionType type;
	private int lvl;
	private int fee;
	private long rate;
	private long endTime;
	private Future<?> functionTask;
	
	public ClanHallFunction(ClanHall ch, ClanHallFunctionType type, int lvl, int lease, long rate, long endTime)
	{
		this.ch = ch;
		this.type = type;
		this.lvl = lvl;
		fee = lease;
		this.rate = rate;
		this.endTime = endTime;
		
		initFunctionTask();
	}
	
	public ClanHallFunctionType getType()
	{
		return type;
	}
	
	public int getLvl()
	{
		return lvl;
	}
	
	public int getLease()
	{
		return fee;
	}
	
	public long getRate()
	{
		return rate;
	}
	
	public void setLvl(int lvl)
	{
		this.lvl = lvl;
	}
	
	public void setLease(int lease)
	{
		fee = lease;
	}
	
	public long getEndTime()
	{
		return endTime;
	}
	
	public void setEndTime(long time)
	{
		endTime = time;
	}
	
	public Future<?> getFunctionTask()
	{
		return functionTask;
	}
	
	public void initFunctionTask()
	{
		long currentTime = System.currentTimeMillis();
		if (getEndTime() > currentTime)
		{
			functionTask = ThreadPoolManager.scheduleAtFixedRate(new ClanHallFunctionTask(ch, this), getEndTime() - currentTime, getRate());
		}
		else
		{
			functionTask = ThreadPoolManager.scheduleAtFixedRate(new ClanHallFunctionTask(ch, this), 1000, getRate());
		}
	}
	
	public void cancelFunctionTask()
	{
		if (functionTask != null)
		{
			functionTask.cancel(false);
		}
	}
}
