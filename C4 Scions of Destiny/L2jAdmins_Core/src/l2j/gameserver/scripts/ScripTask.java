package l2j.gameserver.scripts;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class ScripTask
{
	protected static final Logger LOG = Logger.getLogger(ScripTask.class.getName());
	
	protected final Script quest;
	protected final String name;
	protected final L2Npc npc;
	protected final L2PcInstance player;
	protected final boolean isRepeating;
	
	protected boolean isActive = true;
	
	private ScheduledFuture<?> schedular;
	
	public ScripTask(Script quest, String name, L2Npc npc, L2PcInstance player, long time, boolean repeating)
	{
		this.quest = quest;
		this.name = name;
		this.npc = npc;
		this.player = player;
		isRepeating = repeating;
		
		if (repeating)
		{
			schedular = ThreadPoolManager.scheduleAtFixedRate(new ScheduleTimerTask(), time, time);
		}
		else
		{
			schedular = ThreadPoolManager.schedule(new ScheduleTimerTask(), time);
		}
	}
	
	protected final class ScheduleTimerTask implements Runnable
	{
		@Override
		public void run()
		{
			if (!isActive)
			{
				return;
			}
			
			if (!isRepeating)
			{
				cancel();
			}
			
			quest.notifyEvent(name, npc, player);
		}
	}
	
	public final void cancel()
	{
		isActive = false;
		
		if (schedular != null)
		{
			schedular.cancel(false);
		}
		
		quest.removeTimer(this);
	}
	
	/**
	 * public method to compare if this timer matches with the key attributes passed.
	 * @param  quest  : Quest instance to which the timer is attached
	 * @param  name   : Name of the timer
	 * @param  npc    : Npc instance attached to the desired timer (null if no npc attached)
	 * @param  player : Player instance attached to the desired timer (null if no player attached)
	 * @return        boolean
	 */
	public final boolean equals(Script quest, String name, L2Npc npc, L2PcInstance player)
	{
		if ((quest == null) || (this.quest != quest))
		{
			return false;
		}
		
		if ((name == null) || !this.name.equals(name))
		{
			return false;
		}
		
		return ((this.npc == npc) && (this.player == player));
	}
	
	public Script getQuest()
	{
		return quest;
	}
	
	public String getName()
	{
		return name;
	}
	
	public L2Npc getNpc()
	{
		return npc;
	}
	
	public L2PcInstance getPlayer()
	{
		return player;
	}
	
	public boolean isRepeating()
	{
		return isRepeating;
	}
	
	public boolean isActive()
	{
		return isActive;
	}
}
