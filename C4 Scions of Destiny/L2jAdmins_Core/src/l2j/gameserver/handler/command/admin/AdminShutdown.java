package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.Shutdown;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class handles following admin commands: - server_shutdown [sec] = shows menu or shuts down server in sec seconds
 * @version $Revision: 1.5.2.1.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminShutdown implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		// misc
		"admin_server_shutdown",
		"admin_server_restart",
		"admin_server_abort"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// Generamos un log con el COMMAND usado por el GM
		GMAudit.auditGMAction(activeChar.getName(), command, activeChar.getTarget() == null ? "no-target?" : "target " + activeChar.getTarget().getName(), "");
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_server_shutdown"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				Shutdown.getInstance().startShutdown(val, false);
			}
			catch (Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_server_restart"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				Shutdown.getInstance().startShutdown(val, true);
			}
			catch (Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_server_abort"))
		{
			Shutdown.getInstance().abort(activeChar);
		}
		
		AdminHelpPage.showHelpPage(activeChar, "menuRestart.htm");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
