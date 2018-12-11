package l2j.gameserver.task.continuous;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2CubicInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.network.external.server.AutoAttackStop;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: $ $Date: $
 * @author  Luca Baldi
 */
public class AttackStanceTaskManager extends AbstractTask implements Runnable
{
	private static final long ATTACK_STANCE_PERIOD = 15000; // 15 seconds
	
	private final Map<L2Character, Long> characters = new ConcurrentHashMap<>();
	
	protected AttackStanceTaskManager()
	{
		// Run task each second.
		fixedSchedule(this, 1000, 1000);
		UtilPrint.result("AttackStanceTaskManager", "Started", "OK");
	}
	
	/**
	 * Adds {@link L2Character} to the AttackStanceTask.
	 * @param character : {@link L2Character} to be added and checked.
	 */
	public final void add(L2Character character)
	{
		if (character instanceof L2Playable)
		{
			for (L2CubicInstance cubic : character.getActingPlayer().getCubics().values())
			{
				if (cubic.getType() != CubicType.LIFE_CUBIC)
				{
					cubic.doAction();
				}
			}
		}
		characters.put(character, System.currentTimeMillis() + ATTACK_STANCE_PERIOD);
	}
	
	/**
	 * Removes {@link L2Character} from the AttackStanceTask.
	 * @param character : {@link L2Character} to be removed.
	 */
	public final void remove(L2Character character)
	{
		if (character instanceof L2Summon)
		{
			character = character.getActingPlayer();
		}
		
		characters.remove(character);
	}
	
	/**
	 * Tests if {@link L2Character} is in AttackStanceTask.
	 * @param  character : {@link L2Character} to be removed.
	 * @return           boolean : True when {@link L2Character} is in attack stance.
	 */
	public final boolean isInAttackStance(L2Character character)
	{
		if (character instanceof L2Summon)
		{
			character = character.getActingPlayer();
		}
		
		return characters.containsKey(character);
	}
	
	@Override
	public final void run()
	{
		// List is empty, skip.
		if (characters.isEmpty())
		{
			return;
		}
		
		// Get current time.
		final long currentTime = System.currentTimeMillis();
		
		// Loop all characters.
		for (Iterator<Entry<L2Character, Long>> iterator = characters.entrySet().iterator(); iterator.hasNext();)
		{
			// Get entry of current iteration.
			Entry<L2Character, Long> entry = iterator.next();
			
			// Time hasn't passed yet, skip.
			if (currentTime < entry.getValue())
			{
				continue;
			}
			
			// Get character.
			final L2Character character = entry.getKey();
			
			// Stop character attack stance animation.
			character.broadcastPacket(new AutoAttackStop(character));
			
			// Stop pet attack stance animation.
			if ((character instanceof L2PcInstance) && (((L2PcInstance) character).getPet() != null))
			{
				((L2PcInstance) character).getPet().broadcastPacket(new AutoAttackStop(((L2PcInstance) character).getPet()));
			}
			
			iterator.remove();
		}
	}
	
	public final static AttackStanceTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AttackStanceTaskManager INSTANCE = new AttackStanceTaskManager();
	}
}
