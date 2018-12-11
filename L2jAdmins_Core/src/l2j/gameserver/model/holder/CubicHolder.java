package l2j.gameserver.model.holder;

import java.util.List;

import l2j.gameserver.model.actor.instance.enums.CubicType;

/**
 * @author fissban
 */
public class CubicHolder
{
	private CubicType cubicType;
	private int chance;
	private int delayAction;
	private final int disappearTime;
	private List<Integer> skillsId;
	
	public CubicHolder(CubicType cubicType, int chance, int delayAction, int disappearTime, List<Integer> skillsId)
	{
		this.cubicType = cubicType;
		this.chance = chance;
		this.delayAction = delayAction;
		this.disappearTime = disappearTime;
		this.skillsId = skillsId;
	}
	
	public CubicType getType()
	{
		return cubicType;
	}
	
	public List<Integer> getSkillsIds()
	{
		return skillsId;
	}
	
	public int getDisappearTime()
	{
		return disappearTime;
	}
	
	public int getChance()
	{
		return chance;
	}
	
	public int getDelayAction()
	{
		return delayAction;
	}
}
