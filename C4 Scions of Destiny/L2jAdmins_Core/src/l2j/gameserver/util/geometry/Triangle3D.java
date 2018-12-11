package l2j.gameserver.util.geometry;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * Tri-sided polygon in 3D, while having bottom and top area flat (in Z coordinate).<br>
 * It is <b>not</b> 3D oriented triangle.
 * @author Hasha
 */
public class Triangle3D extends Triangle
{
	// min and max Z coorinates
	private final int minZ;
	private final int maxZ;
	
	// total length of all sides
	private final double length;
	
	/**
	 * Triangle constructor.
	 * @param A : Point A of the triangle.
	 * @param B : Point B of the triangle.
	 * @param C : Point C of the triangle.
	 */
	public Triangle3D(int[] A, int[] B, int[] C)
	{
		super(A, B, C);
		
		minZ = Math.min(A[2], Math.min(B[2], C[2]));
		maxZ = Math.max(A[2], Math.max(B[2], C[2]));
		
		final int CBx = CAx - BAx;
		final int CBy = CAy - BAy;
		length = Math.sqrt((BAx * BAx) + (BAy * BAy)) + Math.sqrt((CAx * CAx) + (CAy * CAy)) + Math.sqrt((CBx * CBx) + (CBy * CBy));
	}
	
	@Override
	public double getArea()
	{
		return (size * 2) + (length * (maxZ - minZ));
	}
	
	@Override
	public double getVolume()
	{
		return size * (maxZ - minZ);
	}
	
	@Override
	public final boolean isInside(int x, int y, int z)
	{
		if ((z < minZ) || (z > maxZ))
		{
			return false;
		}
		
		return super.isInside(x, y, z);
	}
	
	@Override
	public final LocationHolder getRandomLocation()
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
		
		// calc coords (take A, add AB and AC)
		final int x = Ax + (int) ((ba * BAx) + (ca * CAx));
		final int y = Ay + (int) ((ba * BAy) + (ca * CAy));
		
		// return
		return new LocationHolder(x, y, Rnd.get(minZ, maxZ));
	}
}
