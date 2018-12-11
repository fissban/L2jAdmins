package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class SendTradeDone extends AServerPacket
{
	public enum SendTradeType
	{
		CANCELED,
		SUCCESSFUL,
	}
	
	private final SendTradeType type;
	
	public SendTradeDone(SendTradeType type)
	{
		this.type = type;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x22);
		writeD(type.ordinal());
	}
}
