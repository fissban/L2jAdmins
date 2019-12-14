package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.actor.manager.pc.party.PartyCommandChannel;
import l2j.gameserver.network.AServerPacket;

/**
 * @author chris_00
 */
public class ExMultiPartyCommandChannelInfo extends AServerPacket
{
	private final PartyCommandChannel channel;
	
	public ExMultiPartyCommandChannelInfo(PartyCommandChannel channel)
	{
		this.channel = channel;
	}
	
	@Override
	public void writeImpl()
	{
		if (channel == null)
		{
			return;
		}
		
		writeC(0xfe);
		writeH(0x30);
		
		writeS(channel.getChannelLeader().getName());
		writeD(channel.getMemberCount());
		
		writeD(channel.getParties().size());
		for (Party p : channel.getParties())
		{
			writeS(p.getLeader().getName());
			writeD(p.getMemberCount());
		}
	}
}
