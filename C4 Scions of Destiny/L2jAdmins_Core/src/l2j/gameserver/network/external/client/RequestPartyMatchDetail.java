package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExManagePartyRoomMember;
import l2j.gameserver.network.external.server.ExPartyRoomMember;
import l2j.gameserver.network.external.server.PartyMatchDetail;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @athor Gnacik
 */
public class RequestPartyMatchDetail extends AClientPacket
{
	private int roomId;
	@SuppressWarnings("unused")
	private int unk1;
	@SuppressWarnings("unused")
	private int unk2;
	@SuppressWarnings("unused")
	private int unk3;
	
	@Override
	protected void readImpl()
	{
		roomId = readD();
		unk1 = readD();
		unk2 = readD();
		unk3 = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(roomId);
		if (room == null)
		{
			return;
		}
		
		if ((activeChar.getParty() == null) && (activeChar.getLevel() >= room.getMinLvl()) && (activeChar.getLevel() <= room.getMaxLvl()) && (room.getMembers().size() < room.getMaxMembers()))
		{
			room.addMember(activeChar);
			activeChar.setPartyRoom(roomId);
			
			activeChar.sendPacket(new PartyMatchDetail(room));
			activeChar.sendPacket(new ExPartyRoomMember(activeChar, room, PartyRoomMemberType.ADD));
			
			for (L2PcInstance member : room.getMembers())
			{
				if ((member == null) || (member == activeChar))
				{
					continue;
				}
				
				member.sendPacket(new ExManagePartyRoomMember(activeChar, room, PartyRoomMemberType.ADD));
			}
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANT_ENTER_PARTY_ROOM));
		}
	}
}
