package l2j.gameserver.network.external.server;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.network.AServerPacket;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 * @author KenM
 */
public class SiegeAttackerList extends AServerPacket
{
	private final Castle castle;
	
	public SiegeAttackerList(Castle castle)
	{
		this.castle = castle;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xca);
		writeD(castle.getId());
		writeD(0x00); // 0
		writeD(0x01); // 1
		writeD(0x00); // 0
		int size = castle.getSiege().getClansListMngr().getClanList(SiegeClanType.ATTACKER).size();
		if (size > 0)
		{
			Clan clan;
			
			writeD(size);
			writeD(size);
			for (SiegeClanHolder siegeclan : castle.getSiege().getClansListMngr().getClanList(SiegeClanType.ATTACKER))
			{
				clan = ClanData.getInstance().getClanById(siegeclan.getClanId());
				if (clan == null)
				{
					continue;
				}
				
				writeD(clan.getId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00); // signed time (seconds) (not storated by L2J)
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
				writeS(""); // AllyLeaderName
				writeD(clan.getAllyCrestId());
			}
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
	}
}
