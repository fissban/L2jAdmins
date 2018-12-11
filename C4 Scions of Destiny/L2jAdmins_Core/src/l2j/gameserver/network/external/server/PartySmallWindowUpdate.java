package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.5 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartySmallWindowUpdate extends AServerPacket
{
	private final L2PcInstance member;
	
	public PartySmallWindowUpdate(L2PcInstance member)
	{
		this.member = member;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x52);
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
	}
}
