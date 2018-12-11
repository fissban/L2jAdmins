package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:40 $
 */
public class SendTradeRequest extends AServerPacket
{
	private final int senderID;
	
	public SendTradeRequest(int senderID)
	{
		this.senderID = senderID;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x5e);
		writeD(senderID);
	}
}
