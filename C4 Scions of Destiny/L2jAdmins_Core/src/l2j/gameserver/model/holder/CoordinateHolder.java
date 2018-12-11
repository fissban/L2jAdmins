package l2j.gameserver.model.holder;

/**
 * Simple class for storing location x/y/z
 * @author fissban
 */
public class CoordinateHolder
{
	private int x;
	private int y;
	private int z;
	
	public CoordinateHolder(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void setZ(int z)
	{
		this.z = z;
	}
	
	public void setXYZ(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof CoordinateHolder)
		{
			CoordinateHolder loc = (CoordinateHolder) object;
			
			return equals(loc.getX(), loc.getY(), loc.getZ());
		}
		return false;
	}
	
	public boolean equals(int x, int y, int z)
	{
		return (this.x == x) && (this.y == y) && (this.z == z);
	}
}
