package l2j.gameserver.handler.command.admin;

import l2j.gameserver.data.GmListData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * This class handles following admin commands: - gmchat text = sends text to all online GM's
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminGmChat implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_gmchat"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_gmchat"))
		{
			try
			{
				GmListData.getInstance().broadcastToGMs(new CreatureSay(SayType.ALLIANCE, activeChar.getName(), command.substring(13)));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// empty message.. ignore
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
