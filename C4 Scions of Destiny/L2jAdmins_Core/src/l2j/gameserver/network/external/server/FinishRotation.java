package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * format dd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class FinishRotation extends AServerPacket
{
	private final L2Character cha;
	
	public FinishRotation(L2Character cha)
	{
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x63);
		writeD(cha.getObjectId());
		writeD(cha.getHeading());
	}
}
