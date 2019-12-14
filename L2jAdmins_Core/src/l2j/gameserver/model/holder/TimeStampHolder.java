package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.manager.character.skills.Skill;

/**
 * Simple class containing all necessary information to maintain valid timestamps and reuse for skills upon relog. Filter this carefully as it becomes redundant to store reuse for small delays.
 * @author Yesod
 */
public class TimeStampHolder
{
	private SkillHolder skill;
	private long reuse;
	private final long stamp;
	
	public TimeStampHolder(Skill skill, long reuse)
	{
		this.skill = new SkillHolder(skill.getId(), skill.getLevel());
		this.reuse = reuse;
		stamp = System.currentTimeMillis() + reuse;
	}
	
	public TimeStampHolder(Skill skill, long reuse, long systime)
	{
		this.skill = new SkillHolder(skill.getId(), skill.getLevel());
		this.reuse = reuse;
		stamp = systime;
	}
	
	public long getStamp()
	{
		return stamp;
	}
	
	public Skill getSkill()
	{
		return skill.getSkill();
	}
	
	public long getReuse()
	{
		return reuse;
	}
	
	public long getRemaining()
	{
		return Math.max(stamp - System.currentTimeMillis(), 0);
	}
	
	public boolean hasNotPassed()
	{
		return System.currentTimeMillis() < stamp;
	}
}
