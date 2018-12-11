package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class RadarControl extends AServerPacket
{
	// TODO es necesario averiguar que funcion cumple cada variable.
	private final int showRadar;
	private final int type;
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * 0xEB RadarControl ddddd
	 * @param showRadar 0 = show radar; 1 = delete radar;
	 * @param type
	 * @param x
	 * @param y
	 * @param z
	 */
	public RadarControl(int showRadar, int type, int x, int y, int z)
	{
		this.showRadar = showRadar;
		this.type = type; // radar type??
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xEB);
		writeD(showRadar);
		writeD(type); // maybe type
		writeD(x); // x
		writeD(y); // y
		writeD(z); // z
	}
}
