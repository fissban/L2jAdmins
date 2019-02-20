package l2j.gameserver.model.zone.type;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.zone.Zone;

/**
 * A damage zone
 * @author durgus
 */
public class DamageZone extends Zone
{
	private int damagePerSec;
	private Future<?> task;
	
	public DamageZone(int id)
	{
		super(id);
		
		// Setup default damage
		damagePerSec = 100;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dmgSec"))
		{
			damagePerSec = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (task == null)
		{
			// Apply dmg
			task = ThreadPoolManager.scheduleAtFixedRate(() ->
			{
				for (L2Character temp : getCharacterList())
				{
					if ((temp != null) && !temp.isDead())
					{
						temp.reduceCurrentHp(getDamagePerSecond(), null);
					}
				}
			}, 10, 1000);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (characterList.isEmpty())
		{
			task.cancel(true);
			task = null;
		}
	}
	
	protected int getDamagePerSecond()
	{
		return damagePerSec;
	}
}
