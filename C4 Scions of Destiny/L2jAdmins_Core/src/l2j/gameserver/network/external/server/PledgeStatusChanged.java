package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00 ....H........... 0010: 00 00 00 00 00 ..... format ddddd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeStatusChanged extends AServerPacket
{
	private final Clan clan;
	
	public PledgeStatusChanged(Clan clan)
	{
		this.clan = clan;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xcd);
		writeD(clan.getLeaderId());
		writeD(clan.getId());
		writeD(clan.getCrestId());
		writeD(clan.getAllyId());
		writeD(clan.getAllyCrestId());
	}
}
