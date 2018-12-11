package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Cylinder extends Circle
{
	// min and max Z coorinates
	private final int minZ;
	private final int maxZ;
	
	/**
	 * Cylinder constructor
	 * @param x    : Center X coordinate.
	 * @param y    : Center X coordinate.
	 * @param r    : Cylinder radius.
	 * @param minZ : Minimum Z coordinate.
	 * @param maxZ : Maximum Z coordinate.
	 */
	public Cylinder(int x, int y, int r, int minZ, int maxZ)
	{
		super(x, y, r);
		
		this.minZ = minZ;
		this.maxZ = maxZ;
	}
	
	@Override
	public final double getArea()
	{
		return 2 * Math.PI * r * ((r + maxZ) - minZ);
	}
	
	@Override
	public final double getVolume()
	{
		return Math.PI * r * r * (maxZ - minZ);
	}
	
	@Override
	public final boolean isInside(int x, int y, int z)
	{
		if ((z < minZ) || (z > maxZ))
		{
			return false;
		}
		
		final int dx = x - x;
		final int dy = y - y;
		
		return ((dx * dx) + (dy * dy)) <= (r * r);
	}
	
	@Override
	public final LocationHolder getRandomLocation()
	{
		// get uniform distance and angle
		final double distance = Math.sqrt(Rnd.nextDouble()) * r;
		final double angle = Rnd.nextDouble() * Math.PI * 2;
		
		// calculate coordinates and return
		return new LocationHolder((int) (distance * Math.cos(angle)), (int) (distance * Math.sin(angle)), Rnd.get(minZ, maxZ));
	}
}
