package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.network.AClientPacket;

/**
 * @author Gnacik
 */
public class RequestDismissPartyRoom extends AClientPacket
{
	private int roomId;
	
	@Override
	protected void readImpl()
	{
		roomId = readD();
		readD();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(roomId);
		if (room == null)
		{
			return;
		}
		
		PartyMatchRoomList.getInstance().deleteRoom(roomId);
	}
}
