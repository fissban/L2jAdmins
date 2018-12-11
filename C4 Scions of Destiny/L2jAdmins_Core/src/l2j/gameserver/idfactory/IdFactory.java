package l2j.gameserver.idfactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.7 $ $Date: 2005/04/11 10:06:12 $
 */
public abstract class IdFactory
{
	private static Logger LOG = Logger.getLogger(IdFactory.class.getName());
	
	protected static final String[] ID_UPDATES =
	{
		"UPDATE character_items       SET owner_id = ?        WHERE owner_id = ?",
		"UPDATE character_items       SET object_id = ?       WHERE object_id = ?",
		"UPDATE character_quests      SET char_id = ?         WHERE char_id = ?",
		"UPDATE character_friends     SET char_id = ?         WHERE char_id = ?",
		"UPDATE character_friends     SET friend_id = ?       WHERE friend_id = ?",
		"UPDATE character_hennas      SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE character_recipebook  SET char_id = ?         WHERE char_id = ?",
		"UPDATE character_shortcuts   SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE character_shortcuts   SET shortcut_id = ?     WHERE shortcut_id = ? AND type = 1", // items
		"UPDATE character_macroses    SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE character_skills      SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE character_skills_save SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE character_subclasses  SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE characters            SET obj_Id = ?          WHERE obj_Id = ?",
		"UPDATE characters            SET clanid = ?          WHERE clanid = ?",
		"UPDATE clan_data             SET clan_id = ?         WHERE clan_id = ?",
		"UPDATE siege_clans           SET clan_id = ?         WHERE clan_id = ?",
		"UPDATE clan_data             SET ally_id = ?         WHERE ally_id = ?",
		"UPDATE clan_data             SET leader_id = ?       WHERE leader_id = ?",
		"UPDATE pets                  SET item_obj_id = ?     WHERE item_obj_id = ?",
		"UPDATE character_hennas      SET char_obj_id = ?     WHERE char_obj_id = ?",
		"UPDATE itemsonground         SET object_id = ?       WHERE object_id = ?",
		"UPDATE auction_bid           SET bidderId = ?        WHERE bidderId = ?",
		"UPDATE auction_watch         SET charObjId = ?       WHERE charObjId = ?",
		"UPDATE character_offline_trade SET char_id = ?       WHERE char_id = ?",
		"UPDATE character_offline_trade_items SET char_id = ? WHERE char_id = ?",
		"UPDATE clanhall              SET ownerId = ?         WHERE ownerId = ?"
	};
	
	protected static final String[] ID_CHECKS =
	{
		"SELECT owner_id    FROM character_items       WHERE object_id >= ?   AND object_id < ?",
		"SELECT object_id   FROM character_items       WHERE object_id >= ?   AND object_id < ?",
		"SELECT char_id     FROM character_quests      WHERE char_id >= ?     AND char_id < ?",
		"SELECT char_id     FROM character_friends     WHERE char_id >= ?     AND char_id < ?",
		"SELECT char_id     FROM character_friends     WHERE friend_id >= ?   AND friend_id < ?",
		"SELECT char_obj_id FROM character_hennas      WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT char_id     FROM character_recipebook  WHERE char_id >= ?     AND char_id < ?",
		"SELECT char_obj_id FROM character_shortcuts   WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT char_obj_id FROM character_macroses    WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT char_obj_id FROM character_skills      WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT char_obj_id FROM character_skills_save WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT char_obj_id FROM character_subclasses  WHERE char_obj_id >= ? AND char_obj_id < ?",
		"SELECT obj_Id      FROM characters            WHERE obj_Id >= ?      AND obj_Id < ?",
		"SELECT clanid      FROM characters            WHERE clanid >= ?      AND clanid < ?",
		"SELECT clan_id     FROM clan_data             WHERE clan_id >= ?     AND clan_id < ?",
		"SELECT clan_id     FROM siege_clans           WHERE clan_id >= ?     AND clan_id < ?",
		"SELECT ally_id     FROM clan_data             WHERE ally_id >= ?     AND ally_id < ?",
		"SELECT leader_id   FROM clan_data             WHERE leader_id >= ?   AND leader_id < ?",
		"SELECT item_obj_id FROM pets                  WHERE item_obj_id >= ? AND item_obj_id < ?",
		"SELECT object_id   FROM itemsonground         WHERE object_id >= ?   AND object_id < ?"
	};
	
	protected boolean initialized;
	
	public static final int FIRST_OID = 0x10000000;
	public static final int LAST_OID = 0x7FFFFFFF;
	public static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;
	
	protected static IdFactory INSTANCE = null;
	
	protected IdFactory()
	{
		setAllCharacterOffline();
		cleanUpDB();
	}
	
	static
	{
		switch (Config.IDFACTORY_TYPE)
		{
			case COMPACTION:
				INSTANCE = new CompactionIDFactory();
				break;
			case BITSET:
				INSTANCE = new BitSetIDFactory();
				break;
			case STACK:
				INSTANCE = new StackIDFactory();
				break;
		}
	}
	
