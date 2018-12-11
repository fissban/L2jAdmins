package l2j.gameserver.network.external.server;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.PartyMatchRoom;
import l2j.gameserver.model.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Gnacik
 */
public class ExPartyRoomMember extends AServerPacket
{
	private final PartyMatchRoom room;
	private final PartyRoomMemberType mode;
	
	public ExPartyRoomMember(L2PcInstance player, PartyMatchRoom room, PartyRoomMemberType mode)
	{
		this.room = room;
		this.mode = mode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x0e);
		writeD(mode.ordinal());
		writeD(room.getMembersCount());
		for (L2PcInstance member : room.getMembers())
		{
			writeD(member.getObjectId());
			writeS(member.getName());
			writeD(member.getActiveClass());
			writeD(member.getLevel());
			writeD(MapRegionData.getInstance().getClosestTownNumber(member));
			if (room.getOwner().equals(member))
			{
				writeD(1);
			}
			else
			{
				if ((room.getOwner().isInParty() && member.isInParty()) && (room.getOwner().getParty().getLeader().getObjectId() == member.getParty().getLeader().getObjectId()))
				{
					writeD(2);
				}
				else
				{
					writeD(0);
				}
			}
		}
	}
}
