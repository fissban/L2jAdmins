package l2j.gameserver.model.holder;

/**
 * @author fissban
 */
public class BoatPatchHolder extends LocationHolder
{
	private int rotacionSpeed;
	private int movementSpeed;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param rotacionSpeed
	 * @param movementSpeed
	 */
	public BoatPatchHolder(int x, int y, int z, int movementSpeed, int rotacionSpeed)
	{
		super(x, y, z);
		
		this.movementSpeed = movementSpeed;
		this.rotacionSpeed = rotacionSpeed;
	}
	
	public int getRotacionSpeed()
	{
		return rotacionSpeed;
	}
	
	public int getMovementSpeed()
	{
		return movementSpeed;
	}
}
