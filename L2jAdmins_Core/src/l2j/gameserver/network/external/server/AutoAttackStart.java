package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AutoAttackStart extends AServerPacket
{
	private final int targetId;
	
	/**
	 * @param cha
	 */
	public AutoAttackStart(L2Character cha)
	{
		targetId = cha.getObjectId();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x2b);
		writeD(targetId);
	}
}
