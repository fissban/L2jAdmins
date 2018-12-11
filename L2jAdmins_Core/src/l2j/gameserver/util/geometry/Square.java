package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Square extends AShape
{
	// square origin coordinates
	protected final int x;
	protected final int y;
	
	// square side
	protected final int a;
	
	/**
	 * Square constructor.
	 * @param x : Bottom left X coordinate.
	 * @param y : Bottom left Y coordinate.
	 * @param a : Size of square side.
	 */
	public Square(int x, int y, int a)
	{
		this.x = x;
		this.y = y;
		
		this.a = a;
	}
	
	@Override
	public final int getSize()
	{
		return a * a;
	}
	
	@Override
	public double getArea()
	{
		return a * a;
	}
	
	@Override
	public double getVolume()
	{
		return 0;
	}
	
	@Override
	public boolean isInside(int x, int y)
	{
		int d = x - x;
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
	public boolean isInside(int x, int y, int z)
	{
		int d = x - x;
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
		return new LocationHolder(x + Rnd.get(a), y + Rnd.get(a), 0);
	}
}
