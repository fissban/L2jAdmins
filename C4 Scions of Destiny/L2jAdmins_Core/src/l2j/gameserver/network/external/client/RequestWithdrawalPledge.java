package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PledgeShowMemberListDelete;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestWithdrawalPledge extends AClientPacket
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
		
		// is player in a clan?
		if (activeChar == null)
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_A_CLAN_MEMBER));
			return;
		}
		
		if (activeChar.isClanLeader())
		{
			return;
		}
		
		if (activeChar.isInCombat())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_LEAVE_DURING_COMBAT));
			return;
		}
		
		// this also updates the database
		clan.removeClanMember(activeChar.getObjectId(), System.currentTimeMillis() + (Config.ALT_CLAN_JOIN_DAYS * 86400000L)); // 24*60*60*1000 = 86400000
		SystemMessage sm = new SystemMessage(SystemMessage.S1_HAS_WITHDRAWN_FROM_THE_CLAN);
		sm.addString(activeChar.getName());
		clan.broadcastToOnlineMembers(sm);
		sm = null;
		// Remove player from memberlist
		clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(activeChar.getName()));
		
		activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_WITHDRAWN_FROM_CLAN));
		activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN));
	}
}
