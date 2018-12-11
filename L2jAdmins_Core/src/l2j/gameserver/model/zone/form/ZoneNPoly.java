package l2j.gameserver.model.zone.form;

import l2j.gameserver.model.zone.ZoneForm;

/**
 * A not so primitive npoly zone
 * @author durgus
 */
public class ZoneNPoly extends ZoneForm
{
	private final int[] x;
	private final int[] y;
	private final int z1;
	private final int z2;
	
	public ZoneNPoly(int[] x, int[] y, int z1, int z2)
	{
		this.x = x;
		this.y = y;
		this.z1 = z1;
		this.z2 = z2;
	}
	
	@Override
	public boolean isInsideZone(int x, int y, int z)
	{
		if ((z < z1) || (z > z2))
		{
			return false;
		}
		
		boolean inside = false;
		for (int i = 0, j = this.x.length - 1; i < this.x.length; j = i++)
		{
			if ((((this.y[i] <= y) && (y < this.y[j])) || ((this.y[j] <= y) && (y < this.y[i]))) && (x < ((((this.x[j] - this.x[i]) * (y - this.y[i])) / (this.y[j] - this.y[i])) + this.x[i])))
			{
				inside = !inside;
			}
		}
		return inside;
	}
	
	@Override
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2)
	{
		int tX, tY, uX, uY;
		
		// First check if a point of the polygon lies inside the rectangle
		if ((x[0] > ax1) && (x[0] < ax2) && (y[0] > ay1) && (y[0] < ay2))
		{
			return true;
		}
		
		// Or a point of the rectangle inside the polygon
		if (isInsideZone(ax1, ay1, (z2 - 1)))
		{
			return true;
		}
		
		// If the first point wasn't inside the rectangle it might still have any line crossing any side
		// of the rectangle
		
		// Check every possible line of the polygon for a collision with any of the rectangles side
		for (int i = 0; i < y.length; i++)
		{
			tX = x[i];
			tY = y[i];
			uX = x[(i + 1) % x.length];
			uY = y[(i + 1) % x.length];
			
			// Check if this line intersects any of the four sites of the rectangle
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax1, ay2))
			{
				return true;
			}
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax2, ay1))
			{
				return true;
			}
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax1, ay2))
			{
				return true;
			}
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax2, ay1))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public double getDistanceToZone(int x, int y)
	{
		double test, shortestDist = Math.pow(this.x[0] - x, 2) + Math.pow(this.y[0] - y, 2);
		
		for (int i = 1; i < this.y.length; i++)
		{
			test = Math.pow(this.x[i] - x, 2) + Math.pow(this.y[i] - y, 2);
			if (test < shortestDist)
			{
				shortestDist = test;
			}
		}
		
		return Math.sqrt(shortestDist);
	}
	
	@Override
	public int getLowZ()
	{
		return z1;
	}
	
	@Override
	public int getHighZ()
	{
		return z2;
	}
}
