package l2j.gameserver.instancemanager.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l2j.DatabaseManager;

/**
 * @author Maktakien
 */
public class CommunityPostInstance
{
	// TODO una clase solo para lojar otra???
	private static final Logger LOG = Logger.getLogger(CommunityPostInstance.class.getName());
	
	public class CPost
	{
		// FIXME definir como privadas y sus metodos get y set
		public int postID;
		public String postOwner;
		public int postOwnerID;
		public long postDate;
		public int postTopicID;
		public int postForumID;
		public String postTxt;
	}
	
	private final List<CPost> post;
	
	/**
	 * @param postOwner
	 * @param postOwnerID
	 * @param date
	 * @param tid
	 * @param postForumID
	 * @param txt
	 */
	public CommunityPostInstance(String postOwner, int postOwnerID, long date, int tid, int postForumID, String txt)
	{
		post = new ArrayList<>();
		final CPost cp = new CPost();
		cp.postID = 0;
		cp.postOwner = postOwner;
		cp.postOwnerID = postOwnerID;
		cp.postDate = date;
		cp.postTopicID = tid;
		cp.postForumID = postForumID;
		cp.postTxt = txt;
		post.add(cp);
		insertInDB(cp);
	}
	
	public CommunityPostInstance(CommunityTopicInstance t)
	{
		post = new ArrayList<>();
		load(t);
	}
	
	public CPost getCPost(int id)
	{
		int i = 0;
		for (final CPost cp : post)
		{
			if (i == id)
			{
				return cp;
			}
			i++;
		}
		return null;
	}
	
	private void insertInDB(CPost cp)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) values (?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, cp.postID);
			ps.setString(2, cp.postOwner);
			ps.setInt(3, cp.postOwnerID);
			ps.setLong(4, cp.postDate);
			ps.setInt(5, cp.postTopicID);
			ps.setInt(6, cp.postForumID);
			ps.setString(7, cp.postTxt);
			ps.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("error while saving new Post to db " + e);
		}
	}
	
	public void deleteFromDB(CommunityTopicInstance t)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?"))
		{
			statement.setInt(1, t.getForumID());
			statement.setInt(2, t.getID());
			statement.execute();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param t
	 */
	private void load(CommunityTopicInstance t)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC"))
		{
			ps.setInt(1, t.getForumID());
			ps.setInt(2, t.getID());
			
			try (ResultSet result = ps.executeQuery())
			{
				while (result.next())
				{
					final CPost cp = new CPost();
					cp.postID = Integer.parseInt(result.getString("post_id"));
					cp.postOwner = result.getString("post_owner_name");
					cp.postOwnerID = Integer.parseInt(result.getString("post_ownerid"));
					cp.postDate = Long.parseLong(result.getString("post_date"));
					cp.postTopicID = Integer.parseInt(result.getString("post_topic_id"));
					cp.postForumID = Integer.parseInt(result.getString("post_forum_id"));
					cp.postTxt = result.getString("post_txt");
					post.add(cp);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("data error on Post " + t.getForumID() + "/" + t.getID() + " : " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * @param i
	 */
	public void updatetxt(int i)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?"))
		{
			final CPost cp = getCPost(i);
			ps.setString(1, cp.postTxt);
			ps.setInt(2, cp.postID);
			ps.setInt(3, cp.postTopicID);
			ps.setInt(4, cp.postForumID);
			ps.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("error while saving new Post to db " + e);
		}
	}
}
