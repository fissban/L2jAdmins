package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PartySmallWindowDelete extends AServerPacket
{
	private final L2PcInstance member;
	
	public PartySmallWindowDelete(L2PcInstance member)
	{
		this.member = member;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x51);
		writeD(member.getObjectId());
		writeS(member.getName());
	}
}
