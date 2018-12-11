package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * Format: (ch) ddd b d: ? d: crest ID d: crest size b: raw data
 * @author -Wooden-
 */
public class ExPledgeCrestLarge extends AServerPacket
{
	private final int crestId;
	private final byte[] data;
	
	public ExPledgeCrestLarge(int crestId, byte[] data)
	{
		this.crestId = crestId;
		this.data = data;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x28);
		
		writeD(0x00); // ???
		writeD(crestId);
		writeD(data.length);
		writeB(data);
	}
}
