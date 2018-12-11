package l2j.gameserver.handler.say;

import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SayChannelAll implements ISayHandler
{
	private final int ALT_CHANNEL_ACTIVATION_COUNT = 5;
	
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.CHANNEL_ALL
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (activeChar.getParty() != null)
		{
			if (!activeChar.getParty().isLeader(activeChar))
			{
				activeChar.sendPacket(SystemMessage.CANT_USE_COMMAND_CHANNEL);
				return;
			}
			
			if (activeChar.getParty().getCommandChannel() != null)
			{
				if (activeChar.getParty().getCommandChannel().getParties().size() < ALT_CHANNEL_ACTIVATION_COUNT)
				{
					activeChar.sendMessage("The command channel is activated only if at least " + ALT_CHANNEL_ACTIVATION_COUNT + " parties participate in.");
					return;
				}
				
				activeChar.getParty().getCommandChannel().broadcastToChannelMembers(new CreatureSay(activeChar, type, activeChar.getName(), text));
			}
		}
	}
}
