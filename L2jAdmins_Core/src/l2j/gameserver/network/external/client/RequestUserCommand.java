package l2j.gameserver.network.external.client;

import l2j.gameserver.handler.CommandUserHandler;
import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestUserCommand extends AClientPacket
{
	private int command;
	
	@Override
	protected void readImpl()
	{
		command = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		IUserCommandHandler handler = CommandUserHandler.getInstance().getHandler(command);
		if (handler != null)
		{
			handler.useUserCommand(command, getClient().getActiveChar());
		}
	}
}
