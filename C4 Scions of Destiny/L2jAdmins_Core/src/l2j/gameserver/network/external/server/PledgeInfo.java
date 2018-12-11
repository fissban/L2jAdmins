package l2j.gameserver.network.external.server;

import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 9c c10c0000 48 00 61 00 6d 00 62 00 75 00 72 .....H.a.m.b.u.r 0010: 00 67 00 00 00 00000000 00000000 00000000 00000000 00000000 00000000 00 00 00000000 ... format dSddddddSd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgeInfo extends AServerPacket
{
	private final Clan clan;
	
	public PledgeInfo(Clan clan)
	{
		this.clan = clan;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x83);
		writeD(clan.getId());
		writeS(clan.getName());
		writeS(clan.getAllyName());
	}
}
