package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00 ?*..L........f.. 0010: 00 7c f1 ff ff .|... format dd ddd
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class ChangeWaitType extends AServerPacket
{
	public enum ChangeWait
	{
		WT_SITTING,
		WT_STANDING,
		WT_START_FAKEDEATH,
		WT_STOP_FAKEDEATH,
	}
	
	private final int objectId;
	private final ChangeWait moveType;
	private final int x, y, z;
	
	public ChangeWaitType(L2Character cha, ChangeWait newMoveType)
	{
		objectId = cha.getObjectId();
		moveType = newMoveType;
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x2f);
		writeD(objectId);
		writeD(moveType.ordinal());
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
