package l2j.gameserver.task.continuous;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class ItemsOnGroundTaskManager extends AbstractTask implements Runnable
{
	private static final Logger LOG = Logger.getLogger(ItemsOnGroundTaskManager.class.getName());
	
	private static final String LOAD_ITEMS = "SELECT object_id, item_id, count, enchant_level, x, y, z, drop_time, equipable FROM Itemsonground";
	private static final String REMOVE_ITEMS = "DELETE FROM Itemsonground";
	private static final String SAVE_ITEMS = "INSERT INTO Itemsonground(object_id, item_id, count, enchant_level, x, y, z, drop_time, equipable) values(?,?,?,?,?,?,?,?,?)";
	
	private final Set<ItemInstance> items = ConcurrentHashMap.newKeySet();
	protected long sleep;
	
	/**
	 * Constructor
	 */
	public ItemsOnGroundTaskManager()
	{
		if (Config.SAVE_DROPPED_ITEM)
		{
			init();
		}
		
		if (Config.AUTODESTROY_ITEM_AFTER > 0)
		{
			sleep = Config.AUTODESTROY_ITEM_AFTER * 1000;
			
			fixedSchedule(this, 5000, 5000);
			UtilPrint.result("ItemsOnGroundTaskManager", "Started", "OK");
		}
		else
		{
			UtilPrint.result("ItemsOnGroundTaskManager", "Started", "NO");
		}
	}
	
	private void init()
	{
		// Add items to world
		load();
		// Clean table ItemsOnGround
		cleanUp();
	}
	
	/**
	 * @param item
	 * @param playerDroped
	 */
	public void add(ItemInstance item, boolean playerDroped)
	{
		if (Config.LIST_PROTECTED_ITEMS.contains(item.getId()))
		{
			return;
		}
		
		// Never destroy items getDropTime == 0
		// Example MercTicketManager
		if (item.getDropTime() == 0)
		{
			return;
		}
		
		if ((Config.AUTODESTROY_ITEM_AFTER > 0))
		{
			if (playerDroped)
			{
				if (item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM)
				{
					items.add(item);
				}
				else
				{
					items.add(item);
				}
			}
			else
			{
				items.add(item);
			}
		}
	}
	
	public void remove(ItemInstance item)
	{
		items.remove(item);
	}
	
	@Override
	public void run()
	{
		if (items.isEmpty())
		{
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		for (ItemInstance item : items)
		{
			if ((item == null) || (item.getLocation() != ItemLocationType.VOID))
			{
				items.remove(item);
			}
			else
			{
				if ((currentTime - item.getDropTime()) > sleep)
				{
					L2World.getInstance().removeVisibleObject(item, item.getWorldRegion());
					L2World.getInstance().removeObject(item);
					
					items.remove(item);
				}
			}
		}
	}
	
	/**
	 * Save all item in ItemOnGroundTable
	 */
	public void save()
	{
		if (!Config.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		for (ItemInstance item : items)
		{
			if (item == null)
			{
				continue;
			}
			
			try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(SAVE_ITEMS))
			{
				ps.setInt(1, item.getObjectId());
				ps.setInt(2, item.getId());
				ps.setInt(3, item.getCount());
				ps.setInt(4, item.getEnchantLevel());
				ps.setInt(5, item.getX());
				ps.setInt(6, item.getY());
				ps.setInt(7, item.getZ());
				if (item.isProtected())
				{
					ps.setLong(8, -1); // item will be protected
				}
				else
				{
					ps.setLong(8, item.getDropTime()); // item will be added to ItemsAutoDestroy
				}
				if (item.isEquipable())
				{
					ps.setLong(9, 1); // set equipable
				}
				else
				{
					ps.setLong(9, 0);
				}
				ps.execute();
			}
			catch (Exception e)
			{
				LOG.warning(ItemsOnGroundTaskManager.class.getSimpleName() + ": while inserting into table ItemsOnGround " + e);
				e.printStackTrace();
			}
		}
		
		System.err.println(ItemsOnGroundTaskManager.class.getSimpleName() + ": All items on ground saved!");
	}
	
	/**
	 * Load and insert all items from ItemOnGround table
	 */
	private void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_ITEMS);
			ResultSet result = ps.executeQuery())
		{
			int count = 0;
			while (result.next())
			{
				ItemInstance item = new ItemInstance(result.getInt(1), result.getInt(2));
				L2World.getInstance().addObject(item);
				if (item.isStackable() && (result.getInt(3) > 1))
				{
					item.setCount(result.getInt(3));
				}
				if (result.getInt(4) > 0)
				{
					item.setEnchantLevel(result.getInt(4));
				}
				item.setWorldPosition(result.getInt(5), result.getInt(6), result.getInt(7));
				item.setWorldRegion(L2World.getInstance().getRegion(item.getWorldPosition()));
				item.getWorldRegion().addVisibleObject(item);
				item.setDropTime(result.getLong(8));
				if (result.getLong(8) == -1)
				{
					item.setProtected(true);
				}
				else
				{
					item.setProtected(false);
				}
				item.setIsVisible(true);
				
				L2World.getInstance().addVisibleObject(item, item.getWorldRegion());
				
				count++;
				// add to ItemsAutoDestroy only items not protected
				if (!Config.LIST_PROTECTED_ITEMS.contains(item.getId()))
				{
					if (!item.isProtected())
					{
						if (Config.AUTODESTROY_ITEM_AFTER > 0)
						{
							items.add(item);
						}
					}
				}
			}
			
			if (count > 0)
			{
				LOG.warning(ItemsOnGroundTaskManager.class.getSimpleName() + ": Restored " + count + " items.");
			}
			else
			{
				LOG.warning(ItemsOnGroundTaskManager.class.getSimpleName() + ": Initializing....");
			}
		}
		catch (Exception e)
		{
			LOG.warning(ItemsOnGroundTaskManager.class.getSimpleName() + ": error while loading ItemsOnGround " + e);
		}
	}
	
	/**
	 * Remove all items from ItemOnGround table
	 */
	private void cleanUp()
	{
		try (Connection conn = DatabaseManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(REMOVE_ITEMS))
		{
			ps.execute();
		}
		catch (Exception e1)
		{
			LOG.severe(ItemsOnGroundTaskManager.class + ": error while cleaning table ItemsOnGround " + e1);
			e1.printStackTrace();
		}
	}
	
	public static final ItemsOnGroundTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsOnGroundTaskManager INSTANCE = new ItemsOnGroundTaskManager();
	}
}
