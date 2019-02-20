package l2j.gameserver.model.drop.task;

import java.util.concurrent.ScheduledFuture;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author DrHouse
 */
public class DropProtectionTask implements Runnable
{
	private static final int PROTECTED_MILLIS_TIME = 15000;
	
	private volatile boolean isProtected = false;
	private L2PcInstance owner = null;
	private ScheduledFuture<?> task = null;
	
	@Override
	public synchronized void run()
	{
		isProtected = false;
		owner = null;
		
		task.cancel(false);
		task = null;
	}
	
	public boolean isProtected()
	{
		return isProtected;
	}
	
	public L2PcInstance getOwner()
	{
		return owner;
	}
	
	public synchronized boolean tryPickUp(L2PcInstance actor)
	{
		if (!isProtected)
		{
			return true;
		}
		
		if (owner == actor)
		{
			return true;
		}
		
		if ((owner.getParty() != null) && (owner.getParty() == actor.getParty()))
		{
			return true;
		}
		
		return false;
	}
	
	private void unprotect()
	{
		if (task != null)
		{
			task.cancel(false);
		}
		
		isProtected = false;
		owner = null;
		task = null;
	}
	
	public synchronized void protect(L2PcInstance player)
	{
		unprotect();
		
		isProtected = true;
		
		if ((owner = player) == null)
		{
			throw new NullPointerException("Trying to protect dropped item to null owner");
		}
		
		task = ThreadPoolManager.schedule(this, PROTECTED_MILLIS_TIME);
	}
}
