package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.PartyMatchRoom;
import l2j.gameserver.model.party.PartyMatchRoomList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExClosePartyRoom;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Gnacik
 */
public class RequestWithdrawPartyRoom extends AClientPacket
{
	private int roomid;
	@SuppressWarnings("unused")
	private int unk1;
	
	@Override
	protected void readImpl()
	{
		roomid = readD();
		unk1 = readD();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(roomid);
		if (room == null)
		{
			return;
		}
		
		if ((activeChar.isInParty() && room.getOwner().isInParty()) && (activeChar.getParty().getLeader().getObjectId() == room.getOwner().getParty().getLeader().getObjectId()))
		{
			return;
		}
		
		room.deleteMember(activeChar);
		activeChar.setPartyRoom(0);
		
		activeChar.sendPacket(new ExClosePartyRoom());
		activeChar.sendPacket(new SystemMessage(SystemMessage.PARTY_ROOM_EXITED));
	}
}
