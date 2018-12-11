package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class StopRotation extends AServerPacket
{
	private final int charId;
	private final int degree;
	private final int speed;
	
	public StopRotation(L2Character cha, int degree, int speed)
	{
		charId = cha.getObjectId();
		this.degree = degree;
		this.speed = speed;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x63);
		writeD(charId);
		writeD(degree);
		writeD(speed);
		writeC(0); // ?
	}
}
