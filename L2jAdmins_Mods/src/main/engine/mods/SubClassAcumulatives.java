package main.engine.mods;

import java.util.HashMap;
import java.util.logging.Level;

import l2j.L2DatabaseFactory;
import l2j.gameserver.data.SkillData;
import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class SubClassAcumulatives extends AbstractMod
{
	// SQL
	private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level,class_index FROM character_skills WHERE char_obj_id=?";
	
	/**
	 * Constructor
	 */
	public SubClassAcumulatives()
	{
		registerMod(ConfigData.ENABLE_SubClassAcumulatives);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public boolean onRestoreSkills(PlayerHolder ph)
	{
		if (ph.getInstance() == null)
		{
			return true;
		}
		
		var skills = new HashMap<Integer, Integer>();
		
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var ps = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
		{
			// Retrieve all skills of this Player from the database
			ps.setInt(1, ph.getObjectId());
			
			try (var rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int id = rs.getInt("skill_id");
					int level = rs.getInt("skill_level");
					int classIndex = rs.getInt("class_index");
					
					if (ph.getInstance().getClassIndex() != classIndex)
					{
						var skill = SkillData.getInstance().getSkill(id, level);
						
						if (skill == null)
						{
							LOG.log(Level.SEVERE, "Skipped null skill Id: " + id + ", Level: " + level + " while restoring player skills for " + ph.getName());
							continue;
						}
						
						if (!ConfigData.ACUMULATIVE_PASIVE_SKILLS)
						{
							if (skill.isPassive())
							{
								continue;
							}
						}
						
						if (ConfigData.DONT_ACUMULATIVE_SKILLS_ID.contains(id))
						{
							continue;
						}
					}
					
					// Save all the skills that we will teach our character.
					// This will avoid teaching a skill from lvl 1 to 15 for example
					// And directly we teach the lvl 15 =)
					if ((skills.get(id) != null) && (skills.get(id) > level))
					{
						continue;
					}
					
					skills.put(id, level);
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Could not restore " + ph.getName() + " skills:", e);
			e.printStackTrace();
		}
		
		for (var entry : skills.entrySet())
		{
			var id = entry.getKey();
			var level = entry.getValue();
			
			// The level of skill that the character has is checked.
			if (ph.getInstance().getSkillLevel(id) < level)
			{
				// Create a Skill object for each record
				var skill = SkillData.getInstance().getSkill(id, level);
				
				// Add the Skill object to the L2Character skills and its Func objects to the calculator set of the L2Character
				ph.getInstance().addSkill(skill);
			}
		}
		
		return true;
	}
}
