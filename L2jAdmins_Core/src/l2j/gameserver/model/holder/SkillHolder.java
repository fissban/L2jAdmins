package l2j.gameserver.model.holder;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.skills.Skill;

/**
 * @author BiggBoss Simple class for storing skill id/level
 */
public final class SkillHolder
{
	private final int skillId;
	private final int skillLvl;
	
	public SkillHolder(int skillId, int skillLvl)
	{
		this.skillId = skillId;
		this.skillLvl = skillLvl;
	}
	
	public SkillHolder(Skill skill)
	{
		skillId = skill.getId();
		skillLvl = skill.getLevel();
	}
	
	public final int getSkillId()
	{
		return skillId;
	}
	
	public final int getSkillLvl()
	{
		return skillLvl;
	}
	
	public final Skill getSkill()
	{
		return SkillData.getInstance().getSkill(skillId, skillLvl);
	}
}
