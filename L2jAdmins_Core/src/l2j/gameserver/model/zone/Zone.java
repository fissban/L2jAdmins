package l2j.gameserver.model.zone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.AServerPacket;
import main.EngineModsManager;

/**
 * Abstract base class for any zone type Handles basic operations
 * @author durgus
 */
public abstract class Zone
{
	private int id;
	protected ZoneForm zone;
	public Map<Integer, L2Character> characterList = new ConcurrentHashMap<>();
	
	private InstanceType targets = InstanceType.L2Character; // default all chars
	
	protected Zone(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	/**
	 * Setup new parameters for this zone
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, String value)
	{
		//
	}
	
	/**
	 * Set the zone for this L2ZoneType Instance
	 * @param zone
	 */
	public void setForm(ZoneForm zone)
	{
		if (this.zone != null)
		{
			throw new IllegalStateException("Zone already set");
		}
		this.zone = zone;
	}
	
	/**
	 * @return this zones zone form
	 */
	public ZoneForm getZone()
	{
		return zone;
	}
	
	public boolean isInsideZone(int x, int y)
	{
		return zone.isInsideZone(x, y, zone.getHighZ());
	}
	
	/**
	 * Checks if the given coordinates are within the zone
	 * @param  x
	 * @param  y
	 * @param  z
	 * @return
	 */
	public boolean isInsideZone(int x, int y, int z)
	{
		return zone.isInsideZone(x, y, z);
	}
	
	/**
	 * Checks if the given object is inside the zone.
	 * @param  object
	 * @return
	 */
	public boolean isInsideZone(L2Object object)
	{
		return isInsideZone(object.getX(), object.getY(), object.getZ());
	}
	
	public double getDistanceToZone(int x, int y)
	{
		return getZone().getDistanceToZone(x, y);
	}
	
	public double getDistanceToZone(L2Object object)
	{
		return getZone().getDistanceToZone(object.getX(), object.getY());
	}
	
	public void revalidateInZone(L2Character character)
	{
		// If the object is inside the zone...
		if (isInsideZone(character.getX(), character.getY(), character.getZ()))
		{
			// Was the character not yet inside this zone?
			if (!characterList.containsKey(character.getObjectId()))
			{
				// TODO desarrollar!
				// List<Quest> quests = getQuestByEvent(QuestEventType.ON_ENTER_ZONE);
				// if (quests != null)
				// {
				// for (Quest quest : quests)
				// quest.notifyEnterZone(character, this);
				// }
				
				characterList.put(character.getObjectId(), character);
				onEnter(character);
				
				EngineModsManager.onEnterZone(character, this);
			}
		}
		else
		{
			removeCharacter(character);
		}
	}
	
	/**
	 * Force fully removes a character from the zone Should use during teleport / logoff
	 * @param character
	 */
	public void removeCharacter(L2Character character)
	{
		if (characterList.containsKey(character.getObjectId()))
		{
			// List<Quest> quests = getQuestByEvent(QuestEventType.ON_EXIT_ZONE);
			// if (quests != null)
			// {
			// for (Quest quest : quests)
			// quest.notifyExitZone(character, this);
			// }
			characterList.remove(character.getObjectId());
			onExit(character);
			
			EngineModsManager.onExitZone(character, this);
		}
	}
	
	/**
	 * @param       <A>
	 * @param  type
	 * @return      a list of given instances within this zone.
	 */
	@SuppressWarnings("unchecked")
	public final <A> List<A> getKnownTypeInside(Class<A> type)
	{
		List<A> result = new ArrayList<>();
		
		for (L2Object obj : characterList.values())
		{
			if (type.isAssignableFrom(obj.getClass()))
			{
				result.add((A) obj);
			}
		}
		return result;
	}
	
	/**
	 * Broadcasts packet to all players inside the zone
	 * @param packet The packet to use.
	 */
	public void broadcastPacket(AServerPacket packet)
	{
		if (characterList.isEmpty())
		{
			return;
		}
		
		for (L2Character character : characterList.values())
		{
			if ((character != null) && (character instanceof L2PcInstance))
			{
				character.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Will scan the zones char list for the character
	 * @param  character
	 * @return
	 */
	public boolean isCharacterInZone(L2Character character)
	{
		return characterList.containsKey(character.getObjectId());
	}
	
	/**
	 * Get all player inside in zone
	 * @return
	 */
	public Collection<L2Character> getCharacterList()
	{
		return characterList.values();
	}
	
	/**
	 * Set objects affected in EffectZone & DamageZone
	 * @param type
	 */
	public void setTargetType(InstanceType type)
	{
		targets = type;
	}
	
	/**
	 * Get all objects affected in EffectZone & DamageZone
	 * @return
	 */
	public InstanceType getTargetType()
	{
		return targets;
	}
	
	protected abstract void onEnter(L2Character character);
	
	protected abstract void onExit(L2Character character);
}
