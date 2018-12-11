package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 85 00 00 00 00 f0 1a 00 00
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SetupGauge extends AServerPacket
{
	public enum SetupGaugeType
	{
		BLUE,
		RED,
		CYAN,
		GREEN
	}
	
	private final SetupGaugeType color;
	private final int time;
	private final int time2;
	
	public SetupGauge(SetupGaugeType color, int time)
	{
		this.color = color;
		this.time = time;
		time2 = time;
	}
	
	public SetupGauge(SetupGaugeType color, int currentTime, int maxTime)
	{
		this.color = color;
		time = currentTime;
		time2 = maxTime;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x6d);
		writeD(color.ordinal());
		writeD(time);
		writeD(time2);
	}
}
