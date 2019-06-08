package l2j.gameserver.model.zone.type;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * A damage zone
 * @author durgus
 */
public class DamageZone extends Zone
{
	private int damagePerSec;
	private int systemMessage;
	private Future<?> task;
	
	public DamageZone(int id)
	{
		super(id);
		
		// Setup default damage
		damagePerSec = 100;
		systemMessage = 686;
		setTargetType(InstanceType.L2Playable); // default only playabale
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("Damage"))
		{
			damagePerSec = Integer.parseInt(value);
		}
		if (name.equals("Message"))
		{
			systemMessage = Integer.parseInt(value);
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
						temp.sendPacket(new SystemMessage(systemMessage));
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
