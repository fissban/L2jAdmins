package l2j.gameserver.task.continuous;

import l2j.Config;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.world.L2WorldRegion;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * Periodically updates known list of all existing {@link L2Character}.<br>
 * Special scope is used for {@link L2WorldRegion} without {@link L2PcInstance} inside.
 * @author Hasha
 */
public final class KnownListUpdateTaskManager extends AbstractTask implements Runnable
{
	// Update for NPCs is performed each FULL_UPDATE tick interval.
	private static final int FULL_UPDATE = 10;
	
	private boolean flagForgetAdd = true;
	private int timer = FULL_UPDATE;
	
	protected KnownListUpdateTaskManager()
	{
		fixedSchedule(this, Config.KNOWNLIST_UPDATE_INTERVAL, Config.KNOWNLIST_UPDATE_INTERVAL);
		UtilPrint.result("KnownListUpdateTaskManager", "Started", "OK");
	}
	
	@Override
	public final void run()
	{
		// Decrease and reset full iteration timer.
		if (--timer == 0)
		{
			timer = FULL_UPDATE;
		}
		
		// When iteration timer is 1, 2, perform forget and add for NPCs.
		final boolean fullUpdate = timer < 3;
		
		// Swap forget/add flag for this iteration.
		flagForgetAdd = !flagForgetAdd;
		
		// Go through all world regions.
		for (L2WorldRegion regions[] : L2World.getInstance().getAllWorldRegions())
		{
			for (L2WorldRegion region : regions)
			{
				// Skip inactive regions unless full update (knownlist can be still updated regardless AI active or detached).
				if (!region.isActive() && !fullUpdate)
				{
					continue;
				}
				
				// Go through all visible objects.
				for (L2Object object : region.getVisibleObjects().values())
				{
					// don't busy about objects lower than L2Character.
					if (!(object instanceof L2Character) || !object.isVisible())
					{
						continue;
					}
					
					final boolean isPlayable = object instanceof L2Playable;
					final boolean isAttackable = object instanceof L2Attackable;
					
					// When one of these conditions below is passed performs forget objects (which are beyond forget distance) or add objects from surrounding regions (which are closer than detect distance)
					// 1) object is non-attackable and non-playable (NPCs) -> each FULL_UPDATE_TIMER iterations
					// 2) object is playable (players, summons, pets) -> each iteration
					// 3) object is attackable (monsters, raids, etc) -> each iteration
					if (fullUpdate || isPlayable || isAttackable)
					{
						// One iteration performs object forget.
						if (flagForgetAdd)
						{
							object.getKnownList().forgetObjects();
							// The other iteration performs object add.
						}
						else
						{
							for (L2WorldRegion surroundingRegion : region.getSurroundingRegions())
							{
								// Object is a monster and surrounding region does not contain playable, skip.
								if (isAttackable && !surroundingRegion.isActive())
								{
									continue;
								}
								
								for (L2Object o : surroundingRegion.getVisibleObjects().values())
								{
									if (o != object)
									{
										object.getKnownList().addObject(o);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static final KnownListUpdateTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final KnownListUpdateTaskManager INSTANCE = new KnownListUpdateTaskManager();
	}
}
