package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.AdminForgePacket;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * This class handles commands for gm to forge packets
 * @author Maktakien
 */
public class AdminPForge implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_forge",
		"admin_forge2",
		"admin_forge3"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// ----------~ COMMAND ~---------- //
		if (command.equals("admin_forge"))
		{
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_forge2"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				String format = st.nextToken();
				showPage2(activeChar, format);
			}
			catch (Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_forge3"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				String format = st.nextToken();
				boolean broadcast = false;
				if (format.toLowerCase().equals("broadcast"))
				{
					format = st.nextToken();
					broadcast = true;
				}
				AdminForgePacket sp = new AdminForgePacket();
				for (int i = 0; i < format.length(); i++)
				{
					String val = st.nextToken();
					if (val.toLowerCase().equals("$objid"))
					{
						val = String.valueOf(activeChar.getObjectId());
					}
					else if (val.toLowerCase().equals("$tobjid"))
					{
						val = String.valueOf(activeChar.getTarget().getObjectId());
					}
					else if (val.toLowerCase().equals("$bobjid"))
					{
						if (activeChar.getBoat() != null)
						{
							val = String.valueOf(activeChar.getBoat().getObjectId());
						}
					}
					else if (val.toLowerCase().equals("$clanid"))
					{
						val = String.valueOf(activeChar.getObjectId());
					}
					else if (val.toLowerCase().equals("$allyid"))
					{
						val = String.valueOf(activeChar.getAllyId());
					}
					else if (val.toLowerCase().equals("$tclanid"))
					{
						val = String.valueOf(((L2PcInstance) activeChar.getTarget()).getObjectId());
					}
					else if (val.toLowerCase().equals("$tallyid"))
					{
						val = String.valueOf(((L2PcInstance) activeChar.getTarget()).getAllyId());
					}
					else if (val.toLowerCase().equals("$x"))
					{
						val = String.valueOf(activeChar.getX());
					}
					else if (val.toLowerCase().equals("$y"))
					{
						val = String.valueOf(activeChar.getY());
					}
					else if (val.toLowerCase().equals("$z"))
					{
						val = String.valueOf(activeChar.getZ());
					}
					else if (val.toLowerCase().equals("$heading"))
					{
						val = String.valueOf(activeChar.getHeading());
					}
					else if (val.toLowerCase().equals("$tx"))
					{
						val = String.valueOf(activeChar.getTarget().getX());
					}
					else if (val.toLowerCase().equals("$ty"))
					{
						val = String.valueOf(activeChar.getTarget().getY());
					}
					else if (val.toLowerCase().equals("$tz"))
					{
						val = String.valueOf(activeChar.getTarget().getZ());
					}
					else if (val.toLowerCase().equals("$theading"))
					{
						val = String.valueOf(((L2PcInstance) activeChar.getTarget()).getHeading());
					}
					
					sp.addPart(format.getBytes()[i], val);
				}
				if (broadcast == true)
				{
					activeChar.broadcastPacket(sp);
				}
				else
				{
					activeChar.sendPacket(sp);
				}
				showPage3(activeChar, format, command);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void showMainPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		
		replyMSG.append("<center>L2J Forge Panel</center><br>");
		replyMSG.append("Format:<edit var=\"format\" width=100><br>");
		replyMSG.append("<button value=\"Step2\" action=\"bypass -h admin_forge2 $format\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br>");
		replyMSG.append("Only c h d f s b or x work<br>");
		replyMSG.append("</body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public void showPage3(L2PcInstance activeChar, String format, String command)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		
		replyMSG.append("<center>L2J Forge Panel 3</center><br>");
		replyMSG.append("GG !! If you can see this, there was no critical :)<br>");
		replyMSG.append("and packet (" + format + ") was sent<br><br>");
		replyMSG.append("<button value=\"Try again ?\" action=\"bypass -h admin_forge\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		replyMSG.append("<br><br>Debug: cmd string :" + command + "<br>");
		replyMSG.append("</body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public void showPage2(L2PcInstance activeChar, String format)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<center>L2J Forge Panel 2</center><br>Format:" + format);
		sb.append("<br>No spaces in values please ;)<br>Decimal values for c h d, a float (with point) for f, a string for s and for x/b the hexadecimal value");
		sb.append("<br>Values<br>");
		for (int i = 0; i < format.length(); i++)
		{
			sb.append(format.charAt(i) + " : <edit var=\"v" + i + "\" width=100> <br>");
		}
		sb.append("<br><button value=\"Send\" action=\"bypass -h admin_forge3 " + format);
		for (int i = 0; i < format.length(); i++)
		{
			sb.append(" $v" + i);
		}
		sb.append("\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		
		sb.append("<br><button value=\"Broadcast\" action=\"bypass -h admin_forge3 broadcast " + format);
		for (int i = 0; i < format.length(); i++)
		{
			sb.append(" $v" + i);
		}
		sb.append("\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
