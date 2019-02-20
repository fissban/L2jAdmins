
package main.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import l2j.DatabaseManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.util.UtilPrint;
import main.EngineModsManager;
import main.holders.objects.CharacterHolder;
import main.holders.objects.ItemHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.ObjectHolder;
import main.holders.objects.PlayerHolder;
import main.holders.objects.SummonHolder;

/**
 * Class responsible for carrying information about the objects that exist inside the game
 * @author fissban
 */
public class ObjectData
{
	private static final Logger LOG = Logger.getLogger(ObjectData.class.getName());
	// SQL
	private static final String SELECT_CHARACTERS = "SELECT obj_Id,char_name,account_name FROM characters";
	/** all objects */
	private static volatile Map<Integer, ObjectHolder> objects = new ConcurrentHashMap<>();
	
	public ObjectData()
	{
		//
	}
	
	// XXX GET ------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public static <A> List<A> getAll(Class<A> type)
	{
		return (List<A>) objects.values().stream().filter(c -> type.isAssignableFrom(c.getClass())).collect(Collectors.toList());
	}
	
	public static <A> A get(Class<A> type, L2Object obj)
	{
		return get(type, obj.getObjectId());
	}
	
	@SuppressWarnings("unchecked")
	public static <A> A get(Class<A> type, int objId)
	{
		if (objects.containsKey(objId))
		{
			if (objects.get(objId) == null)
			{
				objects.remove(objId);
			}
			else if (type.isAssignableFrom(objects.get(objId).getClass()))
			{
				return (A) objects.get(objId);
			}
		}
		return null;
	}
	
	// XXX ADD & REMOVE -----------------------------------------------------------------------------------------------
	
	/**
	 * Add new player
	 * @param objectId
	 * @param name
	 * @param accountName
	 */
	public static void addPlayer(int objectId, String name, String accountName)
	{
		objects.put(objectId, new PlayerHolder(objectId, name, accountName));
	}
	
	public static void removePlayer(L2PcInstance p)
	{
		((PlayerHolder) objects.get(p.getObjectId())).setInstance(null);
	}
	
	/**
	 * Add new item
	 * @param item
	 */
	public static void addItem(ItemInstance item)
	{
		// dummyItems has objectId = 0
		if (item.getObjectId() > 0)
		{
			objects.put(item.getObjectId(), new ItemHolder(item));
			EngineModsManager.onCreatedItem(item);
		}
	}
	
	/**
	 * Add all objects.
	 * <li>For add Items {@link #addItem(ItemInstance)}.</li>
	 * <li>For add Player {@link #addPlayer(int, String, String)}.</li>
	 * @param obj
	 */
	public static void addObject(L2Object obj)
	{
		if (objects.containsKey(obj.getObjectId()) && (obj instanceof L2PcInstance))
		{
			((PlayerHolder) objects.get(obj.getObjectId())).setInstance((L2PcInstance) obj);
		}
		else if (!objects.containsKey(obj.getObjectId()))
		{
			if (obj instanceof ItemInstance)
			{
				//
			}
			else if (obj instanceof L2PcInstance)
			{
				//
			}
			else if (obj instanceof L2Summon)
			{
				objects.put(obj.getObjectId(), new SummonHolder((L2Summon) obj));
			}
			else if (obj instanceof L2Npc)
			{
				objects.put(obj.getObjectId(), new NpcHolder((L2Npc) obj));
			}
			else if (obj instanceof L2Character)
			{
				objects.put(obj.getObjectId(), new CharacterHolder((L2Character) obj));
			}
			else
			{
				objects.put(obj.getObjectId(), new ObjectHolder(obj));
			}
		}
	}
	
	/**
	 * Remove any object.
	 * @param obj
	 */
	public static void removeObject(L2Object obj)
	{
		if (obj instanceof L2PcInstance)
		{
			// removePlayer((L2PcInstance) obj);
			return;
		}
		
		objects.remove(obj.getObjectId());
	}
	
	// XXX PLAYERS -----------------------------------------------------------------------------------------------
	
	/**
	 * All the created characters are read from the DB.<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U>: This method should only be used when starting the server</B></FONT>
	 */
	public static void loadPlayers()
	{
		try (var con = DatabaseManager.getConnection();
			var statement = con.prepareStatement(SELECT_CHARACTERS);
			var rset = statement.executeQuery())
		{
			// Go though the recordset of this SQL query
			while (rset.next())
			{
				objects.put(rset.getInt("obj_Id"), new PlayerHolder(rset.getInt("obj_Id"), rset.getString("char_name"), rset.getString("account_name")));
			}
		}
		catch (Exception e)
		{
			LOG.warning("Could not restore character values: " + e.getMessage());
			e.printStackTrace();
		}
		
		UtilPrint.result("ObjectData", "Loaded players info", objects.size());
	}
}