	/**
	 * Sets all character offline
	 */
	private void setAllCharacterOffline()
	{
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var statement = con.createStatement())
		{
			statement.executeUpdate("UPDATE characters SET online=0");
			LOG.info("UPDATE characters online status.");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Cleans up Database
	 */
	private void cleanUpDB()
	{
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var statement = con.createStatement())
		{
			var cleanCount = 0;
			// Character related
			cleanCount += statement.executeUpdate("DELETE FROM character_friends WHERE character_friends.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_hennas WHERE character_hennas.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_macroses WHERE character_macroses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_quests WHERE character_quests.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_recipebook WHERE character_recipebook.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_shortcuts WHERE character_shortcuts.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_skills WHERE character_skills.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_skills_save WHERE character_skills_save.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_subclasses WHERE character_subclasses.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_recommends WHERE character_recommends.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_recommends WHERE character_recommends.target_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM heroes WHERE heroes.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM olympiad_nobles WHERE olympiad_nobles.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM pets WHERE pets.item_obj_id NOT IN (SELECT object_id FROM character_items);");
			cleanCount += statement.executeUpdate("DELETE FROM seven_signs WHERE seven_signs.char_obj_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_offline_trade WHERE character_offline_trade.char_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM character_offline_trade_items WHERE character_offline_trade_items.char_id NOT IN (SELECT obj_Id FROM characters);");
			// Clan related
			cleanCount += statement.executeUpdate("DELETE FROM clan_data WHERE clan_data.leader_id NOT IN (SELECT obj_Id FROM characters);");
			cleanCount += statement.executeUpdate("DELETE FROM auction_bid WHERE auction_bid.bidderId NOT IN (SELECT clan_id FROM clan_data);");
			cleanCount += statement.executeUpdate("DELETE FROM clanhall_functions WHERE clanhall_functions.hall_id NOT IN (SELECT id FROM clanhall WHERE ownerId <> 0);");
			cleanCount += statement.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan1 NOT IN (SELECT clan_id FROM clan_data);");
			cleanCount += statement.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan2 NOT IN (SELECT clan_id FROM clan_data);");
			cleanCount += statement.executeUpdate("DELETE FROM siege_clans WHERE siege_clans.clan_id NOT IN (SELECT clan_id FROM clan_data);");
			statement.executeUpdate("UPDATE castle SET taxpercent=0 WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);");
			// Character & clan related
			cleanCount += statement.executeUpdate("DELETE FROM character_items WHERE character_items.owner_id NOT IN (SELECT obj_Id FROM characters) AND character_items.owner_id NOT IN (SELECT clan_id FROM clan_data);");
			statement.executeUpdate("UPDATE characters SET clanid=0 WHERE characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
			// Forum related
			cleanCount += statement.executeUpdate("DELETE FROM forums WHERE forums.forum_owner_id NOT IN (SELECT clan_id FROM clan_data) AND forums.forum_parent=2;");
			cleanCount += statement.executeUpdate("DELETE FROM topic WHERE topic.topic_forum_id NOT IN (SELECT forum_id FROM forums);");
			cleanCount += statement.executeUpdate("DELETE FROM posts WHERE posts.post_forum_id NOT IN (SELECT forum_id FROM forums);");
			
			UtilPrint.result("IdFactory", "Cleaned elements from database", cleanCount);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param  con
	 * @return
	 */
	protected int[] extractUsedObjectIDTable()
	{
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var statement = con.createStatement())
		{
			statement.executeUpdate("DROP TABLE IF EXISTS temporaryObjectTable");
			statement.executeUpdate("DELETE from itemsonground where object_id in (SELECT object_id FROM character_items)");
			statement.executeUpdate("CREATE table temporaryObjectTable (object_id int NOT NULL PRIMARY KEY)");
			statement.executeUpdate("INSERT into temporaryObjectTable (object_id) SELECT obj_id FROM characters");
			statement.executeUpdate("INSERT into temporaryObjectTable (object_id) SELECT object_id FROM character_items");
			statement.executeUpdate("INSERT into temporaryObjectTable (object_id) SELECT clan_id FROM clan_data");
			statement.executeUpdate("INSERT into temporaryObjectTable (object_id) SELECT object_id FROM itemsonground");
			
			try (var result = statement.executeQuery("SELECT count(object_id) from temporaryObjectTable"))
			{
				result.next();
				var size = result.getInt(1);
				var tmpObjIds = new int[size];
				
				try (ResultSet result1 = statement.executeQuery("SELECT object_id FROM temporaryObjectTable ORDER BY object_id"))
				{
					var idx = 0;
					while (result1.next())
					{
						tmpObjIds[idx++] = result1.getInt(1);
					}
				}
				
				return tmpObjIds;
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public static IdFactory getInstance()
	{
		return INSTANCE;
	}
	
	public abstract int getNextId();
	
	/**
	 * return a used Object ID back to the pool
	 * @param id
	 */
	public abstract void releaseId(int id);
	
	public abstract int size();
}
