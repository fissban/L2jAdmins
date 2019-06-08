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
	private final int id;
	protected ZoneForm zone;
	public Map<Integer, L2Character> characterList = new ConcurrentHashMap<>();
	private InstanceType _target = InstanceType.L2Character; // default all chars
	
	/** Parameters to affect specific characters */
	private boolean checkAffected;
	
	private int minLvl;
	private int maxLvl;
	private int[] race;
	private int[] classId;
	private char classType;
	
	protected Zone(int id)
	{
		this.id = id;
		checkAffected = false;
		
		minLvl = 0;
		maxLvl = 255;
		
		classType = 0;
		
		race = null;
		classId = null;
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
		checkAffected = true;
		
		// Minimum level
		if (name.equals("affectedLvlMin"))
		{
			minLvl = Integer.parseInt(value);
		}
		// Maximum level
		else if (name.equals("affectedLvlMax"))
		{
			maxLvl = Integer.parseInt(value);
		}
		// Affected Races
		else if (name.equals("affectedRace"))
		{
			// Create a new array holding the affected race
			if (race == null)
			{
				race = new int[1];
				race[0] = Integer.parseInt(value);
			}
			else
			{
				int[] temp = new int[race.length + 1];
				
				int i = 0;
				for (; i < race.length; i++)
				{
					temp[i] = race[i];
				}
				
				temp[i] = Integer.parseInt(value);
				race = temp;
			}
		}
		// Affected classes
		else if (name.equals("affectedClassId"))
		{
			// Create a new array holding the affected classIds
			if (classId == null)
			{
				classId = new int[1];
				classId[0] = Integer.parseInt(value);
			}
			else
			{
				int[] temp = new int[classId.length + 1];
				
				int i = 0;
				for (; i < classId.length; i++)
				{
					temp[i] = classId[i];
				}
				
				temp[i] = Integer.parseInt(value);
				classId = temp;
			}
		}
		// Affected class type
		else if (name.equals("affectedClassType"))
		{
			if (value.equals("Fighter"))
			{
				classType = 1;
			}
			else
			{
				classType = 2;
			}
		}
		else if (name.equals("targetClass"))
		{
			_target = Enum.valueOf(InstanceType.class, value);
		}
	}
	
	/**
	 * Checks if the given character is affected by this zone
	 * @param  character
	 * @return
	 */
	private boolean isAffected(L2Character character)
	{
		// Check lvl
		if ((character.getLevel() < minLvl) || (character.getLevel() > maxLvl))
		{
			return false;
		}
		
		// check obj class
		if (!character.isInstanceTypes(_target))
		{
			return false;
		}
		
		if (character instanceof L2PcInstance)
		{
			// Check class type
			if (classType != 0)
			{
				if (((L2PcInstance) character).isMageClass())
				{
					if (classType == 1)
					{
						return false;
					}
				}
				else if (classType == 2)
				{
					return false;
				}
			}
			
			// Check race
			if (race != null)
			{
				boolean ok = false;
				
				for (int element : race)
				{
					if (((L2PcInstance) character).getRace().ordinal() == element)
					{
						ok = true;
						break;
					}
				}
				
				if (!ok)
				{
					return false;
				}
			}
			
			// Check class
			if (classId != null)
			{
				boolean ok = false;
				for (int clas : classId)
				{
					if (((L2PcInstance) character).getClassId().ordinal() == clas)
					{
						ok = true;
						break;
					}
				}
				
				if (!ok)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Set the zone for this L2ZoneType Instance
	 * @param id
	 * @param zone
	 */
	public void setZone(int id, ZoneForm zone)
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
			// If the character can't be affected by this zone return
			if (checkAffected)
			{
				if (!isAffected(character))
				{
					return;
				}
			}
			
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
	 * @param  <A>
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
	
	public Collection<L2Character> getCharacterList()
	{
		return characterList.values();
	}
	
	public InstanceType getTargetType()
	{
		return _target;
	}
	
	public void setTargetType(InstanceType type)
	{
		_target = type;
		checkAffected = true;
	}
	
	protected abstract void onEnter(L2Character character);
	
	protected abstract void onExit(L2Character character);
}
