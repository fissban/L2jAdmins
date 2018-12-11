package l2j.gameserver.instancemanager.sevensigns.enums;

/**
 * @author fissban
 */
public enum PeriodType
{
	RECRUITING("Quest Event Initialization"),
	COMPETITION("Competition (Quest Event)"),
	RESULTS("Quest Event Results"),
	SEAL_VALIDATION("Seal Validation");
	
	private String name;
	
	PeriodType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
