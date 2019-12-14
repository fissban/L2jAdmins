package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyRoomMemberType;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExClosePartyRoom;
import l2j.gameserver.network.external.server.ExManagePartyRoomMember;
import l2j.gameserver.network.external.server.ExPartyRoomMember;
import l2j.gameserver.network.external.server.JoinParty;
import l2j.gameserver.network.external.server.PartyMatchDetail;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * sample 2a 01 00 00 00 format cdd
 * @version $Revision: 1.7.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerJoinParty extends AClientPacket
{
	private int response;
	
	@Override
	protected void readImpl()
	{
		response = readD();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2PcInstance partner = player.getRequestInvite().getPartner();
		if (partner == null)
		{
			return;
		}
		
		if (response == 1)
		{
			// summary of ppl already in party and ppl that get invitation
			if (partner.isInParty() && !partner.getParty().isLeader(partner))
			{
				partner.sendPacket(SystemMessage.ONLY_LEADER_CAN_INVITE);
			}
			else if (partner.isInParty() && (partner.getParty().getMemberCount() >= 9))
			{
				partner.sendPacket(SystemMessage.PARTY_FULL);
				player.sendPacket(SystemMessage.PARTY_FULL);
			}
			else if (partner.isInParty() && partner.getParty().isInDimensionalRift())
			{
				partner.sendMessage("You cannot invite characters from another dimension.");
			}
			else if (player.isInJail() || partner.isInJail())
			{
				partner.sendMessage("Player is jailed.");
			}
			else if (partner.getPrivateStore().inOfflineMode())
			{
				player.sendMessage("Requestor is in Offline mode.");
			}
			else if (player.inObserverMode() || partner.inObserverMode())
			{
				player.sendMessage("A Party request cannot be done while one of the partners is in Observer mode.");
			}
			else if (player.isInOlympiadMode() || partner.isInOlympiadMode())
			{
				player.sendMessage("A Party request cannot be done while one of the partners is in Olympiad mode.");
			}
			else if (player.isInParty())
			{
				partner.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_IN_PARTY).addString(player.getName()));
			}
			else
			{
				if (!(partner.getRequestInvite().isRequestPacket(RequestPacketType.JOIN_PARTY)))
				{
					return;
				}
				
				if (!partner.isInParty())
				{
					partner.setParty(new Party(partner));
				}
				
				player.joinParty(partner.getParty());
				// Check everything in detail
				checkPartyMatchingConditions(partner, player);
			}
		}
		else
		{
			partner.sendPacket(new SystemMessage(SystemMessage.PLAYER_DECLINED));
		}
		
		partner.sendPacket(new JoinParty(response));
		
		partner.getRequestInvite().endRequest();
	}
	
	private void checkPartyMatchingConditions(L2PcInstance requestor, L2PcInstance player)
	{
		if (requestor.isInPartyMatchRoom())
		{
			final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
			if (list != null)
			{
				final PartyMatchRoom room = list.getPlayerRoom(requestor);
				final PartyMatchRoom targetRoom = list.getPlayerRoom(player);
				if (player.isInPartyMatchRoom())
				{
					if (room.getId() != targetRoom.getId())
					{
						requestor.sendPacket(new ExClosePartyRoom());
						room.deleteMember(requestor);
						requestor.setPartyRoom(0);
						requestor.broadcastUserInfo();
						
						player.sendPacket(new ExClosePartyRoom());
						targetRoom.deleteMember(player);
						player.setPartyRoom(0);
					}
					else if (requestor != room.getOwner())
					{
						requestor.sendPacket(new ExClosePartyRoom());
						room.deleteMember(requestor);
						requestor.setPartyRoom(0);
						requestor.broadcastUserInfo();
						
						player.sendPacket(new ExClosePartyRoom());
						room.deleteMember(player);
						player.setPartyRoom(0);
					}
					else
					{
						for (final L2PcInstance member : room.getMembers())
						{
							member.sendPacket(new ExManagePartyRoomMember(player, room, PartyRoomMemberType.MODIFY));
						}
					}
					player.broadcastUserInfo();
				}
				else
				{
					if (requestor != room.getOwner())
					{
						requestor.sendPacket(new ExClosePartyRoom());
						room.deleteMember(requestor);
						requestor.setPartyRoom(0);
						requestor.broadcastUserInfo();
					}
					else
					{
						room.addMember(player);
						player.setPartyRoom(room.getId());
						
						player.sendPacket(new PartyMatchDetail(room));
						player.sendPacket(new ExPartyRoomMember(player, room, PartyRoomMemberType.ADD));
						
						player.broadcastUserInfo();
						
						for (final L2PcInstance member : room.getMembers())
						{
							member.sendPacket(new ExManagePartyRoomMember(player, room, PartyRoomMemberType.ADD));
						}
					}
				}
			}
		}
		else
		{
			final PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
			if (room != null)
			{
				player.sendPacket(new ExClosePartyRoom());
				room.deleteMember(player);
				player.setPartyRoom(0);
				player.broadcastUserInfo();
			}
		}
	}
}
