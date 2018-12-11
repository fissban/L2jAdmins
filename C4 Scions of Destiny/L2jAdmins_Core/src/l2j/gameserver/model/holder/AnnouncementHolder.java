package l2j.gameserver.model.holder;

import l2j.gameserver.data.AnnouncementsData.AnnouncementType;

/**
 * @author fissban
 */
public class AnnouncementHolder
{
	private String announcement;
	private AnnouncementType announcementType;
	private boolean repeatable;
	private int reuse;
	
	public AnnouncementHolder(String announcement, AnnouncementType announcementType, boolean repeatable, int reuse)
	{
		this.announcement = announcement;
		this.announcementType = announcementType;
		this.repeatable = repeatable;
		this.reuse = reuse;
	}
	
	public String getAnnouncement()
	{
		return announcement;
	}
	
	public AnnouncementType getAnnouncementType()
	{
		return announcementType;
	}
	
	public boolean isRepeatable()
	{
		return repeatable;
	}
	
	public int getReuse()
	{
		return reuse;
	}
}
