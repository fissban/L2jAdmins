package l2j.gameserver.model.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l2j.util.Rnd;

public class TerritoryHolder
{
	private static final Logger LOG = Logger.getLogger(TerritoryHolder.class.getName());
	
	protected class Point
	{
		protected int x, y, zmin, zmax, proc;
		
		Point(int x, int y, int zmin, int zmax, int proc)
		{
			this.x = x;
			this.y = y;
			this.zmin = zmin;
			this.zmax = zmax;
			this.proc = proc;
		}
	}
	
	private final List<Point> points;
	private final int terr;
	private int x_min;
	private int x_max;
	private int y_min;
	private int y_max;
	private int z_min;
	private int z_max;
	private int proc_max;
	
	public TerritoryHolder(int terr)
	{
		points = new ArrayList<>();
		this.terr = terr;
		x_min = 999999;
		x_max = -999999;
		y_min = 999999;
		y_max = -999999;
		z_min = 999999;
		z_max = -999999;
		proc_max = 0;
	}
	
	public void add(int x, int y, int zmin, int zmax, int proc)
	{
		points.add(new Point(x, y, zmin, zmax, proc));
		if (x < x_min)
		{
			x_min = x;
		}
		if (y < y_min)
		{
			y_min = y;
		}
		if (x > x_max)
		{
			x_max = x;
		}
		if (y > y_max)
		{
			y_max = y;
		}
		if (zmin < z_min)
		{
			z_min = zmin;
		}
		if (zmax > z_max)
		{
			z_max = zmax;
		}
		proc_max += proc;
	}
	
	public boolean isIntersect(int x, int y, Point p1, Point p2)
	{
		double dy1 = p1.y - y;
		double dy2 = p2.y - y;
		
		if (Math.signum(dy1) == Math.signum(dy2))
		{
			return false;
		}
		
		double dx1 = p1.x - x;
		double dx2 = p2.x - x;
		
		if ((dx1 >= 0) && (dx2 >= 0))
		{
			return true;
		}
		
		if ((dx1 < 0) && (dx2 < 0))
		{
			return false;
		}
		
		double dx0 = (dy1 * (p1.x - p2.x)) / (p1.y - p2.y);
		
		return dx0 <= dx1;
	}
	
	public boolean isInside(int x, int y)
	{
		int intersect_count = 0;
		for (int i = 0; i < points.size(); i++)
		{
			Point p1 = points.get(i > 0 ? i - 1 : points.size() - 1);
			Point p2 = points.get(i);
			
			if (isIntersect(x, y, p1, p2))
			{
				intersect_count++;
			}
		}
		
		return (intersect_count % 2) == 1;
	}
	
	public int[] getRandomPoint()
	{
		int i;
		int[] p = new int[4];
		if (proc_max > 0)
		{
			int pos = 0;
			int rnd = Rnd.nextInt(proc_max);
			for (i = 0; i < points.size(); i++)
			{
				Point p1 = points.get(i);
				pos += p1.proc;
				if (rnd <= pos)
				{
					p[0] = p1.x;
					p[1] = p1.y;
					p[2] = p1.zmin;
					p[3] = p1.zmax;
					return p;
				}
			}
			
		}
		for (i = 0; i < 100; i++)
		{
			p[0] = Rnd.get(x_min, x_max);
			p[1] = Rnd.get(y_min, y_max);
			if (isInside(p[0], p[1]))
			{
				double curdistance = 0;
				p[2] = z_min + 100;
				p[3] = z_max;
				for (i = 0; i < points.size(); i++)
				{
					Point p1 = points.get(i);
					double dx = p1.x - p[0];
					double dy = p1.y - p[1];
					double distance = Math.sqrt((dx * dx) + (dy * dy));
					if ((curdistance == 0) || (distance < curdistance))
					{
						curdistance = distance;
						p[2] = p1.zmin + 100;
					}
				}
				return p;
			}
		}
		LOG.warning("Can't make point for territory" + terr);
		return p;
	}
	
	public int getProcMax()
	{
		return proc_max;
	}
}
