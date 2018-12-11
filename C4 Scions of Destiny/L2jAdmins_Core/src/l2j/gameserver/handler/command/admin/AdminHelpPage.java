package l2j.gameserver.handler.command.admin;

import l2j.gameserver.data.HtmData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * This class handles following admin commands: - help path = shows /data/html/admin/path file to char, should not be used by GM's directly
 * @version $Revision: 1.2.4.3 $ $Date: 2005/04/11 10:06:02 $
 */
public class AdminHelpPage implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_help"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_help"))
		{
			try
			{
				String val = command.substring(11);
				showHelpPage(activeChar, val);
			}
			catch (Exception e)
			{
				// case of empty filename
			}
		}
		
		return true;
	}
	
	public static void showHelpPage(L2PcInstance targetChar, String filename)
	{
		String content = HtmData.getInstance().getHtmForce("data/html/admin/" + filename);
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setHtml(content);
		targetChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
