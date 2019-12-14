package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.JoinPledge;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;
import l2j.gameserver.network.external.server.PledgeShowMemberListAdd;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerJoinPledge extends AClientPacket
{
	private int answer;
	
	@Override
	protected void readImpl()
	{
		answer = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance requestor = activeChar.getRequestInvite().getPartner();
		
		if (requestor == null)
		{
			return;
		}
		
		if (answer == 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_DID_NOT_RESPOND_TO_S1_CLAN_INVITATION).addString(requestor.getName()));
			requestor.sendPacket(new SystemMessage(SystemMessage.S1_DID_NOT_RESPOND_TO_CLAN_INVITATION).addString(activeChar.getName()));
		}
		else
		{
			if (!(requestor.getRequestInvite().isRequestPacket(RequestPacketType.JOIN_PLEDGE)))
			{
				return;
			}
			
			Clan clan = requestor.getClan();
			if (!clan.checkClanJoinCondition(requestor, activeChar))
			{
				return;
			}
			
			activeChar.sendPacket(new JoinPledge(requestor.getClanId()));
			
			// this also updates the database
			clan.addClanMember(activeChar);
			
			activeChar.setClan(clan);
			
			activeChar.sendPacket(SystemMessage.ENTERED_THE_CLAN);
			clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.S1_HAS_JOINED_CLAN).addString(activeChar.getName()));
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
			
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
			
			// this activates the clan tab for the new member
			activeChar.sendPacket(new PledgeShowMemberListAll(clan, activeChar));
			activeChar.setClanJoinExpiryTime(0);
			
			activeChar.broadcastUserInfo();
			
		}
		
		activeChar.getRequestInvite().endRequest();
	}
}
