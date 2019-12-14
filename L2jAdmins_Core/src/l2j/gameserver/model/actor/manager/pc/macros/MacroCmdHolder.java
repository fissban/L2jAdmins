package l2j.gameserver.model.actor.manager.pc.macros;

/**
 * @author fissban
 */
public class MacroCmdHolder
{
	private int entry;
	private MacroType type;
	private int skillId; // skill_id or page for shortcuts
	private int shortCutId; // shortcut
	private String cmd;
	
	public MacroCmdHolder(int entry, MacroType type, int skillId, int shortCutId, String cmd)
	{
		this.entry = entry;
		this.type = type;
		this.skillId = skillId;
		this.shortCutId = shortCutId;
		this.cmd = cmd;
	}
	
	public int getEntry()
	{
		return entry;
	}
	
	public void setEntry(int entry)
	{
		this.entry = entry;
	}
	
	/**
	 * type 1 = skill, 3 = action, 4 = shortcut
	 * @return
	 */
	public MacroType getType()
	{
		return type;
	}
	
	/**
	 * type 1 = skill, 3 = action, 4 = shortcut
	 * @param type
	 */
	public void setType(MacroType type)
	{
		this.type = type;
	}
	
	/**
	 * skillId or page
	 * @return int
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * setSkillId or page
	 * @param d1
	 */
	public void setSkillId(int d1)
	{
		skillId = d1;
	}
	
	public int getShortCutId()
	{
		return shortCutId;
	}
	
	public void setShortCutId(int shortCutId)
	{
		this.shortCutId = shortCutId;
	}
	
	/**
	 * Return command name
	 * @return
	 */
	public String getCmd()
	{
		return cmd;
	}
	
	/**
	 * Set command name
	 * @param cmd
	 */
	public void setCmd(String cmd)
	{
		this.cmd = cmd;
	}
}
