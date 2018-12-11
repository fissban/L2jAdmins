package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Sphere extends Circle
{
	// sphere center Z coordinate
	private final int z;
	
	/**
	 * Sphere constructor.
	 * @param x : Center X coordinate.
	 * @param y : Center Y coordinate.
	 * @param z : Center Z coordinate.
	 * @param r : Sphere radius.
	 */
	public Sphere(int x, int y, int z, int r)
	{
		super(x, y, r);
		
		this.z = z;
	}
	
	@Override
	public final double getArea()
	{
		return 4 * Math.PI * r * r;
	}
	
	@Override
	public final double getVolume()
	{
		return (4 * Math.PI * r * r * r) / 3;
	}
	
	@Override
	public final boolean isInside(int x, int y, int z)
	{
		final int dx = x - x;
		final int dy = y - y;
		final int dz = z - z;
		
		return ((dx * dx) + (dy * dy) + (dz * dz)) <= (r * r);
	}
	
	@Override
	public final LocationHolder getRandomLocation()
	{
		// get uniform distance and angles
		final double r = Math.cbrt(Rnd.nextDouble()) * this.r;
		final double phi = Rnd.nextDouble() * 2 * Math.PI;
		final double theta = Math.acos((2 * Rnd.nextDouble()) - 1);
		
		// calculate coordinates
		final int x = (int) (this.x + (r * Math.cos(phi) * Math.sin(theta)));
		final int y = (int) (this.y + (r * Math.sin(phi) * Math.sin(theta)));
		final int z = (int) (this.z + (r * Math.cos(theta)));
		
		// return
		return new LocationHolder(x, y, z);
	}
}
