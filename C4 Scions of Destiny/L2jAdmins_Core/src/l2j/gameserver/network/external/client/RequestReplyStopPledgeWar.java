package l2j.gameserver.network.external.client;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestReplyStopPledgeWar extends AClientPacket
{
	private int answer;
	
	@Override
	protected void readImpl()
	{
		@SuppressWarnings("unused")
		String reqName = readS();
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
		L2PcInstance partner = activeChar.getRequestInvite().getPartner();
		if (partner == null)
		{
			return;
		}
		
		if (answer == 1)
		{
			ClanData.getInstance().deleteClansWars(partner.getClanId(), activeChar.getClanId());
		}
		else
		{
			partner.sendPacket(SystemMessage.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
		}
		
		partner.getRequestInvite().endRequest();
	}
}
