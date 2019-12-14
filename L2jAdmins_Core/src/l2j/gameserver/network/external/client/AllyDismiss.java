package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPenaltyType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

public class AllyDismiss extends AClientPacket
{
	private String clanName;
	
	@Override
	protected void readImpl()
	{
		clanName = readS();
	}
	
	@Override
	public void runImpl()
	{
		if (clanName == null)
		{
			return;
		}
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getClan() == null)
		{
			player.sendPacket(SystemMessage.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		
		Clan leaderClan = player.getClan();
		if (leaderClan.getAllyId() == 0)
		{
			player.sendPacket(SystemMessage.NO_CURRENT_ALLIANCES);
			return;
		}
		
		if (!player.isClanLeader() || (leaderClan.getId() != leaderClan.getAllyId()))
		{
			player.sendPacket(SystemMessage.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return;
		}
		
		Clan clan = ClanData.getInstance().getClanByName(clanName);
		if (clan == null)
		{
			player.sendPacket(SystemMessage.CLAN_DOESNT_EXISTS);
			return;
		}
		
		if (clan.getId() == leaderClan.getId())
		{
			player.sendPacket(SystemMessage.ALLIANCE_LEADER_CANT_WITHDRAW);
			return;
		}
		
		if (clan.getAllyId() != leaderClan.getAllyId())
		{
			player.sendPacket(SystemMessage.DIFFERENT_ALLIANCE);
			return;
		}
		
		leaderClan.setAllyPenaltyExpiryTime(System.currentTimeMillis() + (Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000), ClanPenaltyType.DISMISS_CLAN); // 24*60*60*1000 = 86400000
		leaderClan.updateClanInDB();
		
		clan.setAllyId(0);
		clan.setAllyName(null);
		// clan.setAllyCrestId(0);
		clan.changeAllyCrest(0, true);
		clan.setAllyJoinExpiryTime(System.currentTimeMillis() + (Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000)); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		
		player.sendPacket(SystemMessage.YOU_HAVE_EXPELED_A_CLAN);
	}
}
