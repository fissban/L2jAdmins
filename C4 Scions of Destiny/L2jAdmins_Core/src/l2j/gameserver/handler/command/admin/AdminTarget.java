package l2j.gameserver.handler.command.admin;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;

/**
 * This class handles following admin commands: - target name = sets player with respective name as target
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:05:56 $
 */
public class AdminTarget implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_target"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_target"))
		{
			try
			{
				String targetName = command.substring(13);
				L2Object obj = L2World.getInstance().getPlayer(targetName);
				if ((obj != null) && (obj instanceof L2PcInstance))
				{
					obj.onAction(activeChar, true);
				}
				else
				{
					activeChar.sendMessage("Player " + targetName + " not found");
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify correct name.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
