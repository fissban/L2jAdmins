package l2j.gameserver.network.external.server;

import java.util.Calendar;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.network.AServerPacket;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related
 * @author KenM
 */
public class SiegeInfo extends AServerPacket
{
	private final Castle castle;
	
	public SiegeInfo(Castle castle)
	{
		this.castle = castle;
	}
	
	@Override
	public void writeImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		writeC(0xc9);
		writeD(castle.getId());
		writeD(((castle.getOwnerId() == activeChar.getClanId()) && (activeChar.isClanLeader())) ? 0x07 : 0x00);
		writeD(castle.getOwnerId());
		if (castle.getOwnerId() > 0)
		{
			Clan owner = ClanData.getInstance().getClanById(castle.getOwnerId());
			if (owner != null)
			{
				writeS(owner.getName()); // Clan Name
				writeS(owner.getLeaderName()); // Clan Leader Name
				writeD(owner.getAllyId()); // Ally ID
				writeS(owner.getAllyName()); // Ally Name
			}
			else
			{
				LOG.warning("Null owner for castle: " + castle.getName());
			}
		}
		else
		{
			writeS("NPC"); // Clan Name
			writeS(""); // Clan Leader Name
			writeD(0); // Ally ID
			writeS(""); // Ally Name
		}
		
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD((int) (castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		writeD(0x00); // number of choices?
	}
}
