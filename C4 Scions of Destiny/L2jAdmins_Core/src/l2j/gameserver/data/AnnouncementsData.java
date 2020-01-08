package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.AnnouncementHolder;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class AnnouncementsData
{
	public enum AnnouncementType
	{
		SYSTEM,
		ALL,
		SHOUT, // !
		TELL,
		PARTY, // #
		CLAN, // @
		GM,
		PETITION_PLAYER, // used for petition
		PETITION_GM, // * used for petition
		TRADE, // +
		ALLIANCE, // $
		ANNOUNCEMENT,
		PARTY_ROOM,
		CHANNEL_LEADER, // (yellow)
		CHANNEL_ALL, // (blue)
		HERO_VOICE,
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnouncementsData.class);
	
	// SQL
	private static final String LOAD_ANNOUNCEMENTS = "SELECT text, sayType, repeatable, reuse FROM announcements";
	private static final String SAVE_ANNOUNCEMENTS = "INSERT INTO announcements (text, sayType, repeatable, reuse) VALUES (?,?,?,?)";
	private static final String DELETE_ANNOUNCEMENTS = "TRUNCATE TABLE announcements";
	
	private static List<AnnouncementHolder> announcements = new ArrayList<>();
	private static Map<String, Future<?>> announcementsTask = new HashMap<>();
	
	/**
	 * Load all announcements from DB
	 */
	public void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_ANNOUNCEMENTS);
			ResultSet result = ps.executeQuery())
		{
			while (result.next())
			{
				String announcement = result.getString("text");
				AnnouncementType type = AnnouncementType.valueOf(result.getString("sayType"));
				boolean repeatable = result.getInt("repeatable") > 0 ? true : false;
				int reuse = result.getInt("reuse");
				
				AnnouncementHolder announce = new AnnouncementHolder(announcement, type, repeatable, reuse);
				createTaskAnnouncements(announce);
				announcements.add(announce);
			}
			
			UtilPrint.result("AnnouncementsData", "Loaded announcements", announcements.size());
		}
		catch (Exception e)
		{
			LOG.warn(AnnouncementsData.class.getSimpleName() + ": Error reading announcements " + e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * <li>Delete all announcements from DB
	 * <li>Save all new announcements in DB
	 */
	public void save()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			// remove all announcements from DB
			try (PreparedStatement ps = con.prepareStatement(DELETE_ANNOUNCEMENTS))
			{
				ps.executeQuery();
			}
			// save all announcements in DB
			try (PreparedStatement ps = con.prepareStatement(SAVE_ANNOUNCEMENTS))
			{
				for (AnnouncementHolder ah : announcements)
				{
					ps.setString(1, ah.getAnnouncement());
					ps.setString(2, ah.getAnnouncementType().toString());
					ps.setInt(3, ah.isRepeatable() ? 1 : 0);
					ps.setInt(4, ah.getReuse());
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn(AnnouncementsData.class.getSimpleName() + ": Error saved announcements " + e);
			e.printStackTrace();
		}
	}
	
	public void sendAnnouncements(L2PcInstance player)
	{
		for (AnnouncementHolder ah : announcements)
		{
			if (ah.isRepeatable())
			{
				continue;
			}
			
			if (ah.getAnnouncementType() == AnnouncementType.SYSTEM)
			{
				player.sendMessage(ah.getAnnouncement());
			}
			else
			{
				player.sendPacket(new CreatureSay(SayType.valueOf(ah.getAnnouncementType().toString()), "", ah.getAnnouncement()));
			}
		}
	}
	
	public void addAnnouncement(String announcement, AnnouncementType announcementType, boolean repeatable, int reuse)
	{
		AnnouncementHolder announce = new AnnouncementHolder(announcement, announcementType, repeatable, reuse);
		// Create new task
		createTaskAnnouncements(announce);
		// saved announcement in list
		announcements.add(announce);
	}
	
	public void removeAnnouncement(int index)
	{
		AnnouncementHolder ah = announcements.remove(index);
		
		if (ah == null)
		{
			return;
		}
		
		// cancel and remove task announcement
		if (ah.isRepeatable())
		{
			Future<?> task = announcementsTask.remove(ah.getAnnouncement());
			task.cancel(false);
			task = null;
		}
	}
	
	public List<AnnouncementHolder> getAnnouncements()
	{
		return announcements;
	}
	
	private void createTaskAnnouncements(AnnouncementHolder ah)
	{
		if (ah.isRepeatable())
		{
			// Create new task
			announcementsTask.put(ah.getAnnouncement(), ThreadPoolManager.scheduleAtFixedRate(() ->
			{
				if (ah.getAnnouncementType() == AnnouncementType.SYSTEM)
				{
					Broadcast.toAllOnlinePlayers(SystemMessage.sendString(ah.getAnnouncement()));
					
				}
				else
				{
					Broadcast.toAllOnlinePlayers(new CreatureSay(SayType.valueOf(ah.getAnnouncementType().toString()), "", ah.getAnnouncement()));
				}
				
			}, ah.getReuse(), ah.getReuse() * 60000));
		}
	}
	
	// MISC ----------------------------------------------------------------------------------
	
	/**
	 * All online players is sent a message of type "Say2.ANNOUNCEMENT"
	 * @param text
	 */
	public void announceToAll(String text)
	{
		Broadcast.toAllOnlinePlayers(text);
	}
	
	/**
	 * All online players sent a message of type "SystemMessage"
	 * @param sm
	 */
	public void announceToAll(SystemMessage sm)
	{
		Broadcast.toAllOnlinePlayers(sm);
	}
	
	public static AnnouncementsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AnnouncementsData INSTANCE = new AnnouncementsData();
	}
}
