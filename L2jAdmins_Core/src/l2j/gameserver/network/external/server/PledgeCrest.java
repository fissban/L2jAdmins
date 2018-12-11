package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 84 6d 06 00 00 36 05 00 00 42 4d 36 05 00 00 00 .m...6...BM6.... 0010: 00 00 00 36 04 00 00 28 00 00 00 10 00 00 00 10 ...6...(........ 0020: 00 00 00 01 00 08 00 00 00 00 00 00 01 00 00 c4 ................ 0030: ... 0530: 10 91 00 00 00 60 9b d1 01 e4 6e ee 52 97 dd .....`....n.R..
 * format dd x...x
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgeCrest extends AServerPacket
{
	private final int crestId;
	private byte[] data;
	
	public PledgeCrest(int crestId, byte[] data)
	{
		this.crestId = crestId;
		this.data = data;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x6c);
		writeD(crestId);
		writeD(data.length);
		writeB(data);
		data = null;
	}
}
