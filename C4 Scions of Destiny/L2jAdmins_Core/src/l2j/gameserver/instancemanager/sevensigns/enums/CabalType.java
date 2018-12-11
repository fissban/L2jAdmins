package l2j.gameserver.instancemanager.sevensigns.enums;

/**
 * @author fissban
 */
public enum CabalType
{
	NULL("No Cabal", "No Cabal"),
	DUSK("Revolutionaries of Dusk", "dusk"),
	DAWN("Lords of Dawn", "dawn");
	
	private String name;
	private String shortName;
	
	CabalType(String name, String shortName)
	{
		this.name = name;
		this.shortName = shortName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getShortName()
	{
		return shortName;
	}
}
