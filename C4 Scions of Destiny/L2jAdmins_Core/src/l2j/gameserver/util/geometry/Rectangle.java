package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Rectangle extends AShape
{
	// rectangle origin coordinates
	protected final int x;
	protected final int y;
	
	// rectangle width and height
	protected final int w;
	protected final int h;
	
	/**
	 * Rectangle constructor.
	 * @param x : Bottom left X coordinate.
	 * @param y : Bottom left Y coordinate.
	 * @param w : Rectangle width.
	 * @param h : Rectangle height.
	 */
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		
		this.w = w;
		this.h = h;
	}
	
	@Override
	public final int getSize()
	{
		return w * h;
	}
	
	@Override
	public double getArea()
	{
		return w * h;
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
	public boolean isInside(int x, int y, int z)
	{
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
		return new LocationHolder(x + Rnd.get(w), y + Rnd.get(h), 0);
	}
}
