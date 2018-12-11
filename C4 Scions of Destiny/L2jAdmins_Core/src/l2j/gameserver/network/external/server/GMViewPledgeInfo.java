package l2j.gameserver.network.external.server;

import java.util.Collection;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * format SdSS dddddddd d (Sddddd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class GMViewPledgeInfo extends AServerPacket
{
	private final Clan clan;
	private final L2PcInstance activeChar;
	
	public GMViewPledgeInfo(Clan clan, L2PcInstance activeChar)
	{
		this.clan = clan;
		this.activeChar = activeChar;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x90);
		writeS(activeChar.getName());
		writeD(clan.getId());
		writeS(clan.getName());
		writeS(clan.getLeaderName());
		writeD(clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
		writeD(clan.getLevel());
		writeD(clan.getCastleId());
		writeD(clan.getClanHallId());
		writeD(0);
		writeD(activeChar.getLevel());
		writeD(clan.getDissolvingExpiryTime() > System.currentTimeMillis() ? 3 : 0);
		writeD(0);
		
		writeD(clan.getAllyId()); // c2
		writeS(clan.getAllyName()); // c2
		writeD(clan.getAllyCrestId()); // c2
		writeD(clan.isAtWar() ? 0x01 : 0x00); // c3
		
		Collection<ClanMemberInstance> members = clan.getMembers();
		writeD(members.size());
		
		for (ClanMemberInstance m : members)
		{
			writeS(m.getName());
			writeD(m.getLevel());
			writeD(m.getClassId());
			writeD(0);
			writeD(1);
			writeD(m.isOnline() ? m.getObjectId() : 0);
		}
	}
}
