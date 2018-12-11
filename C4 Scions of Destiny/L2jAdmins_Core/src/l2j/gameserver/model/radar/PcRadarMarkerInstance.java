package l2j.gameserver.model.radar;

/**
 * Simple class to model radar points.
 */
public class PcRadarMarkerInstance
{
	public int type, x, y, z;
	
	public PcRadarMarkerInstance(int type, int x, int y, int z)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public PcRadarMarkerInstance(int x, int y, int z)
	{
		type = 1;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
