package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Tempy
 */
public class UserMount implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			61
		};
	}
	
	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		return activeChar.mountPlayer(activeChar.getPet());
	}
}
