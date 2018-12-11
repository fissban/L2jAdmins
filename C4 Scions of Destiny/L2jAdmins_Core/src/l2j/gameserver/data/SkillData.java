package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.engines.DocumentEngine;
import l2j.gameserver.model.skills.Skill;

/**
 *
 */
public class SkillData
{
	private static final List<Skill> NOBLE_SKILLS = new ArrayList<>();
	
	private static final List<Skill> SIEGE_SKILLS = new ArrayList<>();
	
	private static final Map<Integer, Skill> skills = new HashMap<>();
	private static final Map<Integer, Integer> skillMaxLevel = new HashMap<>();
	
	public void load()
	{
		reload();
		// -----------------------------------------------//
		NOBLE_SKILLS.add(getInstance().getSkill(325, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(326, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(327, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(1323, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(1324, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(1325, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(1326, 1));
		NOBLE_SKILLS.add(getInstance().getSkill(1327, 1));
		// -----------------------------------------------//
		SIEGE_SKILLS.add(getInstance().getSkill(246, 1));
		SIEGE_SKILLS.add(getInstance().getSkill(247, 1));
		// -----------------------------------------------//
	}
	
	public void reload()
	{
		skills.clear();
		skillMaxLevel.clear();
		
		DocumentEngine.getInstance().loadAllSkills(skills);
		
		for (final Skill skill : skills.values())
		{
			final int skillLvl = skill.getLevel();
			if (skillLvl < 99)
			{
				final int skillId = skill.getId();
				final int maxLvl = (skillMaxLevel.get(skillId) == null) ? 0 : skillMaxLevel.get(skillId);
				if (skillLvl > maxLvl)
				{
					skillMaxLevel.put(skillId, skillLvl);
				}
			}
		}
	}
	
	/**
	 * Provides the skill hash
	 * @param  skill The Skill to be hashed
	 * @return       getSkillHashCode(skill.getId(), skill.getLevel())
	 */
	public static int getSkillHashCode(Skill skill)
	{
		return getSkillHashCode(skill.getId(), skill.getLevel());
	}
	
	/**
	 * Centralized method for easier change of the hashing sys
	 * @param  skillId    The Skill Id
	 * @param  skillLevel The Skill Level
	 * @return            The Skill hash number
	 */
	public static int getSkillHashCode(int skillId, int skillLevel)
	{
		return (skillId * 256) + skillLevel;
	}
	
	public final Skill getSkill(final int skillId, final int level)
	{
		return skills.get(getSkillHashCode(skillId, level));
	}
	
	public final int getMaxLevel(final int skillId)
	{
		return skillMaxLevel.get(skillId);
	}
	
	public static List<Skill> getNobleSkills()
	{
		return NOBLE_SKILLS;
	}
	
	public static List<Skill> getSiegeSkills()
	{
		return SIEGE_SKILLS;
	}
	
	public static SkillData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillData INSTANCE = new SkillData();
	}
}
