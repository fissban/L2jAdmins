package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

public class AllyLeave extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessage.ONLY_CLAN_LEADER_WITHDRAW_ALLY);
			return;
		}
		
		if (clan.getAllyId() == 0)
		{
			player.sendPacket(SystemMessage.NO_CURRENT_ALLIANCES);
			return;
		}
		
		if (clan.getId() == clan.getAllyId())
		{
			player.sendPacket(SystemMessage.ALLIANCE_LEADER_CANT_WITHDRAW);
			return;
		}
		
		clan.setAllyId(0);
		clan.setAllyName(null);
		// clan.setAllyCrestId(0);
		clan.changeAllyCrest(0, true);
		clan.setAllyJoinExpiryTime(System.currentTimeMillis() + (Config.ALT_ALLY_JOIN_DAYS_WHEN_LEAVED * 86400000)); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		player.sendPacket(SystemMessage.YOU_HAVE_WITHDRAWN_FROM_ALLIANCE);
	}
}
