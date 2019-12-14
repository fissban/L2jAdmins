package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.ClanMemberInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 68 b1010000 48 00 61 00 6d 00 62 00 75 00 72 00 67 00 00 00 H.a.m.b.u.r.g... 43 00 61 00 6c 00 61 00 64 00 6f 00 6e 00 00 00 C.a.l.a.d.o.n... 00000000 crestid | not used (nuocnam) 00000000 00000000 00000000 00000000 22000000 00000000 00000000 00000000 ally id 00 00 ally name 00000000
 * ally crrest id 02000000 6c 00 69 00 74 00 68 00 69 00 75 00 6d 00 31 00 00 00 l.i.t.h.i.u.m... 0d000000 level 12000000 class id 00000000 01000000 offline 1=true 00000000 45 00 6c 00 61 00 6e 00 61 00 00 00 E.l.a.n.a... 08000000 19000000 01000000 01000000 00000000 format dSS dddddddddSd d (Sddddd)
 * @version $Revision: 1.6.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgeShowMemberListAll extends AServerPacket
{
	private final Clan clan;
	private final L2PcInstance activeChar;
	
	public PledgeShowMemberListAll(Clan clan, L2PcInstance activeChar)
	{
		this.clan = clan;
		this.activeChar = activeChar;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x53);
		writeD(clan.getId());
		writeS(clan.getName());
		writeS(clan.getLeaderName());
		writeD(clan.getCrestId()); // crest id .. is used again
		writeD(clan.getLevel());
		writeD(clan.getCastleId());
		writeD(clan.getClanHallId());
		writeD(0);
		writeD(activeChar.getLevel());
		writeD(clan.getDissolvingExpiryTime() > System.currentTimeMillis() ? 3 : 0);
		writeD(0);
		
		writeD(clan.getAllyId());
		writeS(clan.getAllyName());
		writeD(clan.getAllyCrestId());
		
		writeD(clan.isAtWar() ? 0x01 : 0x00);// new c3
		
		writeD(clan.getMembers().size());
		for (ClanMemberInstance m : clan.getMembers())
		{
			// if (m.getObjectId() == activeChar.getObjectId())
			// {
			// continue;
			// }
			
			writeS(m.getName());
			writeD(m.getLevel());
			writeD(m.getClassId());
			writeD(0);
			writeD(1);
			writeD(m.isOnline() ? m.getObjectId() : 0); // 1=online 0=offline
		}
	}
}
