package l2j.gameserver.network.external.server;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.network.AServerPacket;

public class AllyInfo extends AServerPacket
{
	private L2PcInstance cha;
	
	public AllyInfo(L2PcInstance cha)
	{
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.getAllyId() == 0)
		{
			cha.sendPacket(SystemMessage.NO_CURRENT_ALLIANCES);
			return;
		}
		
		// ======<AllyInfo>======
		cha.sendPacket(SystemMessage.ALLIANCE_INFO_HEAD);
		
		// ======<Ally Name>======
		SystemMessage sm = new SystemMessage(SystemMessage.ALLIANCE_NAME_S1);
		sm.addString(cha.getClan().getAllyName());
		cha.sendPacket(sm);
		
		int online = 0;
		int count = 0;
		int clancount = 0;
		
		for (Clan clan : ClanData.getInstance().getClans())
		{
			if (clan.getAllyId() == cha.getAllyId())
			{
				clancount++;
				online += clan.getOnlineMembers().size();
				count += clan.getMembers().size();
			}
		}
		
		// Connection
		cha.sendPacket(new SystemMessage(SystemMessage.CONNECTION_S1_TOTAL_S2).addString("" + online).addString("" + count));
		
		Clan leaderclan = ClanData.getInstance().getClanById(cha.getAllyId());
		
		cha.sendPacket(new SystemMessage(SystemMessage.ALLIANCE_LEADER_S2_OF_S1).addString(leaderclan.getName()).addString(leaderclan.getLeaderName()));
		
		// clan count
		cha.sendPacket(new SystemMessage(SystemMessage.ALLIANCE_CLAN_TOTAL_S1).addString("" + clancount));
		// clan information
		cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_HEAD));
		
		for (Clan clan : ClanData.getInstance().getClans())
		{
			if (clan.getAllyId() == cha.getAllyId())
			{
				// clan name
				cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_NAME_S1).addString(clan.getName()));
				// clan leader name
				cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_LEADER_S1).addString(clan.getLeaderName()));
				// clan level
				cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_LEVEL_S1).addNumber(clan.getLevel()));
				// ---------
				cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_SEPARATOR));
			}
		}
		
		// =========================
		cha.sendPacket(new SystemMessage(SystemMessage.CLAN_INFO_FOOT));
	}
}
