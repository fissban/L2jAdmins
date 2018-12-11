package l2j.gameserver.instancemanager.sevensigns.enums;

/**
 * @author fissban
 */
public enum SealType
{
	NULL("", ""),
	AVARICE("Seal of Avarice", "Avarice"),
	GNOSIS("Seal of Gnosis", "Gnosis"),
	STRIFE("Seal of Strife", "Strife");
	
	private String name;
	private String shortName;
	
	SealType(String name, String shortName)
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
