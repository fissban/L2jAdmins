package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.actor.manager.pc.party.PartyCommandChannel;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Chris
 */
public class UserChannelLeave implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			96
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		Party party = activeChar.getParty();
		if (party != null)
		{
			if (party.isLeader(activeChar) && party.isInCommandChannel())
			{
				PartyCommandChannel channel = party.getCommandChannel();
				
				for (Party leftParty : channel.getParties())
				{
					if ((leftParty == null) || (leftParty == party))
					{
						continue;
					}
					
					leftParty.broadcastToPartyMembers(new SystemMessage(SystemMessage.C1_PARTY_LEFT_COMMAND_CHANNEL).addString(party.getMembers().get(0).getName()));
				}
				
				channel.removeParty(party);
				party.getLeader().sendPacket(SystemMessage.LEFT_COMMAND_CHANNEL);
				return true;
			}
		}
		
		return false;
	}
}
