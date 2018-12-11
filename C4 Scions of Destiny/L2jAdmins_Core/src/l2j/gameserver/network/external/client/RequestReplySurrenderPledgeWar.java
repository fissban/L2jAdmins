package l2j.gameserver.network.external.client;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;

public class RequestReplySurrenderPledgeWar extends AClientPacket
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
			partner.deathPenalty(false);
			ClanData.getInstance().deleteClansWars(partner.getClanId(), activeChar.getClanId());
		}
		
		partner.getRequestInvite().endRequest();
	}
}
