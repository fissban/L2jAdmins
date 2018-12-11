package l2j.gameserver.network.external.server;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.PartyMatchRoom;
import l2j.gameserver.model.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Gnacik
 */
public class ExManagePartyRoomMember extends AServerPacket
{
	private final L2PcInstance activeChar;
	private final PartyMatchRoom room;
	private final PartyRoomMemberType mode;
	
	public ExManagePartyRoomMember(L2PcInstance player, PartyMatchRoom room, PartyRoomMemberType mode)
	{
		activeChar = player;
		this.room = room;
		this.mode = mode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x10);
		writeD(mode.ordinal());
		writeD(activeChar.getObjectId());
		writeS(activeChar.getName());
		writeD(activeChar.getActiveClass());
		writeD(activeChar.getLevel());
		writeD(MapRegionData.getInstance().getClosestTownNumber(activeChar));
		if (room.getOwner().equals(activeChar))
		{
			writeD(1);
		}
		else
		{
			if ((room.getOwner().isInParty() && activeChar.isInParty()) && (room.getOwner().getParty().getLeader().getObjectId() == activeChar.getParty().getLeader().getObjectId()))
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
