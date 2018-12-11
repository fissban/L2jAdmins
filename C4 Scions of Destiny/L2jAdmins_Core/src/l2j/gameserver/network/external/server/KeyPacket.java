package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class KeyPacket extends AServerPacket
{
	private final byte[] key;
	
	public KeyPacket(byte[] key)
	{
		this.key = key;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x00);
		writeC(0x01); // isProtocolOk
		writeB(key);
		// writeD(0x01);
		// writeD(0x01);
	}
}
