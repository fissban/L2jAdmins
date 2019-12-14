package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPenaltyType;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * sample 5F 01 00 00 00 format cdd
 * @version $Revision: 1.7.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerJoinAlly extends AClientPacket
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
		
		if (response == 0)
		{
			activeChar.sendPacket(SystemMessage.YOU_DID_NOT_RESPOND_TO_ALLY_INVITATION);
			requestor.sendPacket(SystemMessage.NO_RESPONSE_TO_ALLY_INVITATION);
		}
		else
		{
			if (!(requestor.getRequestInvite().isRequestPacket(RequestPacketType.JOIN_ALLY)))
			{
				return;
			}
			
			Clan clan = requestor.getClan();
			
			if (clan.checkAllyJoinCondition(requestor, activeChar))
			{
				requestor.sendMessage("You have succed invitting alliance");
				activeChar.sendPacket(SystemMessage.YOU_ACCEPTED_ALLIANCE);
				
				activeChar.getClan().setAllyId(clan.getAllyId());
				activeChar.getClan().setAllyName(clan.getAllyName());
				activeChar.getClan().setAllyPenaltyExpiryTime(0, ClanPenaltyType.NOTHING);
				// activeChar.getClan().setAllyCrestId(clan.getAllyCrestId());
				activeChar.getClan().changeAllyCrest(clan.getAllyCrestId(), true);
				activeChar.getClan().updateClanInDB();
			}
		}
		
		activeChar.getRequestInvite().endRequest();
	}
}
