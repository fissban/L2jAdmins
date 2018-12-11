package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.AdminCommandData;
import l2j.gameserver.handler.CommandAdminHandler;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.util.audit.GMAudit;
import main.EngineModsManager;

/**
 * This class handles all GM commands triggered by //command
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:29 $
 */
public class SendBypassBuildCmd extends AClientPacket
{
	public final static int GM_MESSAGE = 9;
	public final static int ANNOUNCEMENT = 10;
	
	private String command;
	
	@Override
	protected void readImpl()
	{
		command = readS();
		if (command != null)
		{
			command = command.trim();
		}
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (EngineModsManager.onVoiced(activeChar, "admin_" + command))
		{
			return;
		}
		
		if (!activeChar.isGM() && !"gm".equalsIgnoreCase(command))
		{
			IllegalAction.report(activeChar, "Warning!! Non-gm character " + activeChar.getName() + " requests gm bypass handler, hack?");
			return;
		}
		
		if (activeChar.getAccessLevel() < AdminCommandData.getInstance().getAccesCommandAdmin("admin_" + command.split(" ")[0]))
		{
			activeChar.sendMessage("you do not have permissions to use this command");
			return;
		}
		
		IAdminCommandHandler ach = CommandAdminHandler.getHandler("admin_" + command);
		if (ach == null)
		{
			LOG.warning("No handler registered for bypass '" + command + "'");
			return;
		}
		
		if (Config.GMAUDIT)
		{
			GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
		}
		
		ach.useAdminCommand("admin_" + command, activeChar);
	}
}
