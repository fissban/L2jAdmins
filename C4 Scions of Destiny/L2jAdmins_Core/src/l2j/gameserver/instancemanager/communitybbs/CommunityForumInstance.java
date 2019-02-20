package l2j.gameserver.instancemanager.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.instancemanager.communitybbs.CommunityTopicInstance.ConstructorType;

public class CommunityForumInstance
{
	private static final Logger LOG = Logger.getLogger(CommunityForumInstance.class.getName());
	// Types
	public static final int ROOT = 0;
	public static final int NORMAL = 1;
	public static final int CLAN = 2;
	public static final int MEMO = 3;
	public static final int MAIL = 4;
	
	// Permissions
	public static final int INVISIBLE = 0;
	public static final int ALL = 1;
	public static final int CLANMEMBERONLY = 2;
	public static final int OWNERONLY = 3;
	
	private final List<CommunityForumInstance> children;
	private final Map<Integer, CommunityTopicInstance> topic;
	
	private final int forumId;
	private String forumName;
	
	private int forumType;
	private int forumPost;
	private int forumPerm;
	
	private final CommunityForumInstance fParent;
	
	private int ownerId;
	
	private boolean loaded = false;
	
	public CommunityForumInstance(int forumId, CommunityForumInstance fParent)
	{
		this.forumId = forumId;
		this.fParent = fParent;
		children = new ArrayList<>();
		topic = new HashMap<>();
	}
	
	public CommunityForumInstance(String name, int forumId, CommunityForumInstance parent, int type, int perm, int ownerID)
	{
		forumName = name;
		this.forumId = forumId;
		forumType = type;
		forumPost = 0;
		forumPerm = perm;
		fParent = parent;
		ownerId = ownerID;
		children = new ArrayList<>();
		topic = new HashMap<>();
		parent.children.add(this);
		loaded = true;
	}
	
	private void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM forums WHERE forum_id=?"))
		{
			ps.setInt(1, forumId);
			try (ResultSet result = ps.executeQuery())
			{
				if (result.next())
				{
					forumName = result.getString("forum_name");
					forumPost = result.getInt("forum_post");
					forumType = result.getInt("forum_type");
					forumPerm = result.getInt("forum_perm");
					ownerId = result.getInt("forum_owner_id");
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Data error on Forum " + forumId + " : " + e.getMessage(), e);
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM topic WHERE topic_forum_id=? ORDER BY topic_id DESC"))
		{
			ps.setInt(1, forumId);
			try (ResultSet result = ps.executeQuery())
			{
				while (result.next())
				{
					CommunityTopicInstance t = new CommunityTopicInstance(ConstructorType.RESTORE, result.getInt("topic_id"), result.getInt("topic_forum_id"), result.getString("topic_name"), result.getLong("topic_date"), result.getString("topic_ownername"), result.getInt("topic_ownerid"), result.getInt("topic_type"), result.getInt("topic_reply"));
					
					topic.put(t.getID(), t);
					
					if (t.getID() > Community.getInstance().getMaxId(this))
					{
						Community.getInstance().setMaxId(t.getID(), this);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Data error on Forum " + forumId + " : " + e.getMessage(), e);
		}
	}
	
	private void getChildren()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_parent=?"))
		{
			ps.setInt(1, forumId);
			try (ResultSet result = ps.executeQuery())
			{
				while (result.next())
				{
					CommunityForumInstance f = new CommunityForumInstance(result.getInt("forum_id"), this);
					children.add(f);
					
					Community.getInstance().addForum(f);
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Data error on Forum (children): " + e.getMessage(), e);
		}
	}
	
	public int getTopicSize()
	{
		vload();
		return topic.size();
	}
	
	public CommunityTopicInstance getTopic(int j)
	{
		vload();
		return topic.get(j);
	}
	
	public void addTopic(CommunityTopicInstance t)
	{
		vload();
		topic.put(t.getID(), t);
	}
	
	public int getId()
	{
		return forumId;
	}
	
	public String getName()
	{
		vload();
		return forumName;
	}
	
	public int getType()
	{
		vload();
		return forumType;
	}
	
	public CommunityForumInstance getChildByName(String name)
	{
		vload();
		for (CommunityForumInstance f : children)
		{
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}
	
	public void rmTopicById(int id)
	{
		topic.remove(id);
	}
	
	public void insertIntoDb()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO forums (forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id) VALUES (?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, forumId);
			ps.setString(2, forumName);
			ps.setInt(3, fParent.getId());
			ps.setInt(4, forumPost);
			ps.setInt(5, forumType);
			ps.setInt(6, forumPerm);
			ps.setInt(7, ownerId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Error while saving new Forum to db " + e.getMessage(), e);
		}
	}
	
	public void vload()
	{
		if (!loaded)
		{
			load();
			getChildren();
			loaded = true;
		}
	}
}
