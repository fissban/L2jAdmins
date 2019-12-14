package l2j.gameserver.network.external.client;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExClosePartyRoom;
import l2j.gameserver.network.external.server.PartyMatchList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * format (ch) d
 * @author -Wooden-
 */
public class RequestOustFromPartyRoom extends AClientPacket
{
	private int targetId;
	
	@Override
	protected void readImpl()
	{
		targetId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		L2Object object = L2World.getInstance().getObject(targetId);
		if (object == null)
		{
			return;
		}
		
		if (!(object instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance member = (L2PcInstance) object;
		
		PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(member);
		if (room == null)
		{
			return;
		}
		
		if (room.getOwner() != activeChar)
		{
			return;
		}
		
		if (room.getOwner() == member)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.INCORRECT_TARGET));
			return;
		}
		
		if (activeChar.isInParty() && member.isInParty() && (activeChar.getParty().getLeader().getObjectId() == member.getParty().getLeader().getObjectId()))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF));
		}
		else
		{
			// Remove member from party room
			room.deleteMember(member);
			member.setPartyRoom(0);
			// Close the PartyRoom window
			member.sendPacket(new ExClosePartyRoom());
			// Send Room list
			int loc = MapRegionData.getInstance().getClosestTownNumber(member);
			member.sendPacket(new PartyMatchList(member, 0, loc, member.getLevel()));
			// Clean Looking for Party title
			member.broadcastUserInfo();
			member.sendPacket(new SystemMessage(SystemMessage.OUSTED_FROM_PARTY_ROOM));
		}
	}
}
