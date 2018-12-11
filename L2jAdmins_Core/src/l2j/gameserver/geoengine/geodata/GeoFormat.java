package l2j.gameserver.geoengine.geodata;

/**
 * @author Hasha
 */
public enum GeoFormat
{
	L2J("%d_%d.l2j"),
	L2OFF("%d_%d_conv.dat"),
	L2D("%d_%d.l2d");
	
	private final String filename;
	
	private GeoFormat(String filename)
	{
		this.filename = filename;
	}
	
	public String getFilename()
	{
		return filename;
	}
}
