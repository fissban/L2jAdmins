package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class EnchantResult extends AServerPacket
{
	private final int result;
	
	public EnchantResult(int result)
	{
		this.result = result;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x81);
		writeD(result);
	}
}
