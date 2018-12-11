package l2j.gameserver.model.holder;

/**
 * TODO se podria remplazar por LocationHolder directamente con modificar este ultimo solo un poco.
 * @author fissban
 */
public class SiegeSpawnHolder
{
	private final LocationHolder location;
	private final int npcId;
	private final int heading;
	private final int castleId;
	private int hp;
	
	public SiegeSpawnHolder(int castleId, int x, int y, int z, int heading, int npcId)
	{
		this.castleId = castleId;
		location = new LocationHolder(x, y, z, heading);
		this.heading = heading;
		this.npcId = npcId;
	}
	
	public SiegeSpawnHolder(int castleId, int x, int y, int z, int heading, int npcId, int hp)
	{
		this.castleId = castleId;
		location = new LocationHolder(x, y, z, heading);
		this.heading = heading;
		this.npcId = npcId;
		this.hp = hp;
	}
	
	public int getCastleId()
	{
		return castleId;
	}
	
	public int getNpcId()
	{
		return npcId;
	}
	
	public int getHeading()
	{
		return heading;
	}
	
	public int getHp()
	{
		return hp;
	}
	
	public LocationHolder getLocation()
	{
		return location;
	}
}
