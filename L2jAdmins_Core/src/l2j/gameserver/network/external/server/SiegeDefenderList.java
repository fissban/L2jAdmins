package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.network.AServerPacket;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 * @author KenM
 */
public class SiegeDefenderList extends AServerPacket
{
	private final Castle castle;
	
	public SiegeDefenderList(Castle castle)
	{
		this.castle = castle;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xcb);
		writeD(castle.getId());
		writeD(0x00); // 0
		writeD(0x01); // 1
		writeD(0x00); // 0
		
		List<SiegeClanHolder> listClans = castle.getSiege().getClansListMngr().getClanList(SiegeClanType.OWNER, SiegeClanType.DEFENDER_PENDING, SiegeClanType.DEFENDER);
		int size = listClans.size();
		if (size > 0)
		{
			Clan clan;
			
			writeD(size);
			writeD(size);
			// Listing the Lord and the approved clans
			for (SiegeClanHolder siegeclan : listClans)
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
				switch (siegeclan.getSiegeClanType())
				{
					case OWNER:
						writeD(0x01); // owner
						break;
					case DEFENDER_PENDING:
						writeD(0x02); // waiting approved
						break;
					case DEFENDER:
						writeD(0x03); // approved
						break;
					default:
						writeD(0x00);
						break;
				}
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
