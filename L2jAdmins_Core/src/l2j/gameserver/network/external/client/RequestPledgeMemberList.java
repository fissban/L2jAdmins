package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;

/**
 * This class ...
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeMemberList extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan != null)
		{
			activeChar.sendPacket(new PledgeShowMemberListAll(clan, activeChar));
		}
	}
}
