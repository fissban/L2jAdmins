package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.PartyMatchRoom;
import l2j.gameserver.model.party.PartyMatchRoomList;
import l2j.gameserver.model.party.enums.PartyRoomMemberType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExPartyRoomMember;
import l2j.gameserver.network.external.server.PartyMatchDetail;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * cdddddS
 * @version $Revision: 1.1.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPartyMatchList extends AClientPacket
{
	private int roomId;
	private int membersMax;
	private int lvlmin;
	private int lvlmax;
	private int loot;
	private String roomTitle;
	
	@Override
	protected void readImpl()
	{
		roomId = readD();
		membersMax = readD();
		lvlmin = readD();
		lvlmax = readD();
		loot = readD();
		roomTitle = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((activeChar.getParty() != null) && (activeChar.getParty().getLeader() != activeChar))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANT_ENTER_PARTY_ROOM));
			return;
		}
		
		if (roomId > 0)
		{
			PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(roomId);
			if (room != null)
			{
				if (Config.DEBUG)
				{
					LOG.info("PartyMatchRoom #" + room.getId() + " changed by " + activeChar.getName());
				}
				
				room.setMaxMembers(membersMax);
				room.setMinLvl(lvlmin);
				room.setMaxLvl(lvlmax);
				room.setLootType(loot);
				room.setTitle(roomTitle);
				
				for (L2PcInstance member : room.getMembers())
				{
					if (member == null)
					{
						continue;
					}
					
					member.sendPacket(new PartyMatchDetail(room));
					member.sendPacket(new SystemMessage(SystemMessage.PARTY_ROOM_REVISED));
				}
			}
		}
		else
		{
			int newId = PartyMatchRoomList.getInstance().getAutoIncrementId();
			PartyMatchRoom room = new PartyMatchRoom(newId, roomTitle, loot, lvlmin, lvlmax, membersMax, activeChar);
			
			if (Config.DEBUG)
			{
				LOG.info("PartyMatchRoom #" + newId + " created by " + activeChar.getName());
			}
			
			PartyMatchRoomList.getInstance().addPartyMatchRoom(room);
			
			if (activeChar.isInParty())
			{
				for (L2PcInstance ptmember : activeChar.getParty().getMembers())
				{
					if (ptmember == null)
					{
						continue;
					}
					
					if (ptmember == activeChar)
					{
						continue;
					}
					
					ptmember.setPartyRoom(newId);
					
					room.addMember(ptmember);
				}
			}
			
			activeChar.sendPacket(new PartyMatchDetail(room));
			activeChar.sendPacket(new ExPartyRoomMember(activeChar, room, PartyRoomMemberType.MODIFY));
			
			activeChar.sendPacket(SystemMessage.PARTY_ROOM_CREATED);
			
			activeChar.setPartyRoom(newId);
			activeChar.broadcastUserInfo();
		}
	}
}
