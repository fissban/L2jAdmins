package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.party.enums.PartyItemDitributionType;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 63 01 00 00 00 count c1 b2 e0 4a object id 54 00 75 00 65 00 73 00 64 00 61 00 79 00 00 00 name 5a 01 00 00 hp 5a 01 00 00 hp max 89 00 00 00 mp 89 00 00 00 mp max 0e 00 00 00 level 12 00 00 00 class 00 00 00 00 01 00 00 00 format d (dSdddddddd)
 * @version $Revision: 1.6.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $
 */
public class PartySmallWindowAll extends AServerPacket
{
	private final Party party;
	private final L2PcInstance exclude;
	PartyItemDitributionType dist;
	private final int leaderObjId;
	
	public PartySmallWindowAll(L2PcInstance exclude, Party party)
	{
		this.exclude = exclude;
		this.party = party;
		leaderObjId = party.getLeader().getObjectId();
		dist = party.getLootDistribution();
	}
	
	@Override
	public final void writeImpl()
	{
		writeC(0x4e);
		writeD(leaderObjId);
		writeD(dist.ordinal());
		writeD(party.getMemberCount() - 1);
		
		for (L2PcInstance member : party.getMembers())
		{
			if ((member != null) && (member != exclude))
			{
				writeD(member.getObjectId());
				writeS(member.getName());
				
				writeD((int) member.getCurrentCp()); // c4
				writeD(member.getStat().getMaxCp()); // c4
				
				writeD((int) member.getCurrentHp());
				writeD(member.getStat().getMaxHp());
				writeD((int) member.getCurrentMp());
				writeD(member.getStat().getMaxMp());
				writeD(member.getLevel());
				writeD(member.getClassId().getId());
				writeD(0);// writeD(0x01); ??
				writeD(member.getRace().ordinal());
			}
		}
	}
}
