package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.network.AServerPacket;

/**
 * <p>
 * sample bf 73 5d 30 49 01 00
 * <p>
 * format dh (objectid, color)
 * <p>
 * color -xx -> -9 red
 * <p>
 * -8 -> -6 light-red
 * <p>
 * -5 -> -3 yellow
 * <p>
 * -2 -> 2 white
 * <p>
 * 3 -> 5 green
 * <p>
 * 6 -> 8 light-blue
 * <p>
 * 9 -> xx blue
 * <p>
 * <p>
 * usually the color equals the level difference to the selected target
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class MyTargetSelected extends AServerPacket
{
	private final int objectId;
	private final int color;
	
	public MyTargetSelected(L2Object obj, int color)
	{
		objectId = obj.getObjectId();
		this.color = color;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa6);
		writeD(objectId);
		writeH(color);
	}
}
