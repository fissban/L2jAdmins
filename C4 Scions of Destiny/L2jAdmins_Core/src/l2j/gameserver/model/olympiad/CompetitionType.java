package l2j.gameserver.model.olympiad;

/**
 * @author DS
 */
public enum CompetitionType
{
	CLASSED("classed"),
	NON_CLASSED("non-classed");
	
	private final String name;
	
	private CompetitionType(String name)
	{
		this.name = name;
	}
	
	@Override
	public final String toString()
	{
		return name;
	}
}
