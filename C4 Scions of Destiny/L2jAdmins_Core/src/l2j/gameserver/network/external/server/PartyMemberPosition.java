package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.network.AServerPacket;

/**
 * @author zabbix
 */
public class PartyMemberPosition extends AServerPacket
{
	private final Party party;
	
	public PartyMemberPosition(L2PcInstance actor)
	{
		party = actor.getParty();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa7);
		writeD(party.getMemberCount());
		
		for (L2PcInstance member : party.getMembers())
		{
			if (member == null)
			{
				continue;
			}
			
			writeD(member.getObjectId());
			writeD(member.getX());
			writeD(member.getY());
			writeD(member.getZ());
		}
	}
}
