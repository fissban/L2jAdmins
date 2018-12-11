package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Cuboid extends Rectangle
{
	// min and max Z coorinates
	private final int minZ;
	private final int maxZ;
	
	/**
	 * Cuboid constructor.
	 * @param x    : Bottom left lower X coordinate.
	 * @param y    : Bottom left lower Y coordinate.
	 * @param minZ : Minimum Z coordinate.
	 * @param maxZ : Maximum Z coordinate.
	 * @param w    : Cuboid width.
	 * @param h    : Cuboid height.
	 */
	public Cuboid(int x, int y, int minZ, int maxZ, int w, int h)
	{
		super(x, y, w, h);
		
		this.minZ = minZ;
		this.maxZ = maxZ;
	}
	
	@Override
	public final double getArea()
	{
		return 2 * ((w * h) + ((w + h) * (maxZ - minZ)));
	}
	
	@Override
	public final double getVolume()
	{
		return w * h * (maxZ - minZ);
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		if ((z < minZ) || (z > maxZ))
		{
			return false;
		}
		
		int d = x - x;
		if ((d < 0) || (d > w))
		{
			return false;
		}
		
		d = y - y;
		if ((d < 0) || (d > h))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public LocationHolder getRandomLocation()
	{
		// calculate coordinates and return
		return new LocationHolder(x + Rnd.get(w), y + Rnd.get(h), Rnd.get(minZ, maxZ));
	}
}
