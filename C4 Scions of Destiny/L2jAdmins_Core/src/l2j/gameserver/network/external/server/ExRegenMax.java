package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author fissban
 */
public final class ExRegenMax extends AServerPacket
{
	private final int count;
	private final int time;
	private final double hpRegen;
	
	public ExRegenMax(int count, int time, double hpRegen)
	{
		this.count = count;
		this.time = time;
		this.hpRegen = hpRegen * 0.66;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x01);
		writeD(1);
		writeD(count);
		writeD(time);
		writeF(hpRegen);
	}
}