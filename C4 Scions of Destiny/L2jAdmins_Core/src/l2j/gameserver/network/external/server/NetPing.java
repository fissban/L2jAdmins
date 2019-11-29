package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample
 * <p>
 * 7d c1 b2 e0 4a 00 00 00 00
 * <p>
 * format cdd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class NetPing extends AServerPacket
{
	int objectId;
	
	public NetPing(int objectId)
	{
		this.objectId = objectId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xd3);
		writeD(objectId);
		// falta algo mas para enviar?
	}
}
