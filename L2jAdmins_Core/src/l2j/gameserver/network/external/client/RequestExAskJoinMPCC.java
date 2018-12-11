package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.request.RequestPacketType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExAskJoinMPCC;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Format: (ch) S
 * @author -Wooden-
 */
public class RequestExAskJoinMPCC extends AClientPacket
{
	private String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Party activeParty = activeChar.getParty();
		if (activeParty == null)
		{
			return;
		}
		
		L2PcInstance player = L2World.getInstance().getPlayer(name);
		if (player == null)
		{
			return;
		}
		
		boolean canInvite = false;
		if (activeParty.isLeader(activeChar))
		{
			if (activeParty.isInCommandChannel())
			{
				if (activeParty.getCommandChannel().getChannelLeader().equals(activeChar) && (activeParty.getCommandChannel().getParties().size() < 50))
				{
					canInvite = true;
				}
			}
			else
			{
				if ((activeChar.getClan() != null) && (activeChar.getClan().getLevel() > 4) && (activeChar.getClan().getLeaderId() == activeChar.getObjectId()))
				{
					canInvite = true;
				}
			}
		}
		
		if (canInvite)
		{
			if (player.isInParty())
			{
				if (activeChar.getParty().equals(player.getParty()))
				{
					return;
				}
				
				if (player.getParty().isInCommandChannel())
				{
					SystemMessage sm = new SystemMessage(SystemMessage.C1_ALREADY_MEMBER_OF_COMMAND_CHANNEL);
					sm.addString(player.getParty().getLeader().getName());
					activeChar.sendPacket(sm);
					sm = null;
				}
				else
				{
					// ready to open a new CC
					// send request to targets Party's PartyLeader
					askJoinMPCC(activeChar, player);
				}
			}
			else
			{
				activeChar.sendMessage("Target does not belong to a party.");
			}
		}
		else
		{
			if (activeParty.isInCommandChannel())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.CANNOT_INVITE_TO_COMMAND_CHANNEL));
			}
			else
			{
				activeChar.sendMessage("Only Party leader, a Clan Leader with a clan of level 5 or more, can open a command channel.");
			}
		}
	}
	
	private void askJoinMPCC(L2PcInstance requestor, L2PcInstance target)
	{
		if (!requestor.getRequestInvite().startRequest(target.getParty().getMembers().get(0), RequestPacketType.JOIN_MPCC))
		{
			return;
		}
		
		target.getParty().getLeader().sendPacket(new ExAskJoinMPCC(requestor.getName()));
		requestor.sendMessage("You have invited a party to the channel.");
	}
}
