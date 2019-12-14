package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyCommandChannel;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author -Wooden-
 */
public class RequestExAcceptJoinMPCC extends AClientPacket
{
	private static final int ALT_CHANNEL_ACTIVATION_COUNT = 5;
	
	private int response;
	
	@Override
	protected void readImpl()
	{
		response = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		L2PcInstance partner = player.getRequestInvite().getPartner();
		if (partner == null)
		{
			return;
		}
		
		if (response == 1)
		{
			if (partner.isInParty() && player.isInParty())
			{
				if (!partner.getParty().isInCommandChannel())
				{
					new PartyCommandChannel(partner); // Create new CC
					partner.getParty().getCommandChannel().addParty(player.getParty());
					
					if (partner.getParty().getCommandChannel().getParties().size() < ALT_CHANNEL_ACTIVATION_COUNT)
					{
						partner.getParty().getCommandChannel().broadcastToChannelMembers(new SystemMessage(SystemMessage.COMMAND_CHANNEL_FORMED));
					}
				}
				else
				{
					if (partner.getParty().getCommandChannel().getChannelLeader().equals(partner))
					{
						if (partner.getParty().getCommandChannel().getParties().size() < 50)
						{
							player.sendPacket(new SystemMessage(SystemMessage.JOINED_COMMAND_CHANNEL));
							partner.getParty().getCommandChannel().addParty(player.getParty());
							
							if (partner.getParty().getCommandChannel().getParties().size() < ALT_CHANNEL_ACTIVATION_COUNT)
							{
								partner.sendMessage("The number of remaining parties is " + (ALT_CHANNEL_ACTIVATION_COUNT - partner.getParty().getCommandChannel().getParties().size()) + " until a channel is activated.");
							}
						}
					}
					else
					{
						partner.sendPacket(new SystemMessage(SystemMessage.CANNOT_LONGER_SETUP_COMMAND_CHANNEL));
					}
				}
			}
		}
		else
		{
			partner.sendPacket(new SystemMessage(SystemMessage.C1_DECLINED_CHANNEL_INVITATION).addString(player.getName()));
		}
		
		partner.getRequestInvite().endRequest();
	}
}
