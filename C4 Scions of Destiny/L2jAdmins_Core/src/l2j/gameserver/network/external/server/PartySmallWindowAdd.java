package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.actor.manager.pc.party.enums.PartyItemDitributionType;
import l2j.gameserver.network.AServerPacket;

public class PartySmallWindowAdd extends AServerPacket
{
	private final L2PcInstance member;
	private final int leaderId;
	private final PartyItemDitributionType distribution;
	
	public PartySmallWindowAdd(L2PcInstance member, Party party)
	{
		this.member = member;
		leaderId = party.getLeader().getObjectId();
		distribution = party.getLootDistribution();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x4f);
		writeD(leaderId); // c3
		writeD(distribution.ordinal()); // c3
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
		writeD(0x00);// writeD(0x01); ??
		writeD(0x00);
	}
}
