package main.holders;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.manager.character.skills.Skill;

/**
 * A generic int/int container.
 */
public class IntIntHolder
{
	private int id;
	private int value;
	
	public IntIntHolder(int id, int value)
	{
		this.id = id;
		this.value = value;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	/**
	 * @return the Skill associated to the id/value.
	 */
	public final Skill getSkill()
	{
		return SkillData.getInstance().getSkill(id, value);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": Id: " + id + ", Value: " + value;
	}
}
