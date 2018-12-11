package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos para la administracion del chat los players.<br>
 *         Contiene los comandos: <br>
 *         <li>banchat
 *         <li>unbanchat
 */
public class AdminBanChat implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		"admin_banchat",
		"admin_unbanchat"
	};
	
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_banchat"))
		{
			int banLength = -1; // permanent
			
			if (st.countTokens() != 2)
			{
				activeChar.sendMessage("Correct command //banchat <playerName> <time in minutes>");
				return false;
			}
			
			String playerName = st.nextToken();
			target = L2World.getInstance().getPlayer(playerName);
			try
			{
				banLength = Integer.parseInt(st.nextToken()); // time in minutes
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Invalid number");
				return false;
			}
			
			if (banLength > -1)
			{
				target.setChatUnbanTask(banLength);
			}
			
			activeChar.sendMessage(target.getName() + " is now chat banned for " + banLength + " minutes.");
			target.sendMessage("You are now chat banned for " + banLength + " minutes.");
			target.setChatBanned(true);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unbanchat"))
		{
			if (st.countTokens() != 1)
			{
				activeChar.sendMessage("Correct command //unbanchat <playerName>");
				return false;
			}
			
			target = L2World.getInstance().getPlayer(st.nextToken());
			activeChar.sendMessage(target.getName() + "'s chat ban has been lifted.");
			target.setChatBanned(false);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
