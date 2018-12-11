package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestStartPledgeWar extends AClientPacket
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
		
		Clan playerClan = getClient().getActiveChar().getClan();
		if (playerClan == null)
		{
			return;
		}
		
		if ((playerClan.getLevel() < 3) || (playerClan.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!player.hasClanPrivilege(ClanPrivilegesType.CL_CLAN_WAR))
		{
			player.sendMessage("You are not authorized to manage clan wars.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		Clan clan = ClanData.getInstance().getClanByName(pledgeName);
		if ((clan == null) || (clan == playerClan))
		{
			player.sendMessage("Invalid Clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((playerClan.getAllyId() == clan.getAllyId()) && (playerClan.getAllyId() != 0))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((clan.getLevel() < 3) || (clan.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		ClanData.getInstance().storeClansWars(player.getClanId(), clan.getId());
		for (L2PcInstance cha : L2World.getInstance().getAllPlayers())
		{
			if ((cha.getClan() == player.getClan()) || (cha.getClan() == clan))
			{
				cha.broadcastUserInfo();
			}
		}
	}
}
