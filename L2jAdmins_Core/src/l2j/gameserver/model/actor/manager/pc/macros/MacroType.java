package l2j.gameserver.model.actor.manager.pc.macros;

/**
 * @author fissban
 */
public enum MacroType
{
	SKILL(1),
	ACTION(3),
	SHORTCUT(4),
	COMMAND(6);
	
	private final int type;
	
	MacroType(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
}
