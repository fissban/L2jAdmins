package l2j.gameserver.handler.command.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;

import l2j.L2DatabaseFactory;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;

/**
 * Comandos para el manejo de sanciones de los players.<br>
 * Contiene los comandos: <br>
 * <li>ban_char
 * <li>jail
 * <li>unban_char
 * <li>unjail
 * @author fissban, Reynald0
 */
public class AdminBanChar implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		"admin_ban_char",
		"admin_jail",
		"admin_unban_char",
		"admin_unjail"
	};
	
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_ban_char"))
		{
			if (st.hasMoreTokens())
			{
				String playerName = st.nextToken();
				
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?"))
				{
					statement.setInt(1, -1);
					statement.setString(2, playerName);
					statement.execute();
					
					if (statement.getUpdateCount() == 0)
					{
						activeChar.sendMessage("Character not found or access level unaltered.");
					}
					else
					{
						activeChar.sendMessage(playerName + " now has an access level of " + 0);
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Exception while changing character's access level");
				}
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target == null)
				{
					return false;
				}
				
				if (target == activeChar)
				{
					activeChar.sendMessage("You can not give yourself ban");
				}
				else if (target != null)
				{
					target.sendMessage("Your character has been banned. Goodbye.");
					target.setAccessLevel(-1);
					target.closeConnection();
					activeChar.sendMessage("The character " + target.getName() + " has now been banned.");
				}
			}
			
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_jail"))
		{
			int time = 0;
			if (st.countTokens() > 2)
			{
				activeChar.sendMessage("Correct command //jail <playerName> <time in minutes>");
			}
			else if ((st.countTokens() >= 1) && (st.countTokens() <= 2))
			{
				String targetName = st.nextToken();
				target = L2World.getInstance().getPlayer(targetName);
				
				if (st.hasMoreTokens())
				{
					try
					{
						time = Integer.parseInt(st.nextToken());
					}
					catch (Exception e)
					{
						activeChar.sendMessage("Invalid number.");
						return false;
					}
				}
				
				if (target != null)
				{
					if (target.equals(activeChar))
					{
						activeChar.sendMessage("You can not apply it to yourself.");
					}
					else
					{
						target.setInJail(true, time);
						activeChar.sendMessage("Character " + targetName + " has been jailed for " + (time > 0 ? (time + " minutes.") : "ever!"));
					}
				}
				else
				{
					jailOfflinePlayer(activeChar, targetName, time, true);
				}
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target != null)
				{
					if (target.equals(activeChar))
					{
						activeChar.sendMessage("You can not apply it to yourself.");
					}
					else
					{
						target.setInJail(true, time);
						activeChar.sendMessage("Character " + target.getName() + " has been jailed for ever!");
					}
				}
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unban_char"))
		{
			String playerName = "";
			
			if (st.hasMoreTokens())
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?"))
				{
					statement.setInt(1, 0);
					statement.setString(2, playerName);
					statement.execute();
					
					if (statement.getUpdateCount() == 0)
					{
						activeChar.sendMessage("Character not found or access level unaltered.");
					}
					else
					{
						activeChar.sendMessage(playerName + " now has an access level of " + 0);
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Exception while changing character's access level");
				}
			}
			else
			{
				activeChar.sendMessage("Please enter name of player");
			}
			
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_unjail"))
		{
			if (st.hasMoreTokens())
			{
				String targetName = st.nextToken();
				target = L2World.getInstance().getPlayer(targetName);
				
				if (target != null)
				{
					if (target.isInJail())
					{
						target.setInJail(false, 0);
						activeChar.sendMessage("Character " + targetName + " has been unjailed!");
					}
					else
					{
						activeChar.sendMessage(targetName + " is not in jail.");
					}
				}
				else
				{
					jailOfflinePlayer(activeChar, targetName, 0, false);
				}
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target != null)
				{
					if (target.isInJail())
					{
						target.setInJail(false, 0);
						activeChar.sendMessage("Character " + target.getName() + " has been unjailed!");
					}
					else
					{
						activeChar.sendMessage(target.getName() + " is not in jail.");
					}
				}
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		
		return true;
	}
	
	/**
	 * We send a player disconnected to jail or we take it out of there
	 * @param activeChar : Character that executed the command.
	 * @param name       : Character name.
	 * @param delay      : Time in minutes.
	 * @param jail       : true = go jail <br>
	 *                       false = out jail.
	 */
	private static void jailOfflinePlayer(L2PcInstance activeChar, String name, int delay, boolean jail)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?"))
		{
			statement.setInt(1, jail ? -114356 : 17836);
			statement.setInt(2, jail ? -249645 : 170178);
			statement.setInt(3, jail ? -2984 : -3507);
			statement.setInt(4, jail ? 1 : 0);
			statement.setLong(5, delay * 60000);
			statement.setString(6, name);
			statement.execute();
			
			if (statement.getUpdateCount() == 0)
			{
				activeChar.sendMessage("Character not found!");
			}
			else
			{
				String msg = "Character " + name + " has been unjailed!";
				if (jail)
				{
					msg = "Character " + name + " jailed for " + (delay > 0 ? delay + " minutes." : "ever!");
				}
				activeChar.sendMessage(msg);
			}
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Exception while jailing player");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
