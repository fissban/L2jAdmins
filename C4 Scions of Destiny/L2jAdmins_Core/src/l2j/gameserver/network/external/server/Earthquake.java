package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * format dddddd
 */
public class Earthquake extends AServerPacket
{
	private final int x;
	private final int y;
	private final int z;
	private final int intensity;
	private final int duration;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param intensity
	 * @param duration
	 */
	public Earthquake(int x, int y, int z, int intensity, int duration)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.intensity = intensity;
		this.duration = duration;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xC4);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(intensity);
		writeD(duration);
		writeD(0x00); // Unknown
	}
}
