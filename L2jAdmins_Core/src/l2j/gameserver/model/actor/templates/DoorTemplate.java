package l2j.gameserver.model.actor.templates;

import l2j.gameserver.model.StatsSet;

public class DoorTemplate extends CharTemplate
{
	private final String name;
	private final int id;
	private final int level;
	
	// coordinates can be part of template, since we spawn 1 instance of door at fixed position
	private final int x;
	private final int y;
	private final int z;
	
	// geodata description of the door
	private final int geoX;
	private final int geoY;
	private final int geoZ;
	private final byte[][] geoData;
	
	private final int castleId;
	private final int openTime;
	
	private final boolean isWall;
	private final boolean unlockable;
	// private boolean isOpen;
	
	public DoorTemplate(StatsSet stats)
	{
		super(stats);
		
		name = stats.getString("name");
		id = stats.getInteger("id");
		// type = stats.getEnum("type", DoorType.class);
		level = stats.getInteger("level");
		
		x = stats.getInteger("posX");
		y = stats.getInteger("posY");
		z = stats.getInteger("posZ");
		
		geoX = stats.getInteger("geoX");
		geoY = stats.getInteger("geoY");
		geoZ = stats.getInteger("geoZ");
		geoData = stats.getObject("geoData", byte[][].class);
		
		castleId = stats.getInteger("castleId", 0);
		
		// openType = stats.getEnum("openType", OpenType.class, OpenType.NPC);
		openTime = stats.getInteger("openTime", 0);
		
		isWall = stats.getBool("isWall");
		unlockable = stats.getBool("unlockable");
		// isOpen = stats.getBool("isOpen", false);
	}
	
	public final String getName()
	{
		return name;
	}
	
	public final int getId()
	{
		return id;
	}
	
	// public final DoorType getType()
	// {
	// return type;
	// }
	
	public final int getLevel()
	{
		return level;
	}
	
	public final int getPosX()
	{
		return x;
	}
	
	public final int getPosY()
	{
		return y;
	}
	
	public final int getPosZ()
	{
		return z;
	}
	
	public final int getGeoX()
	{
		return geoX;
	}
	
	public final int getGeoY()
	{
		return geoY;
	}
	
	public final int getGeoZ()
	{
		return geoZ;
	}
	
	public final byte[][] getGeoData()
	{
		return geoData;
	}
	
	public final int getCastleId()
	{
		return castleId;
	}
	
	public final int getOpenTime()
	{
		return openTime;
	}
	
	/**
	 * @return the isWall
	 */
	public boolean isWall()
	{
		return isWall;
	}
	
	public boolean isUnlockable()
	{
		return unlockable;
	}
}
