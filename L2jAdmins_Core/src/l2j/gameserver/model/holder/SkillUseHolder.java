package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.manager.character.skills.Skill;

/**
 * Skill casting information (used to queue when several skills are cast in a short time)
 **/
public class SkillUseHolder
{
	private Skill skill;
	private boolean ctrlPressed;
	private boolean shiftPressed;
	
	public SkillUseHolder()
	{
	}
	
	public SkillUseHolder(Skill skill, boolean ctrlPressed, boolean shiftPressed)
	{
		this.skill = skill;
		this.ctrlPressed = ctrlPressed;
		this.shiftPressed = shiftPressed;
	}
	
	public Skill getSkill()
	{
		return skill;
	}
	
	public int getSkillId()
	{
		return (getSkill() != null) ? getSkill().getId() : -1;
	}
	
	public boolean isCtrlPressed()
	{
		return ctrlPressed;
	}
	
	public boolean isShiftPressed()
	{
		return shiftPressed;
	}
	
	public void setSkill(Skill skill)
	{
		this.skill = skill;
	}
	
	public void setCtrlPressed(boolean ctrlPressed)
	{
		this.ctrlPressed = ctrlPressed;
	}
	
	public void setShiftPressed(boolean shiftPressed)
	{
		this.shiftPressed = shiftPressed;
	}
}
