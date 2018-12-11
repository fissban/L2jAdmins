package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Cube extends Square
{
	// cube origin coordinates
	private final int z;
	
	/**
	 * Cube constructor.
	 * @param x : Bottom left lower X coordinate.
	 * @param y : Bottom left lower Y coordinate.
	 * @param z : Bottom left lower Z coordinate.
	 * @param a : Size of cube side.
	 */
	public Cube(int x, int y, int z, int a)
	{
		super(x, y, a);
		
		this.z = z;
	}
	
	@Override
	public double getArea()
	{
		return 6 * a * a;
	}
	
	@Override
	public double getVolume()
	{
		return a * a * a;
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		int d = z - z;
		if ((d < 0) || (d > a))
		{
			return false;
		}
		
		d = x - x;
		if ((d < 0) || (d > a))
		{
			return false;
		}
		
		d = y - y;
		if ((d < 0) || (d > a))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public LocationHolder getRandomLocation()
	{
		// calculate coordinates and return
		return new LocationHolder(x + Rnd.get(a), y + Rnd.get(a), z + Rnd.get(a));
	}
}
