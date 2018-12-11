package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.AnnouncementsData.AnnouncementType;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.AnnouncementHolder;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos para el manejo de los anuncios.<br>
 *         Contiene los comandos: <br>
 *         <li>list_announcements
 *         <li>admin_add_announcement
 *         <li>admin_announce
 *         <li>announce_announcements
 *         <li>announce_menu
 *         <li>del_announcement
 *         <li>reload_announcements
 *         <li>reload_autoannounce
 */
public class AdminAnnouncements implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		// HTML
		"admin_list_announcements",
		// MISC
		"admin_add_announcement",
		"admin_announce",
		"admin_announce_menu",
		"admin_del_announcement",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		
		String event = st.nextToken();// actual command
		
		/** ====================== [ HTML ] ====================== */
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_list_announcements"))
		{
			listAnnouncements(activeChar);
		}
		/** ====================== [ MISC ] ====================== */
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_add_announcement"))
		{
			try
			{
				StringTokenizer st1 = new StringTokenizer(command.substring(23), "**");
				String announcement = st1.nextToken();
				
				String parameters = st1.nextToken().substring(1);
				
				StringTokenizer st2 = new StringTokenizer(parameters, " ");
				AnnouncementType type = AnnouncementType.valueOf(st2.nextToken());
				boolean repeteable = st2.nextToken().equals("true") ? true : false;
				int reuse = 0;
				try
				{
					if (st.hasMoreTokens())
					{
						reuse = Integer.parseInt(st2.nextToken());
					}
				}
				catch (Exception e)
				{
					//
				}
				
				AnnouncementsData.getInstance().addAnnouncement(announcement, type, repeteable, reuse);
				listAnnouncements(activeChar);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.startsWith("admin_announce"))
		{
			try
			{
				AnnouncementsData.getInstance().announceToAll(command.substring(15));
			}
			catch (Exception e)
			{
				//
			}
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_announce_menu"))
		{
			try
			{
				AnnouncementsData.getInstance().announceToAll(command.substring(20));
				listAnnouncements(activeChar);
			}
			catch (Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_del_announcement"))
		{
			try
			{
				AnnouncementsData.getInstance().removeAnnouncement(Integer.parseInt(st.nextToken()));
				listAnnouncements(activeChar);
			}
			catch (Exception e)
			{
				
			}
		}
		
		return true;
	}
	
	// Misc
	
	/**
	 * Html with the announcements shown.
	 * @param activeChar
	 */
	public void listAnnouncements(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body><center>");
		sb.append("<table width=260>");
		sb.append("<tr>");
		sb.append("<td width=80><button value=\"Main\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.btn1_normal></td>");
		sb.append("<td width=100><center>Menu</center></td>");
		sb.append("<td width=80><button value=\"Back\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.btn1_normal></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br><br>");
		sb.append("Add a new announcement:");
		sb.append("<multiedit var=\"new_announcement\" width=260 height=30><br>");
		
		StringBuilder sb1 = new StringBuilder();
		int size = AnnouncementType.values().length;
		
		for (AnnouncementType at : AnnouncementType.values())
		{
			sb1.append(at);
			size--;
			if (size > 0)
			{
				sb1.append(";");
			}
		}
		
		sb.append("<table width=260>");
		sb.append("<tr>");
		sb.append("<td width=40><font color=\"LEVEL\">SayType:</font></td>");
		sb.append("<td width=180><combobox width=180 var=kSayType list=\"" + sb1.toString() + "\"></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width=40><font color=\"LEVEL\">Repeteable:</font></td>");
		sb.append("<td width=180><combobox width=180 var=kRepeteable list=\"false;true\"></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width=40><font color=\"LEVEL\">Reuse:</font></td>");
		sb.append("<td width=180><edit var=box width=180 height=16></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><button value=\"Add\" action=\"bypass -h admin_add_announcement $new_announcement ** $kSayType $kRepeteable $box\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.btn1_normal></td>");
		
		sb.append("</tr></table>");
		sb.append("<br>");
		
		sb.append("<font color=\"LEVEL\">Predefined announcements</font>.<br>");
		
		int i = 0;
		for (AnnouncementHolder announce : AnnouncementsData.getInstance().getAnnouncements())
		{
			sb.append("<table width=260 bgcolor=000000>");
			sb.append("<tr><td width=260>--------------------------------------------------------------</td></tr>");
			sb.append("<tr><td width=260>" + announce.getAnnouncement() + "</td></tr>");
			sb.append("<tr><td width=260><font color=\"LEVEL\">* announce type: </font>" + announce.getAnnouncementType().toString() + "</td></tr>");
			sb.append("<tr><td width=260><font color=\"LEVEL\">* is repeteable: </font>" + (announce.isRepeatable() ? "true" : "false") + "</td></tr>");
			sb.append("<tr><td width=260><font color=\"LEVEL\">* reuse (min): </font>" + announce.getReuse() + "</td></tr>");
			sb.append("<tr><td width=260 align=left><button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.btn1_normal></td></tr>");
			sb.append("</table>");
			
			i++;
		}
		sb.append("</center></body></html>");
		sb.append("<br>1");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
