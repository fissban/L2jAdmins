package l2j.gameserver.instancemanager.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.util.Util;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class Community
{
	private static final Logger LOG = Logger.getLogger(Community.class.getName());
	
	// TopicBBSManager
	private final List<CommunityTopicInstance> tableTopic = new ArrayList<>();
	private final Map<CommunityForumInstance, Integer> maxId = new ConcurrentHashMap<>();
	// ForumsBBSManager
	private static final String SELECT_FORUM_ID = "SELECT forum_id FROM forums";
	private final List<CommunityForumInstance> tableForum = new CopyOnWriteArrayList<>();
	private int forumLastId = 4;
	// PostBBSManager
	private final Map<CommunityTopicInstance, CommunityPostInstance> postByTopic = new HashMap<>();
	// MailBBSManager
	private static final String SELECT_CHAR_MAILS = "SELECT * FROM character_mail WHERE charId = ? ORDER BY letterId ASC";
	private static final String SELECT_LAST_ID = "SELECT letterId FROM character_mail ORDER BY letterId DESC LIMIT 1";
	
	private final Map<Integer, List<CommunityMailInstance>> mails = new HashMap<>();
	
	private int mailLastId = 0;
	
	protected Community()
	{
		// ForumsBBSManager
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_FORUM_ID);
			ResultSet result = ps.executeQuery())
		{
			int forumId = 1;
			
			while (result.next())
			{
				forumId = result.getInt("forum_id");
				// only add forum root
				if (forumId <= 4)
				{
					addForum(new CommunityForumInstance(result.getInt("forum_id"), null));
				}
			}
			forumLastId = forumId;
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Data error on Forum (root)");
			e.printStackTrace();
		}
		
		// MailBBSManager
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_LAST_ID);
			ResultSet result = ps.executeQuery())
		{
			while (result.next())
			{
				if (result.getInt(1) > mailLastId)
				{
					mailLastId = result.getInt("letterId");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": data error on MailBBS (initId)");
			e.printStackTrace();
		}
		
		UtilPrint.result("Community", "Loaded forums", forumLastId);
		UtilPrint.result("Community", "Loaded mails", mailLastId);
	}
	
	public void addTopic(CommunityTopicInstance tt)
	{
		tableTopic.add(tt);
	}
	
	public void delTopic(CommunityTopicInstance topic)
	{
		tableTopic.remove(topic);
	}
	
	public void setMaxId(int id, CommunityForumInstance f)
	{
		maxId.put(f, id);
	}
	
	public int getMaxId(CommunityForumInstance f)
	{
		Integer i = maxId.get(f);
		if (i == null)
		{
			return 0;
		}
		
		return i;
	}
	
	public CommunityTopicInstance getTopicByID(int idf)
	{
		for (CommunityTopicInstance t : tableTopic)
		{
			if (t.getID() == idf)
			{
				return t;
			}
		}
		return null;
	}
	
	// METODOS propios de MailBBSManager ----------------------------------------------------------------------------------
	
	public int checkUnreadMail(L2PcInstance activeChar)
	{
		int count = 0;
		for (CommunityMailInstance letter : getPlayerMails(activeChar.getObjectId()))
		{
			if (letter.unread)
			{
				count++;
			}
		}
		return count;
	}
	
	public synchronized int getNewMailId()
	{
		return ++mailLastId;
	}
	
	public List<CommunityMailInstance> getPlayerMails(int objId)
	{
		List<CommunityMailInstance> letters = mails.get(objId);
		if (letters == null)
		{
			letters = new ArrayList<>();
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT_CHAR_MAILS))
			{
				ps.setInt(1, objId);
				try (ResultSet result = ps.executeQuery())
				{
					while (result.next())
					{
						CommunityMailInstance letter = new CommunityMailInstance();
						letter.charId = result.getInt("charId");
						letter.letterId = result.getInt("letterId");
						letter.senderId = result.getInt("senderId");
						letter.location = result.getString("location").toLowerCase();
						letter.recipientNames = result.getString("recipientNames");
						letter.subject = result.getString("subject");
						letter.message = result.getString("message");
						letter.sentDate = result.getTimestamp("sentDate");
						letter.sentDateString = Util.formatDate(letter.sentDate, "yyyy-MM-dd HH:mm");
						letter.unread = result.getInt("unread") != 0;
						letters.add(0, letter);
					}
				}
			}
			catch (Exception e)
			{
				LOG.warning("couldnt load mail for ID:" + objId + " " + e.getMessage());
				return Collections.emptyList();
			}
			mails.put(objId, letters);
		}
		return letters;
	}
	
	// METODOS propios de PostBBSManager ---------------------------------------------------------------------------------
	
	public CommunityPostInstance getPostByTopic(CommunityTopicInstance t)
	{
		CommunityPostInstance post = postByTopic.get(t);
		if (post == null)
		{
			post = load(t);
			postByTopic.put(t, post);
		}
		return post;
	}
	
	public void delPostByTopic(CommunityTopicInstance t)
	{
		postByTopic.remove(t);
	}
	
	public void addPostByTopic(CommunityPostInstance p, CommunityTopicInstance t)
	{
		if (postByTopic.get(t) == null)
		{
			postByTopic.put(t, p);
		}
	}
	
	private static CommunityPostInstance load(CommunityTopicInstance t)
	{
		return new CommunityPostInstance(t);
	}
	
	// METODOS propios de ForumsBBSManager -------------------------------------------------------------------------------
	
	public void initRoot()
	{
		for (CommunityForumInstance f : tableForum)
		{
			f.vload();
		}
		
		LOG.info("Loaded " + tableForum.size() + " forums. Last forum id used: " + forumLastId);
	}
	
	public void addForum(CommunityForumInstance ff)
	{
		if (ff != null)
		{
			tableForum.add(ff);
		}
	}
	
	public CommunityForumInstance getForumByName(String name)
	{
		for (CommunityForumInstance f : tableForum)
		{
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}
	
	public CommunityForumInstance createNewForum(String name, CommunityForumInstance parent, int type, int perm, int oid)
	{
		int forumId = ++forumLastId;
		CommunityForumInstance forum = new CommunityForumInstance(name, forumId, parent, type, perm, oid);
		addForum(forum);
		forum.insertIntoDb();
		
		return forum;
	}
	
	public CommunityForumInstance getForumByID(int id)
	{
		for (CommunityForumInstance f : tableForum)
		{
			if (f.getId() == id)
			{
				return f;
			}
		}
		return null;
	}
	
	public static Community getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Community INSTANCE = new Community();
	}
}
