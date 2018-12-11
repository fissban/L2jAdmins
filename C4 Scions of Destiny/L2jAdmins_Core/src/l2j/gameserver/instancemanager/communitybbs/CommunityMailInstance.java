package l2j.gameserver.instancemanager.communitybbs;

import java.sql.Timestamp;

/**
 * @author fissban
 */
public class CommunityMailInstance
{
	public int charId;
	public int letterId;
	public int senderId;
	public String location;
	public String recipientNames;
	public String subject;
	public String message;
	public Timestamp sentDate;
	public String sentDateString;
	public boolean unread;
}
