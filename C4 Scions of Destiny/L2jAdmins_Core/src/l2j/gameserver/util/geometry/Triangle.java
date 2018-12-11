package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * @author Hasha
 */
public class Triangle extends AShape
{
	// A point
	protected final int Ax;
	protected final int Ay;
	
	// BA vector coordinates
	protected final int BAx;
	protected final int BAy;
	
	// CA vector coordinates
	protected final int CAx;
	protected final int CAy;
	
	// size
	protected final int size;
	
	/**
	 * Triangle constructor.
	 * @param A : Point A of the triangle.
	 * @param B : Point B of the triangle.
	 * @param C : Point C of the triangle.
	 */
	public Triangle(int[] A, int[] B, int[] C)
	{
		Ax = A[0];
		Ay = A[1];
		
		BAx = B[0] - A[0];
		BAy = B[1] - A[1];
		
		CAx = C[0] - A[0];
		CAy = C[1] - A[1];
		
		size = Math.abs((BAx * CAy) - (CAx * BAy)) / 2;
	}
	
	@Override
	public final int getSize()
	{
		return size;
	}
	
	@Override
	public double getArea()
	{
		return size;
	}
	
	@Override
	public double getVolume()
	{
		return 0;
	}
	
	@Override
	public final boolean isInside(int x, int y)
	{
		// method parameters must be LONG, since whole calculations must be done in LONG...we are doing really big numbers
		final long dx = x - Ax;
		final long dy = y - Ay;
		
		final boolean a = (((0 - dx) * (BAy - 0)) - ((BAx - 0) * (0 - dy))) >= 0;
		final boolean b = (((BAx - dx) * (CAy - BAy)) - ((CAx - BAx) * (BAy - dy))) >= 0;
		final boolean c = (((CAx - dx) * (0 - CAy)) - ((0 - CAx) * (CAy - dy))) >= 0;
		
		return (a == b) && (b == c);
	}
	
	@Override
	public boolean isInside(int x, int y, int z)
	{
		// method parameters must be LONG, since whole calculations must be done in LONG...we are doing really big numbers
		final long dx = x - Ax;
		final long dy = y - Ay;
		
		final boolean a = (((0 - dx) * (BAy - 0)) - ((BAx - 0) * (0 - dy))) >= 0;
		final boolean b = (((BAx - dx) * (CAy - BAy)) - ((CAx - BAx) * (BAy - dy))) >= 0;
		final boolean c = (((CAx - dx) * (0 - CAy)) - ((0 - CAx) * (CAy - dy))) >= 0;
		
		return (a == b) && (b == c);
	}
	
	@Override
	public LocationHolder getRandomLocation()
	{
		// get relative length of AB and AC vectors
		double ba = Rnd.nextDouble();
		double ca = Rnd.nextDouble();
		
		// adjust length if too long
		if ((ba + ca) > 1)
		{
			ba = 1 - ba;
			ca = 1 - ca;
		}
		
		// calculate coordinates (take A, add AB and AC)
		final int x = Ax + (int) ((ba * BAx) + (ca * CAx));
		final int y = Ay + (int) ((ba * BAy) + (ca * CAy));
		
		// return
		return new LocationHolder(x, y, 0);
	}
}
