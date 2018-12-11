package l2j.gameserver.model.macros;

/**
 * @author fissban
 */
public enum PcMacroType
{
	SKILL(1),
	ACTION(3),
	SHORTCUT(4),
	COMMAND(6);
	
	private final int type;
	
	PcMacroType(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
}
