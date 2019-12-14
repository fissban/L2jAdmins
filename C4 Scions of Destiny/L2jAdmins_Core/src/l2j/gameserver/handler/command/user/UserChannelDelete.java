package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyCommandChannel;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Chris
 */
public class UserChannelDelete implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			93
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (activeChar.isInParty())
		{
			if (activeChar.getParty().isLeader(activeChar) && activeChar.getParty().isInCommandChannel() && activeChar.getParty().getCommandChannel().getChannelLeader().equals(activeChar))
			{
				PartyCommandChannel channel = activeChar.getParty().getCommandChannel();
				channel.broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_DISBANDED));
				channel.disbandChannel();
				return true;
			}
		}
		
		return false;
	}
}
