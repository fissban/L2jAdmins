package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.Olympiad;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos para el manejo de las olimpiadas.<br>
 *         Contiene los comandos: <br>
 *         <li>ban_char
 *         <li>jail
 *         <li>unban_char
 *         <li>unjail
 */
public class AdminOlympiad implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		"admin_endolympiad",
		"admin_saveolymp"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		/** ====================== [ OLYMPIAD ] ====================== */
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_endolympiad"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
				activeChar.sendMessage("Heroes were formed!!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Can not finished the olympiads");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_saveolymp"))
		{
			try
			{
				Olympiad.getInstance().saveOlympiadStatus();
				activeChar.sendMessage("Olympiad data saved!!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Can not save the olympiads");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
