package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.PartyCommandChannel;
import l2j.gameserver.network.external.server.ExMultiPartyCommandChannelInfo;

/**
 * @author Chris
 */
public class UserChannelListUpdate implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			97
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if ((activeChar.getParty() == null) || (activeChar.getParty().getCommandChannel() == null))
		{
			return false;
		}
		
		PartyCommandChannel channel = activeChar.getParty().getCommandChannel();
		
		activeChar.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
		return true;
	}
}
