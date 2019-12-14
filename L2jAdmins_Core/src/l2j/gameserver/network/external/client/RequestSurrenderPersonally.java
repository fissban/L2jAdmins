package l2j.gameserver.network.external.client;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestSurrenderPersonally extends AClientPacket
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
		Clan clan = ClanData.getInstance().getClanByName(pledgeName);
		
		if (playerClan == null)
		{
			return;
		}
		
		if (clan == null)
		{
			player.sendMessage("No such clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!playerClan.isAtWarWith(clan.getId()) || (player.getWantsPeace() == 1))
		{
			player.sendMessage("You aren't at war with this clan.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.setWantsPeace(1);
		player.deathPenalty(false);
		player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN).addString(pledgeName));
		ClanData.getInstance().checkSurrender(playerClan, clan);
	}
}
