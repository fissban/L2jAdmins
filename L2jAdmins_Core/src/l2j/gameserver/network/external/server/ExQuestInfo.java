package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: $ $Date: $
 * @author  Luca Baldi
 */
public class ExQuestInfo extends AServerPacket
{
	public static final ExQuestInfo STATIC_PACKET = new ExQuestInfo();
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x19);
	}
}
