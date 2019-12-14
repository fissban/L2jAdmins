package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeShowInfoUpdate extends AServerPacket
{
	private final Clan clan;
	
	public PledgeShowInfoUpdate(Clan clan)
	{
		this.clan = clan;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x88);
		// sending empty data so client will ask all the info in response ;)
		writeD(clan.getId());
		writeD(clan.getCrestId());
		writeD(clan.getLevel());
		writeD(clan.getCastleId());
		writeD(clan.getClanHallId());
		writeD(0);
		writeD(clan.getLeader().getLevel());
		writeD(clan.getDissolvingExpiryTime() > System.currentTimeMillis() ? 3 : 0);
		writeD(0);
		writeD(clan.getAllyId());
		writeS(clan.getAllyName());
		writeD(clan.getAllyCrestId());
		writeD(clan.isAtWar() ? 0x01 : 0x00);
	}
}
