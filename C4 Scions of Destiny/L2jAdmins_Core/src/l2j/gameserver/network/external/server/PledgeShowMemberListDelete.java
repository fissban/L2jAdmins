package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeShowMemberListDelete extends AServerPacket
{
	private final String player;
	
	public PledgeShowMemberListDelete(String playerName)
	{
		player = playerName;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x56);
		writeS(player);
	}
}
