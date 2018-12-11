package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * format dddd sample 0000: 3a 69 08 10 48 02 c1 00 00 f7 56 00 00 89 ea ff :i..H.....V..... 0010: ff 0c b2 d8 61 ....a
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class TargetUnselected extends AServerPacket
{
	private final L2Character cha;
	
	/**
	 * @param cha
	 */
	public TargetUnselected(L2Character cha)
	{
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x2a);
		writeD(cha.getObjectId());
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
	}
}
