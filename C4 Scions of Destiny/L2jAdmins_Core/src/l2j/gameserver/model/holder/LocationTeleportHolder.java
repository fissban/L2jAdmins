package l2j.gameserver.model.holder;

/**
 * This class ...
 * @version $Revision: 1.2.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class LocationTeleportHolder
{
	private final int teleId;
	private final int x;
	private final int y;
	private final int z;
	private final int price;
	private final boolean forNoble;
	
	public LocationTeleportHolder(int teleId, int x, int y, int z, int price, boolean forNoble)
	{
		this.teleId = teleId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.price = price;
		this.forNoble = forNoble;
	}
	
	/**
	 * @return
	 */
	public int getId()
	{
		return teleId;
	}
	
	/**
	 * @return
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * @return
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * @return
	 */
	public int getZ()
	{
		return z;
	}
	
	/**
	 * @return
	 */
	public int getPrice()
	{
		return price;
	}
	
	/**
	 * @return true si el teleport es solo para el uso de los nobles
	 */
	public boolean isForNoble()
	{
		return forNoble;
	}
}
