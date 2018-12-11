package l2j.gameserver.handler.command.admin;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class AdminHelpTarget implements IAdminCommandHandler
{
	@Override
	public String[] getAdminCommandList()
	{
		return new String[]
		{
			"admin_gettarget",
		};
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// TODO por ahora no le daremos accion alguna
		
		return true;
	}
	
	/**
	 * @param  activeChar
	 * @return            nuestro target solo si es instanceof L2PcInstance, en caso contrario devuelve null y un mensaje
	 */
	public static L2PcInstance getPlayer(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return null;
		}
		
		if (target instanceof L2PcInstance)
		{
			return (L2PcInstance) target;
		}
		
		activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
		return null;
	}
}
