package l2j.gameserver.handler.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import l2j.DatabaseManager;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.instancemanager.communitybbs.Community;
import l2j.gameserver.instancemanager.communitybbs.CommunityMailInstance;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.ExMailArrived;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class CommunityMail extends AbstractCommunityHandler
{
	private static final String INSERT_NEW_MAIL = "INSERT INTO character_mail (charId, letterId, senderId, location, recipientNames, subject, message, sentDate, unread) VALUES (?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_MAIL = "DELETE FROM character_mail WHERE letterId = ?";
	private static final String MARK_MAIL_READ = "UPDATE character_mail SET unread = ? WHERE letterId = ?";
	private static final String SET_LETTER_LOC = "UPDATE character_mail SET location = ? WHERE letterId = ?";
	
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_maillist_0_1_0_",
			"_bbsmail" // usado en botones ya seteados desde el cliente
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// maillist_0_1_0_ // bbsmail
		
		if (!st.hasMoreTokens())
		{
			showMailList(activeChar, 1, "inbox");
		}
		else
		{
			String action = st.nextToken();
			
			switch (action)
			{
				case "inbox":
				case "sentbox":
				case "archive":
				case "temparchive":
					final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
					final String sType = (st.hasMoreTokens()) ? st.nextToken() : "";
					final String search = (st.hasMoreTokens()) ? st.nextToken() : "";
					
					showMailList(activeChar, page, action, sType, search);
					break;
				
				case "crea":
					showWriteView(activeChar);
					break;
				
				case "view":
					final int letterId = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : -1;
					
					CommunityMailInstance letter = getLetter(activeChar, letterId);
					if (letter == null)
					{
						showLastForum(activeChar);
					}
					else
					{
						showLetterView(activeChar, letter);
						if (letter.unread)
						{
							setLetterToRead(activeChar, letter.letterId);
						}
					}
					break;
				
				case "reply":
					final int letterId_reply = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : -1;
					
					CommunityMailInstance letter_reply = getLetter(activeChar, letterId_reply);
					if (letter_reply == null)
					{
						showLastForum(activeChar);
					}
					else
					{
						showWriteView(activeChar, getCharName(letter_reply.senderId), letter_reply);
					}
					break;
				
				case "del":
					final int letterId_del = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : -1;
					
					CommunityMailInstance letter_del = getLetter(activeChar, letterId_del);
					if (letter_del != null)
					{
						deleteLetter(activeChar, letter_del.letterId);
					}
					
					showLastForum(activeChar);
				case "store":
					final int letterId_store = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : -1;
					
					CommunityMailInstance letter_store = getLetter(activeChar, letterId_store);
					if (letter_store != null)
					{
						setLetterLocation(activeChar, letter_store.letterId, "archive");
					}
					
					showMailList(activeChar, 1, "archive");
					break;
				
			}
		}
		
		// separateAndSend("<html><body><br><br><center>the command: " + st.nextToken() + " is not implemented yet</center><br><br></body></html>", activeChar);
	}
	
	@Override
	public String getWriteList()
	{
		return "Mail";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		if (ar1.equals("Send"))
		{
			sendLetter(ar3, ar4, ar5, activeChar);
			showMailList(activeChar, 1, "sentbox");
		}
		else if (ar1.startsWith("Search"))
		{
			StringTokenizer st = new StringTokenizer(ar1, ";");
			st.nextToken();
			
			showMailList(activeChar, 1, st.nextToken(), ar4, ar5);
		}
	}
	
	// METODOS de parseCmd
	
	private CommunityMailInstance getLetter(L2PcInstance activeChar, int letterId)
	{
		for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(activeChar.getObjectId()))
		{
			if (letter.letterId == letterId)
			{
				return letter;
			}
		}
		return null;
	}
	
	private static String abbreviate(String s, int maxWidth)
	{
		return s.length() > maxWidth ? s.substring(0, maxWidth) : s;
	}
	
	private void showMailList(L2PcInstance activeChar, int page, String type)
	{
		showMailList(activeChar, page, type, "", "");
	}
	
	private void showMailList(L2PcInstance activeChar, int page, String type, String sType, String search)
	{
		List<CommunityMailInstance> letters;
		if (!sType.equals("") && !search.equals(""))
		{
			letters = new ArrayList<>();
			
			boolean byTitle = sType.equalsIgnoreCase("title");
			
			for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(activeChar.getObjectId()))
			{
				if (byTitle && letter.subject.toLowerCase().contains(search.toLowerCase()))
				{
					letters.add(letter);
				}
				else if (!byTitle)
				{
					String writer = getCharName(letter.senderId);
					if (writer.toLowerCase().contains(search.toLowerCase()))
					{
						letters.add(letter);
					}
				}
			}
		}
		else
		{
			letters = Community.getInstance().getPlayerMails(activeChar.getObjectId());
		}
		
		final int countMails = getCountLetters(activeChar.getObjectId(), type, sType, search);
		final int maxpage = getMaxPageId(countMails);
		
		if (page > maxpage)
		{
			page = maxpage;
		}
		if (page < 1)
		{
			page = 1;
		}
		
		activeChar.setMailPosition(page);
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 9 : (page * 10) - 1);
		minIndex = maxIndex - 9;
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "mail/mail.htm");
		content = content.replace("%inbox%", getCountLetters(activeChar.getObjectId(), "inbox", "", "") + "");
		content = content.replace("%sentbox%", getCountLetters(activeChar.getObjectId(), "sentbox", "", "") + "");
		content = content.replace("%archive%", getCountLetters(activeChar.getObjectId(), "archive", "", "") + "");
		content = content.replace("%temparchive%", getCountLetters(activeChar.getObjectId(), "temparchive", "", "") + "");
		
		String htmlType = "";
		if (type.equalsIgnoreCase("inbox"))
		{
			htmlType = "Inbox";
		}
		else if (type.equalsIgnoreCase("sentbox"))
		{
			htmlType = "Sent Box";
		}
		else if (type.equalsIgnoreCase("archive"))
		{
			htmlType = "Mail Archive";
		}
		else if (type.equalsIgnoreCase("temparchive"))
		{
			htmlType = "Temporary Mail Archive";
		}
		content = content.replace("%type%", htmlType);
		
		content = content.replace("%htype%", type);
		String mailList = "";
		for (CommunityMailInstance letter : letters)
		{
			if (letter.location.equals(type))
			{
				if (index < minIndex)
				{
					index++;
					continue;
				}
				
				if (index > maxIndex)
				{
					break;
				}
				
				String tempName = getCharName(letter.senderId);
				mailList += "<table width=610><tr>";
				mailList += "<td width=5></td>";
				mailList += "<td width=150>" + tempName + "</td>";
				mailList += "<td width=300><a action=\"bypass _maillist_0_1_0_;view;" + letter.letterId + "\">";
				if (letter.unread)
				{
					mailList += "<font color=\"LEVEL\">";
				}
				mailList += abbreviate(letter.subject, 51);
				if (letter.unread)
				{
					mailList += "</font>";
				}
				mailList += "</a>";
				mailList += "</td><td width=150>" + letter.sentDateString + "</td>";
				mailList += "<td width=5></td></tr></table>";
				mailList += "<img src=\"L2UI.Squaregray\" width=610 height=1>";
				index++;
			}
		}
		content = content.replace("%maillist%", mailList);
		String fullSearch = (!sType.equals("") && !search.equals("")) ? ";" + sType + ";" + search : "";
		String mailListLength = "";
		mailListLength += "<td><table><tr><td></td></tr><tr><td>";
		mailListLength += "<button action=\"bypass _maillist_0_1_0_;" + type + ";" + (page == 1 ? page : page - 1) + fullSearch + "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16>";
		mailListLength += "</td></tr></table></td>";
		int i = 0;
		if (maxpage > 21)
		{
			if (page <= 11)
			{
				for (i = 1; i <= (10 + page); i++)
				{
					if (i == page)
					{
						mailListLength += "<td> " + i + " </td>";
					}
					else
					{
						mailListLength += "<td><a action=\"bypass _maillist_0_1_0_;" + type + ";" + i + fullSearch + "\"> " + i + " </a></td>";
					}
				}
			}
			else if ((page > 11) && ((maxpage - page) > 10))
			{
				for (i = (page - 10); i <= (page - 1); i++)
				{
					if (i == page)
					{
						continue;
					}
					
					mailListLength += "<td><a action=\"bypass _maillist_0_1_0_;" + type + ";" + i + fullSearch + "\"> " + i + " </a></td>";
				}
				for (i = page; i <= (page + 10); i++)
				{
					if (i == page)
					{
						mailListLength += "<td> " + i + " </td>";
					}
					else
					{
						mailListLength += "<td><a action=\"bypass _maillist_0_1_0_;" + type + ";" + i + fullSearch + "\"> " + i + " </a></td>";
					}
				}
			}
			else if ((maxpage - page) <= 10)
			{
				for (i = (page - 10); i <= maxpage; i++)
				{
					if (i == page)
					{
						mailListLength += "<td> " + i + " </td>";
					}
					else
					{
						mailListLength += "<td><a action=\"bypass _maillist_0_1_0_;" + type + ";" + i + fullSearch + "\"> " + i + " </a></td>";
					}
				}
			}
		}
		else
		{
			for (i = 1; i <= maxpage; i++)
			{
				if (i == page)
				{
					mailListLength += "<td> " + i + " </td>";
				}
				else
				{
					mailListLength += "<td><a action=\"bypass maillist_0_1_0_;" + type + ";" + i + fullSearch + "\"> " + i + " </a></td>";
				}
			}
		}
		mailListLength += "<td><table><tr><td></td></tr><tr><td>";
		mailListLength += "<button action=\"bypass _maillist_0_1_0_;" + type + ";" + (page == maxpage ? page : page + 1) + fullSearch + "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 >";
		mailListLength += "</td></tr></table></td>";
		content = content.replace("%maillistlength%", mailListLength);
		
		separateAndSend(content, activeChar);
	}
	
	private void showLetterView(L2PcInstance activeChar, CommunityMailInstance letter)
	{
		if (letter == null)
		{
			showMailList(activeChar, 1, "inbox");
			return;
		}
		
		String content = HtmData.getInstance().getHtm(CB_PATH + "mail/mail-show.htm");
		
		String link = "<a action=\"bypass _maillist_0_1_0_\">Inbox</a>";
		if (letter.location.equalsIgnoreCase("sentbox"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;sentbox\">Sent Box</a>";
		}
		else if (letter.location.equalsIgnoreCase("archive"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;archive\">Mail Archive</a>";
		}
		else if (letter.location.equalsIgnoreCase("temparchive"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;temp_archive\">Temporary Mail Archive</a>";
		}
		link += "&nbsp;&gt;&nbsp;" + letter.subject;
		content = content.replace("%maillink%", link);
		
		content = content.replace("%writer%", getCharName(letter.senderId));
		content = content.replace("%sentDate%", letter.sentDateString);
		content = content.replace("%receiver%", letter.recipientNames);
		content = content.replace("%delDate%", "Unknown");
		content = content.replace("%title%", letter.subject.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;"));
		content = content.replace("%mes%", letter.message.replaceAll("\r\n", "<br>").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;"));
		content = content.replace("%letterId%", letter.letterId + "");
		separateAndSend(content, activeChar);
	}
	
	private static void showWriteView(L2PcInstance activeChar)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "mail/mail-write.htm");
		separateAndSend(content, activeChar);
	}
	
	private static void showWriteView(L2PcInstance activeChar, String parcipientName, CommunityMailInstance letter)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "mail/mail-reply.htm");
		
		String link = "<a action=\"bypass _maillist_0_1_0_\">Inbox</a>";
		if (letter.location.equalsIgnoreCase("sentbox"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;sentbox\">Sent Box</a>";
		}
		else if (letter.location.equalsIgnoreCase("archive"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;archive\">Mail Archive</a>";
		}
		else if (letter.location.equalsIgnoreCase("temparchive"))
		{
			link = "<a action=\"bypass _maillist_0_1_0_;temp_archive\">Temporary Mail Archive</a>";
		}
		link += "&nbsp;&gt;&nbsp;<a action=\"bypass _maillist_0_1_0_;view " + letter.letterId + "\">" + letter.subject + "</a>&nbsp;&gt;&nbsp;";
		content = content.replace("%maillink%", link);
		
		content = content.replace("%recipients%", letter.senderId == activeChar.getObjectId() ? letter.recipientNames : getCharName(letter.senderId));
		content = content.replace("%letterId%", letter.letterId + "");
		send1001(content, activeChar);
		send1002(activeChar, " ", "Re: " + letter.subject, "0");
	}
	
	@SuppressWarnings("resource")
	public static void sendLetter(String recipients, String subject, String message, L2PcInstance activeChar)
	{
		int countTodaysLetters = 0;
		Timestamp ts = new Timestamp(Calendar.getInstance().getTimeInMillis() - 86400000L);
		long date = Calendar.getInstance().getTimeInMillis();
		
		for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(activeChar.getObjectId()))
		{
			if (letter.sentDate.after(ts) && letter.location.equals("sentbox"))
			{
				countTodaysLetters++;
			}
		}
		
		if ((countTodaysLetters >= 10) && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessage.NO_MORE_MESSAGES_TODAY);
			return;
		}
		
		if ((subject == null) || subject.isEmpty())
		{
			subject = "(no subject)";
		}
		
		try (Connection con = DatabaseManager.getConnection())
		{
			Set<String> recipts = new HashSet<>(5);
			String[] recipAr = recipients.split(";");
			for (String r : recipAr)
			{
				recipts.add(r.trim());
			}
			
			message = message.replaceAll("\n", "<br1>");
			
			boolean sent = false;
			int countRecips = 0;
			
			Timestamp time = new Timestamp(date);
			PreparedStatement ps = null;
			
			for (String recipient : recipts)
			{
				int recipId = CharNameData.getInstance().getIdByName(recipient);
				if ((recipId <= 0) || (recipId == activeChar.getObjectId()))
				{
					activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
				}
				else if (!activeChar.isGM())
				{
					if (isGM(recipId))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.CANNOT_MAIL_GM_C1).addString(recipient));
					}
					else if (isBlocked(activeChar, recipId))
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.C1_BLOCKED_YOU_CANNOT_MAIL).addString(recipient));
					}
					else if (isRecipInboxFull(recipId))
					{
						activeChar.sendPacket(SystemMessage.MESSAGE_NOT_SENT);
						
						L2PcInstance PCrecipient = L2World.getInstance().getPlayer(recipient);
						if (PCrecipient != null)
						{
							PCrecipient.sendPacket(SystemMessage.MAILBOX_FULL);
						}
					}
				}
				else if (((countRecips < 5) && !activeChar.isGM()) || activeChar.isGM())
				{
					int id = Community.getInstance().getNewMailId();
					if (ps == null)
					{
						ps = con.prepareStatement(INSERT_NEW_MAIL);
						ps.setInt(3, activeChar.getObjectId());
						ps.setString(4, "inbox");
						ps.setString(5, recipients);
						ps.setString(6, abbreviate(subject, 128));
						ps.setString(7, message);
						ps.setTimestamp(8, time);
						ps.setInt(9, 1);
					}
					ps.setInt(1, recipId);
					ps.setInt(2, id);
					ps.execute();
					sent = true;
					
					CommunityMailInstance letter = new CommunityMailInstance();
					letter.charId = recipId;
					letter.letterId = id;
					letter.senderId = activeChar.getObjectId();
					letter.location = "inbox";
					letter.recipientNames = recipients;
					letter.subject = abbreviate(subject, 128);
					letter.message = message;
					letter.sentDate = time;
					letter.sentDateString = Util.formatDate(letter.sentDate, "yyyy-MM-dd HH:mm");
					letter.unread = true;
					Community.getInstance().getPlayerMails(recipId).add(0, letter);
					
					countRecips++;
					
					L2PcInstance PCrecipient = L2World.getInstance().getPlayer(recipient);
					if (PCrecipient != null)
					{
						PCrecipient.sendPacket(SystemMessage.NEW_MAIL);
						PCrecipient.playSound(PlaySoundType.SYS_MSG_1233);
						PCrecipient.sendPacket(ExMailArrived.STATIC_PACKET);
					}
				}
			}
			
			// Create a copy into activeChar's sent box
			if (ps != null)
			{
				int id = Community.getInstance().getNewMailId();
				
				ps.setInt(1, activeChar.getObjectId());
				ps.setInt(2, id);
				ps.setString(4, "sentbox");
				ps.setInt(9, 0);
				ps.execute();
				ps.close();
				
				CommunityMailInstance letter = new CommunityMailInstance();
				letter.charId = activeChar.getObjectId();
				letter.letterId = id;
				letter.senderId = activeChar.getObjectId();
				letter.location = "sentbox";
				letter.recipientNames = recipients;
				letter.subject = abbreviate(subject, 128);
				letter.message = message;
				letter.sentDate = time;
				letter.sentDateString = Util.formatDate(letter.sentDate, "yyyy-MM-dd HH:mm");
				letter.unread = false;
				Community.getInstance().getPlayerMails(activeChar.getObjectId()).add(0, letter);
			}
			
			if ((countRecips > 5) && !activeChar.isGM())
			{
				activeChar.sendPacket(SystemMessage.ONLY_FIVE_RECIPIENTS);
			}
			
			if (sent)
			{
				activeChar.sendPacket(SystemMessage.SENT_MAIL);
			}
		}
		catch (Exception e)
		{
			LOG.warning("couldnt send letter for " + activeChar.getName() + " " + e.getMessage());
		}
	}
	
	private static int getCountLetters(int objId, String location, String sType, String search)
	{
		int count = 0;
		if (!sType.equals("") && !search.equals(""))
		{
			boolean byTitle = sType.equalsIgnoreCase("title");
			for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(objId))
			{
				if (!letter.location.equals(location))
				{
					continue;
				}
				
				if (byTitle && letter.subject.toLowerCase().contains(search.toLowerCase()))
				{
					count++;
				}
				else if (!byTitle)
				{
					String writer = getCharName(letter.senderId);
					if (writer.toLowerCase().contains(search.toLowerCase()))
					{
						count++;
					}
				}
			}
		}
		else
		{
			for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(objId))
			{
				if (letter.location.equals(location))
				{
					count++;
				}
			}
		}
		return count;
	}
	
	private static boolean isBlocked(L2PcInstance activeChar, int recipId)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player.getObjectId() == recipId)
			{
				if (PcBlockList.isInBlockList(player, activeChar))
				{
					return true;
				}
				
				return false;
			}
		}
		return false;
	}
	
	private void deleteLetter(L2PcInstance activeChar, int letterId)
	{
		for (CommunityMailInstance letter : Community.getInstance().getPlayerMails(activeChar.getObjectId()))
		{
			if (letter.letterId == letterId)
			{
				Community.getInstance().getPlayerMails(activeChar.getObjectId()).remove(letter);
				break;
			}
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MAIL))
		{
			ps.setInt(1, letterId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("couldnt delete letter " + letterId + " " + e);
		}
	}
	
	private void setLetterToRead(L2PcInstance activeChar, int letterId)
	{
		getLetter(activeChar, letterId).unread = false;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(MARK_MAIL_READ))
		{
			ps.setInt(1, 0);
			ps.setInt(2, letterId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("couldnt set unread to false for " + letterId + " " + e);
		}
	}
	
	private void setLetterLocation(L2PcInstance activeChar, int letterId, String location)
	{
		getLetter(activeChar, letterId).location = location;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(SET_LETTER_LOC))
		{
			ps.setString(1, location);
			ps.setInt(2, letterId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning("couldnt set location to false for " + letterId + " " + e);
		}
	}
	
	private static String getCharName(int charId)
	{
		String name = CharNameData.getInstance().getNameById(charId);
		return name == null ? "Unknown" : name;
	}
	
	private static boolean isGM(int charId)
	{
		boolean isGM = false;
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT accesslevel FROM characters WHERE obj_Id = ?"))
		{
			ps.setInt(1, charId);
			try (ResultSet result = ps.executeQuery())
			{
				result.next();
				isGM = result.getInt(1) > 0;
			}
		}
		catch (Exception e)
		{
			LOG.warning(e.getMessage());
		}
		return isGM;
	}
	
	private static boolean isRecipInboxFull(int charId)
	{
		return getCountLetters(charId, "inbox", "", "") >= 100;
	}
	
	private void showLastForum(L2PcInstance activeChar)
	{
		int page = activeChar.getMailPosition() % 1000;
		int type = activeChar.getMailPosition() / 1000;
		
		switch (type)
		{
			case 0:
				showMailList(activeChar, page, "inbox");
				break;
			
			case 1:
				showMailList(activeChar, page, "sentbox");
				break;
			
			case 2:
				showMailList(activeChar, page, "archive");
				break;
			
			case 3:
				showMailList(activeChar, page, "temparchive");
				break;
		}
	}
	
	private static int getMaxPageId(int letterCount)
	{
		if (letterCount < 1)
		{
			return 1;
		}
		
		if ((letterCount % 10) == 0)
		{
			return letterCount / 10;
		}
		
		return (letterCount / 10) + 1;
	}
}
