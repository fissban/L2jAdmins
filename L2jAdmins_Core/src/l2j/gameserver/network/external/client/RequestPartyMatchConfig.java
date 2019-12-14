package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ExPartyRoomMember;
import l2j.gameserver.network.external.server.PartyMatchDetail;
import l2j.gameserver.network.external.server.PartyMatchList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPartyMatchConfig extends AClientPacket
{
	private int auto, loc, lvl;
	
	@Override
	protected void readImpl()
	{
		auto = readD();
		loc = readD(); // Location
		lvl = readD(); // my level
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!activeChar.isInPartyMatchRoom() && (activeChar.getParty() != null) && (activeChar.getParty().getLeader() != activeChar))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANT_VIEW_PARTY_ROOMS));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isInPartyMatchRoom())
		{
			// If Player is in Room show him room, not list
			PartyMatchRoomList list = PartyMatchRoomList.getInstance();
			if (list == null)
			{
				return;
			}
			
			PartyMatchRoom room = list.getPlayerRoom(activeChar);
			if (room == null)
			{
				return;
			}
			
			sendPacket(new PartyMatchDetail(room));
			
			if (activeChar == room.getOwner())
			{
				sendPacket(new ExPartyRoomMember(activeChar, room, PartyRoomMemberType.MODIFY));
			}
			else
			{
				activeChar.sendPacket(new ExPartyRoomMember(activeChar, room, PartyRoomMemberType.QUIT));
			}
			
			activeChar.setPartyRoom(room.getId());
			activeChar.broadcastUserInfo();
		}
		else
		{
			// Send Room list
			activeChar.sendPacket(new PartyMatchList(activeChar, auto, loc, lvl));
		}
	}
}
