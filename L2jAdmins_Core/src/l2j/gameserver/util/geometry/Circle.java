package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Circle extends AShape
{
	// circle center coordinates
	protected final int x;
	protected final int y;
	
	// circle radius
	protected final int r;
	
	/**
	 * Circle constructor
	 * @param x : Center X coordinate.
	 * @param y : Center Y coordinate.
	 * @param r : Circle radius.
	 */
	public Circle(int x, int y, int r)
	{
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	@Override
	public final int getSize()
	{
		return (int) Math.PI * r * r;
	}
	
	@Override
	public double getArea()
	{
		return (int) Math.PI * r * r;
	}
	
	@Override
	public double getVolume()
	{
		return 0;
	}
	
	@Override
	public final boolean isInside(int x, int y)
	{
		final int dx = x - x;
		final int dy = y - y;
		
		return ((dx * dx) + (dy * dy)) <= (r * r);
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		final int dx = x - x;
		final int dy = y - y;
		
		return ((dx * dx) + (dy * dy)) <= (r * r);
	}
	
	@Override
	public LocationHolder getRandomLocation()
	{
		// get uniform distance and angle
		final double distance = Math.sqrt(Rnd.nextDouble()) * r;
		final double angle = Rnd.nextDouble() * Math.PI * 2;
		
		// calculate coordinates and return
		return new LocationHolder((int) (distance * Math.cos(angle)), (int) (distance * Math.sin(angle)), 0);
	}
}
