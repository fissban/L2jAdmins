package l2j.gameserver.model.zone.form;

import l2j.gameserver.model.zone.ZoneForm;

/**
 * A primitive rectangular zone
 * @author durgus
 */
public class ZoneCubo extends ZoneForm
{
	private int x1, x2, y1, y2, z1, z2;
	
	public ZoneCubo(int x1, int x2, int y1, int y2, int z1, int z2)
	{
		this.x1 = x1;
		this.x2 = x2;
		if (this.x1 > this.x2) // switch them if alignment is wrong
		{
			this.x1 = x2;
			this.x2 = x1;
		}
		
		this.y1 = y1;
		this.y2 = y2;
		if (this.y1 > this.y2) // switch them if alignment is wrong
		{
			this.y1 = y2;
			this.y2 = y1;
		}
		
		this.z1 = z1;
		this.z2 = z2;
		if (this.z1 > this.z2) // switch them if alignment is wrong
		{
			this.z1 = z2;
			this.z2 = z1;
		}
	}
	
	@Override
	public boolean isInsideZone(int x, int y, int z)
	{
		if ((x < x1) || (x > x2) || (y < y1) || (y > y2) || (z < z1) || (z > z2))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2)
	{
		// Check if any point inside this rectangle
		if (isInsideZone(ax1, ay1, (z2 - 1)))
		{
			return true;
		}
		if (isInsideZone(ax1, ay2, (z2 - 1)))
		{
			return true;
		}
		if (isInsideZone(ax2, ay1, (z2 - 1)))
		{
			return true;
		}
		if (isInsideZone(ax2, ay2, (z2 - 1)))
		{
			return true;
		}
		
		// Check if any point from this rectangle is inside the other one
		if ((x1 > ax1) && (x1 < ax2) && (y1 > ay1) && (y1 < ay2))
		{
			return true;
		}
		if ((x1 > ax1) && (x1 < ax2) && (y2 > ay1) && (y2 < ay2))
		{
			return true;
		}
		if ((x2 > ax1) && (x2 < ax2) && (y1 > ay1) && (y1 < ay2))
		{
			return true;
		}
		if ((x2 > ax1) && (x2 < ax2) && (y2 > ay1) && (y2 < ay2))
		{
			return true;
		}
		
		// Horizontal lines may intersect vertical lines
		if (lineSegmentsIntersect(x1, y1, x2, y1, ax1, ay1, ax1, ay2))
		{
			return true;
		}
		if (lineSegmentsIntersect(x1, y1, x2, y1, ax2, ay1, ax2, ay2))
		{
			return true;
		}
		if (lineSegmentsIntersect(x1, y2, x2, y2, ax1, ay1, ax1, ay2))
		{
			return true;
		}
		if (lineSegmentsIntersect(x1, y2, x2, y2, ax2, ay1, ax2, ay2))
		{
			return true;
		}
		
		// Vertical lines may intersect horizontal lines
		if (lineSegmentsIntersect(x1, y1, x1, y2, ax1, ay1, ax2, ay1))
		{
			return true;
		}
		if (lineSegmentsIntersect(x1, y1, x1, y2, ax1, ay2, ax2, ay2))
		{
			return true;
		}
		if (lineSegmentsIntersect(x2, y1, x2, y2, ax1, ay1, ax2, ay1))
		{
			return true;
		}
		if (lineSegmentsIntersect(x2, y1, x2, y2, ax1, ay2, ax2, ay2))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public double getDistanceToZone(int x, int y)
	{
		double test, shortestDist = Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2);
		
		test = Math.pow(x1 - x, 2) + Math.pow(y2 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
		}
		
		test = Math.pow(x2 - x, 2) + Math.pow(y1 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
		}
		
		test = Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2);
		if (test < shortestDist)
		{
			shortestDist = test;
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
