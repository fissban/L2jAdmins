package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class CharNameData
{
	private static final Logger LOG = Logger.getLogger(CharNameData.class.getName());
	
	// SQL
	private static final String SELECT_1 = "SELECT account_name FROM characters WHERE char_name=?";
	private static final String SELECT_2 = "SELECT COUNT(char_name) FROM characters WHERE account_name=?";
	private static final String SELECT_3 = "SELECT obj_Id,char_name FROM characters";
	//
	private final Map<Integer, String> chars = new HashMap<>();
	
	protected CharNameData()
	{
		loadAll();
		
		UtilPrint.result("CharNameData", "Loaded char names", chars.size());
	}
	
	/**
	 * Add player name in memory
	 * @param player
	 */
	public final void addName(L2PcInstance player)
	{
		if (player != null)
		{
			addName(player.getObjectId(), player.getName());
		}
	}
	
	/**
	 * Add player name in memory
	 * @param objectId
	 * @param name
	 */
	private final void addName(int objectId, String name)
	{
		if (name != null)
		{
			if (!name.equals(chars.get(objectId)))
			{
				chars.put(objectId, name);
			}
		}
	}
	
	/**
	 * Remove char name from memory
	 * @param objId
	 */
	public final void removeName(int objId)
	{
		chars.remove(objId);
	}
	
	/**
	 * You get the id of a char according to your name
	 * @param  name
	 * @return
	 */
	public final int getIdByName(String name)
	{
		if ((name == null) || name.isEmpty())
		{
			return -1;
		}
		
		Iterator<Entry<Integer, String>> it = chars.entrySet().iterator();
		
		Entry<Integer, String> pair;
		while (it.hasNext())
		{
			pair = it.next();
			if (pair.getValue().equalsIgnoreCase(name))
			{
				return pair.getKey();
			}
		}
		
		return -1;
	}
	
	/**
	 * Get the name of a char according to its Id
	 * @param  id
	 * @return
	 */
	public final String getNameById(int id)
	{
		if (id <= 0)
		{
			return null;
		}
		
		String name = chars.get(id);
		if (name != null)
		{
			return name;
		}
		
		return null;
	}
	
	/**
	 * Check if the name of a char is already in use by another.
	 * @param  name
	 * @return
	 */
	public synchronized boolean doesCharNameExist(String name)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_1))
		{
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery())
			{
				result = rs.next();
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing charname: " + e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * Get the number of characters in an account.
	 * @param  account
	 * @return
	 */
	public int accountCharNumber(String account)
	{
		int number = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_2))
		{
			ps.setString(1, account);
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					number = rset.getInt(1);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char number: " + e.getMessage(), e);
		}
		return number;
	}
	
	/**
	 * Load all characters names.
	 */
	private void loadAll()
	{
		String name;
		int id = -1;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(SELECT_3))
		{
			while (rs.next())
			{
				id = rs.getInt(1);
				name = rs.getString(2);
				
				chars.put(id, name);
			}
		}
		catch (SQLException e)
		{
			LOG.log(Level.WARNING, getClass().getSimpleName() + ": Could not load char name: " + e.getMessage(), e);
		}
	}
	
	public static CharNameData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CharNameData INSTANCE = new CharNameData();
	}
}
