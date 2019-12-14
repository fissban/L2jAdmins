package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.util.UtilPrint;

public class SkillSpellbookData
{
	private static final Logger LOG = Logger.getLogger(SkillSpellbookData.class.getName());
	private static SkillSpellbookData INSTANCE;
	
	private static Map<Integer, Integer> skillSpellbooks;
	
	public static SkillSpellbookData getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new SkillSpellbookData();
		}
		
		return INSTANCE;
	}
	
	private SkillSpellbookData()
	{
		skillSpellbooks = new HashMap<>();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT skill_id, item_id FROM skill_spellbooks");
			ResultSet rs = ps.executeQuery())
		{
			
			while (rs.next())
			{
				skillSpellbooks.put(rs.getInt("skill_id"), rs.getInt("item_id"));
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error while loading spellbook data: " + e);
		}
		
		UtilPrint.result("SkillSpellbookData", "Loaded spell books", skillSpellbooks.size());
	}
	
	public int getBookForSkill(int skillId)
	{
		if (!skillSpellbooks.containsKey(skillId))
		{
			return -1;
		}
		
		return skillSpellbooks.get(skillId);
	}
	
	public int getBookForSkill(Skill skill)
	{
		return getBookForSkill(skill.getId());
	}
}
