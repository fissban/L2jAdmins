package l2j.gameserver.handler.command.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.util.audit.GMAudit;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos para el arreglo de un personaje atorado.<br>
 *         Contiene el comando: <br>
 *         <li>repair
 */
public class AdminRepairChar implements IAdminCommandHandler
{
	private static final Logger LOG = Logger.getLogger(AdminRepairChar.class.getName());
	private static String target;
	
	private static String[] ADMINCOMMAND =
	{
		"admin_repair"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		if (event.equals("admin_repair"))
		{
			if (st.countTokens() != 1)
			{
				activeChar.sendMessage("Correct command //admin_repair <PlayerName>");
				return false;
			}
			
			target = st.nextToken();
			
			if (target.equalsIgnoreCase(activeChar.getName()))
			{
				activeChar.sendMessage("You can not apply it to yourself");
				AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
				return false;
			}
			
			// We create a record of the used command.
			GMAudit.auditGMAction(activeChar.getName(), command, target == "" ? "No Target" : "Target: " + target, "");
			
			try (Connection con = DatabaseManager.getConnection())
			{
				/** Character coordinates are placed in Talking Island */
				PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE char_name=?");
				statement.setInt(1, -84318);
				statement.setInt(2, 244579);
				statement.setInt(3, -3730);
				statement.setString(4, target);
				
				statement.execute();
				
				if (statement.getUpdateCount() == 0)
				{
					statement.close();
					activeChar.sendMessage("Character not found!");
					AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
					return false;
				}
				statement.close();
				
				/** The object number of the character is selected according name */
				statement = con.prepareStatement("SELECT obj_id FROM characters where char_name=?");
				statement.setString(1, target);
				ResultSet rset = statement.executeQuery();
				int objId = 0;
				if (rset.next())
				{
					objId = rset.getInt(1);
				}
				
				rset.close();
				statement.close();
				
				if (objId == 0)
				{
					con.close();
					AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
					return false;
				}
				
				/** All items of the character are removed and placed in inventory */
				statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?");
				statement.setInt(1, objId);
				statement.execute();
				statement.close();
				
				activeChar.sendMessage("Character " + target + " repaired successfully!");
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "could not repair char: ", e);
			}
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
