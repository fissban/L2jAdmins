package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.ClanMemberInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PledgeShowMemberListDelete;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestOustPledgeMember extends AClientPacket
{
	private String targetName;
	
	@Override
	protected void readImpl()
	{
		targetName = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		// is player leader of the clan ?
		if ((activeChar == null) || !activeChar.isClanLeader())
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_A_CLAN_MEMBER));
			return;
		}
		
		ClanMemberInstance member = clan.getClanMember(targetName);
		if ((member == null) || (member.getObjectId() == activeChar.getObjectId()))
		{
			return;
		}
		
		if (member.isOnline())
		{
			
			L2PcInstance player = member.getPlayerInstance();
			if (player.isInCombat())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.CLAN_MEMBER_CANNOT_BE_DISMISSED_DURING_COMBAT));
				return;
			}
			
			player.sendPacket(new SystemMessage(SystemMessage.CLAN_MEMBERSHIP_TERMINATED));
		}
		
		// this also updates the database
		clan.removeClanMember(member.getObjectId(), System.currentTimeMillis() + (Config.ALT_CLAN_JOIN_DAYS * 86400000)); // 24*60*60*1000 = 86400000
		clan.setCharPenaltyExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_JOIN_DAYS * 86400000)); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		
		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_EXPELLED).addString(member.getName()));
		activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUCCEEDED_IN_EXPELLING_CLAN_MEMBER));
		
		clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(targetName));
	}
}
