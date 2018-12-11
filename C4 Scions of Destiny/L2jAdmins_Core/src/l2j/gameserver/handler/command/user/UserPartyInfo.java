package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.party.enums.PartyItemDitributionType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Tempy
 */
public class UserPartyInfo implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			81
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (!activeChar.isInParty())
		{
			activeChar.sendMessage("Party does not exist.");
			return false;
		}
		
		final Party playerParty = activeChar.getParty();
		final int memberCount = playerParty.getMemberCount();
		final PartyItemDitributionType lootDistribution = playerParty.getLootDistribution();
		final String partyLeader = playerParty.getLeader().getName();
		
		activeChar.sendPacket(SystemMessage.PARTY_INFORMATION);
		
		switch (lootDistribution)
		{
			case LOOTER:
				activeChar.sendPacket(SystemMessage.LOOTING_FINDERS_KEEPERS);
				break;
			case ORDER:
				activeChar.sendPacket(SystemMessage.LOOTING_BY_TURN);
				break;
			case ORDER_SPOIL:
				activeChar.sendPacket(SystemMessage.LOOTING_BY_TURN_INCLUDE_SPOIL);
				break;
			case RANDOM:
				activeChar.sendPacket(SystemMessage.LOOTING_RANDOM);
				break;
			case RANDOM_SPOIL:
				activeChar.sendPacket(SystemMessage.LOOTING_RANDOM_INCLUDE_SPOIL);
				break;
		}
		
		activeChar.sendPacket(new SystemMessage(SystemMessage.PARTY_LEADER_C1).addString(partyLeader));
		activeChar.sendMessage("Members: " + memberCount + "/9");
		
		activeChar.sendPacket(SystemMessage.PARTY_INFORMATION);
		return true;
	}
}
