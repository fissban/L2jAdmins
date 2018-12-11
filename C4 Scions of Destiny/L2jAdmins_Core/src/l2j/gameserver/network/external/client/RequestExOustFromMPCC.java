package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author -Wooden-
 */
public class RequestExOustFromMPCC extends AClientPacket
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
		L2PcInstance target = L2World.getInstance().getPlayer(name);
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((target != null) && (target != activeChar) && target.isInParty() && activeChar.isInParty() && activeChar.getParty().isInCommandChannel() && target.getParty().isInCommandChannel() && (target.getParty().getCommandChannel() == activeChar.getParty().getCommandChannel())
			&& activeChar.getParty().getCommandChannel().getChannelLeader().equals(activeChar))
		{
			target.getParty().getCommandChannel().removeParty(target.getParty());
			target.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.DISMISSED_FROM_COMMAND_CHANNEL));
			
			if (activeChar.getParty().isInCommandChannel())
			{
				SystemMessage sm = new SystemMessage(SystemMessage.C1_PARTY_DISMISSED_FROM_COMMAND_CHANNEL);
				sm.addString(target.getParty().getLeader().getName());
				activeChar.getParty().getCommandChannel().broadcastToChannelMembers(sm);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
		}
	}
}
