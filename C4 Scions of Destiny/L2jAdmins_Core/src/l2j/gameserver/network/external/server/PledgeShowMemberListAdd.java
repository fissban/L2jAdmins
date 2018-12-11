package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeShowMemberListAdd extends AServerPacket
{
	private final L2PcInstance player;
	
	public PledgeShowMemberListAdd(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x55);
		writeS(player.getName());
		writeD(player.getLevel());
		writeD(player.getClassId().getId());
		writeD(0);
		writeD(1);
		writeD(((player.isOnline()) && !player.getPrivateStore().inOfflineMode() ? player.getObjectId() : 0)); // 1=online 0=offline
	}
}
