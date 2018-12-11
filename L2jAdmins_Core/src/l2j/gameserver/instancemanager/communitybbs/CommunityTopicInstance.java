package l2j.gameserver.instancemanager.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;

public class CommunityTopicInstance
{
	private static final Logger LOG = Logger.getLogger(CommunityTopicInstance.class.getName());
	
	public enum ConstructorType
	{
		RESTORE,
		CREATE
	}
	
	public static final int MORMAL = 0;
	public static final int MEMO = 1;
	
	private final int id;
	private final int forumID;
	private final String topicName;
	private final long date;
	private final String ownerName;
	private final int ownerID;
	private final int type;
	private final int creply;
	
	/**
	 * @param ct
	 * @param id
	 * @param fid
	 * @param name
	 * @param date
	 * @param oname
	 * @param oid
	 * @param type
	 * @param Creply
	 */
	public CommunityTopicInstance(ConstructorType ct, int id, int fid, String name, long date, String oname, int oid, int type, int Creply)
	{
		this.id = id;
		forumID = fid;
		topicName = name;
		this.date = date;
		ownerName = oname;
		ownerID = oid;
		this.type = type;
		creply = Creply;
		
		if (ct == ConstructorType.CREATE)
		{
			insertInDB();
		}
	}
	
	/**
	 *
	 */
	private void insertInDB()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO topic (topic_id,topic_forum_id,topic_name,topic_date,topic_ownerName,topic_ownerid,topic_type,topic_reply) values (?,?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, id);
			ps.setInt(2, forumID);
			ps.setString(3, topicName);
			ps.setLong(4, date);
			ps.setString(5, ownerName);
			ps.setInt(6, ownerID);
			ps.setInt(7, type);
			ps.setInt(8, creply);
			ps.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("error while saving new Topic to db " + e);
		}
	}
	
	/**
	 * @return
	 */
	public int getID()
	{
		return id;
	}
	
	public int getForumID()
	{
		return forumID;
	}
	
	/**
	 * @return
	 */
	public String getName()
	{
		return topicName;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
	
	/**
	 * Borramos el topic de la DB y de la lista de su foro
	 * @param f
	 */
	public void deleteFromDB(CommunityForumInstance f)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM topic WHERE topic_id=? AND topic_forum_id=?"))
		{
			ps.setInt(1, getID());
			ps.setInt(2, f.getId());
			ps.execute();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public long getDate()
	{
		return date;
	}
}
