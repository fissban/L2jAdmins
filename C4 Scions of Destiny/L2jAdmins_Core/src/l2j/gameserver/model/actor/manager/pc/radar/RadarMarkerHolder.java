package l2j.gameserver.model.actor.manager.pc.radar;

/**
 * Simple class to model radar points.
 */
public class RadarMarkerHolder
{
	public int type, x, y, z;
	
	public RadarMarkerHolder(int type, int x, int y, int z)
	{
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public RadarMarkerHolder(int x, int y, int z)
	{
		type = 1;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
