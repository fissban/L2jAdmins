package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class MagicSkillCanceld extends AServerPacket
{
	private final int objectId;
	
	public MagicSkillCanceld(L2Character cha)
	{
		objectId = cha.getObjectId();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x49);
		writeD(objectId);
	}
}
