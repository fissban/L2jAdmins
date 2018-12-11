package l2j.gameserver.model.interfaces;

/**
 * @author fissban
 */
public interface Location
{
	public int getX();
	
	public int getY();
	
	public int getZ();
	
	public int getHeading();
	
	public void setX(int x);
	
	public void setY(int y);
	
	public void setZ(int z);
	
	public void setHeading(int heading);
}
