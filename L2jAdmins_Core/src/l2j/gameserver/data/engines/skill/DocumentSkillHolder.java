package l2j.gameserver.data.engines.skill;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.manager.character.skills.Skill;

/**
 * @author fissban
 */
public class DocumentSkillHolder
{
	// TODO pasar a private y crear metiodos GET y SET y aplicar la nomenclatura para las variables
	
	public int id;
	public String name;
	public StatsSet[] sets;
	public StatsSet[] enchsets1;
	public StatsSet[] enchsets2;
	public int currentLevel;
	public List<Skill> skills = new ArrayList<>();
	public List<Skill> currentSkills = new ArrayList<>();
}
