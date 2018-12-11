package l2j.gameserver.model.skills.enums;

/**
 * @author fissban
 */
public enum SkillConditionType
{
	NONE(0),
	BEHIND(8), // 8
	CRIT(16); // 16
	
	private int value;
	
	SkillConditionType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
