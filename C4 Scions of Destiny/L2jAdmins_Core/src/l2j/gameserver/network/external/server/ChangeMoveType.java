package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 3e 2a 89 00 4c 01 00 00 00 .|... format dd
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class ChangeMoveType extends AServerPacket
{
	private final int chaId;
	private final boolean running;
	
	public ChangeMoveType(L2Character cha)
	{
		chaId = cha.getObjectId();
		running = cha.isRunning();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x2e);
		writeD(chaId);
		writeD(running ? 1 : 0);
		writeD(0); // c2
	}
}
