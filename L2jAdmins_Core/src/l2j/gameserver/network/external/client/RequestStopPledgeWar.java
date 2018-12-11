package l2j.gameserver.network.external.client;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;

public class RequestStopPledgeWar extends AClientPacket
{
	private String pledgeName;
	
	@Override
	protected void readImpl()
	{
		pledgeName = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		Clan playerClan = player.getClan();
		if (playerClan == null)
		{
			return;
		}
		
		Clan clan = ClanData.getInstance().getClanByName(pledgeName);
		
		if (clan == null)
		{
			player.sendMessage("Clan does not exist.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!player.hasClanPrivilege(ClanPrivilegesType.CL_CLAN_WAR))
		{
			player.sendMessage("You are not authorized to manage clan wars.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!playerClan.isAtWarWith(clan.getId()))
		{
			player.sendMessage("You aren't at war with this clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		ClanData.getInstance().deleteClansWars(playerClan.getId(), clan.getId());
		for (L2PcInstance cha : L2World.getInstance().getAllPlayers())
		{
			if ((cha.getClan() == player.getClan()) || (cha.getClan() == clan))
			{
				cha.broadcastUserInfo();
			}
		}
	}
}
