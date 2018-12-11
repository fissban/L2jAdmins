package l2j.gameserver.geoengine.geodata;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * @author Hasha
 */
public class GeoLocation extends LocationHolder
{
	private byte nswe;
	
	public GeoLocation(int x, int y, int z)
	{
		super(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
		nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
	}
	
	public void set(int x, int y, short z)
	{
		super.setXYZ(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
		nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
	}
	
	public int getGeoX()
	{
		return x;
	}
	
	public int getGeoY()
	{
		return y;
	}
	
	@Override
	public int getX()
	{
		return GeoEngine.getWorldX(x);
	}
	
	@Override
	public int getY()
	{
		return GeoEngine.getWorldY(y);
	}
	
	public byte getNSWE()
	{
		return nswe;
	}
}
