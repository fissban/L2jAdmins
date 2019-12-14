package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.util.audit.GMAudit;

/**
 * Pledge Manipulation //pledge <create|dismiss>
 */
public class AdminPledge implements IAdminCommandHandler
{
	private static final Logger LOG = Logger.getLogger(AdminPledge.class.getName());
	
	private static final String[] ADMINCOMMAND =
	{
		"admin_pledge"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_pledge"))
		{
			String action = null;
			String parameter = null;
			GMAudit.auditGMAction(activeChar.getName(), command, activeChar.getName(), "");
			final StringTokenizer st = new StringTokenizer(command);
			
			try
			{
				st.nextToken();
				action = st.nextToken(); // create|dismiss|setlevel
				parameter = st.nextToken(); // clanname|nothing|nothing|level
			}
			catch (final Exception e)
			{
				//
			}
			
			if (action != null)
			{
				final L2PcInstance target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				if (parameter == null)
				{
					activeChar.sendMessage("Usage: //pledge <setlevel> <number>");
				}
				else if (action.equals("create"))
				{
					try
					{
						final long time = target.getClanCreateExpiryTime();
						target.setClanCreateExpiryTime(0);
						final Clan clan = ClanData.getInstance().createClan(target, parameter);
						if (clan != null)
						{
							activeChar.sendMessage("Clan " + parameter + " created! Leader: " + target.getName());
						}
						else
						{
							target.setClanCreateExpiryTime(time);
							activeChar.sendMessage("There was a problem while creating the clan.");
						}
					}
					catch (final Exception e)
					{
						LOG.warning("Error creating pledge by GM command: " + e);
					}
				}
				else if (!target.isClanLeader())
				{
					activeChar.sendMessage("Target is not a clan leader.");
					showMainPage(activeChar);
					return false;
				}
				else if (action.equals("dismiss"))
				{
					
					ClanData.getInstance().destroyClan(target.getClanId());
					if (target.getClan() == null)
					{
						activeChar.sendMessage("Clan disbanded.");
					}
					else
					{
						activeChar.sendMessage("There was a problem while destroying the clan.");
					}
					
				}
				else if (action.equals("setlevel"))
				{
					final int level = Integer.parseInt(parameter);
					if ((level >= 0) && (level < 6))
					{
						target.getClan().changeLevel(level);
						activeChar.sendMessage("You set level " + level + " for clan " + target.getClan().getName());
					}
					else
					{
						activeChar.sendMessage("Incorrect level.");
					}
				}
			}
		}
		
		showMainPage(activeChar);
		return true;
	}
	
	public static void showMainPage(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<center><table width=260><tr><td width=80>");
		sb.append("<button value=\"Main\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over>");
		sb.append("</td><td width=100>");
		sb.append("<center>Clan Management</center>");
		sb.append("</td><td width=80>");
		sb.append("</td></tr></table></center><br>");
		sb.append("<center>Create / Destroy / Level 0-5:</center>");
		sb.append("<center><edit var=\"menu_command\" width=100 height=15></center><br>");
		sb.append("<center><table><tr><td>");
		sb.append("<button value=\"Create\" action=\"bypass -h admin_pledge create $menu_command\" width=55 width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td><td>");
		sb.append("<button value=\"Delete\" action=\"bypass -h admin_pledge dismiss $menu_command\" width=55 width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td><td>");
		sb.append("<button value=\"SetLevel\" action=\"bypass -h admin_pledge setlevel $menu_command\" width=55 width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td></tr>");
		sb.append("</table></center>");
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
