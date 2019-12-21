package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.EnchantSkillLearnHolder;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.13.2.2.2.8 $ $Date: 2005/04/06 16:13:25 $
 */
public class SkillTreeData
{
	private static final Logger LOG = Logger.getLogger(SkillTreeData.class.getName());
	
	// Query
	private static final String LOAD_CHARACTER_SKILL_TREE = "SELECT class_id, skill_id, level, name, sp, min_level FROM skill_trees WHERE class_id=? ORDER BY skill_id, level";
	private static final String LOAD_FISHING_SKILL_TREE = "SELECT skill_id, level, name, sp, min_level, costid, cost, isfordwarf FROM fishing_skill_trees ORDER BY skill_id, level";
	private static final String LOAD_ENCHANT_SKILL_TREE = "SELECT skill_id, level, name, base_lvl, sp, min_skill_lvl, exp, success_rate76, success_rate77, success_rate78 FROM enchant_skill_trees ORDER BY skill_id, level";
	
	private static final Map<ClassId, Map<Integer, SkillLearnHolder>> skillTrees = new HashMap<>();
	// all common skills (teached by Fisherman)
	private static final List<SkillLearnHolder> fishingSkillTrees = new ArrayList<>();
	// list of special skill for dwarf (expand dwarf craft) learned by class teacher
	private static final List<SkillLearnHolder> expandDwarfCraftSkillTrees = new ArrayList<>();
	// enchant skill list
	private static final Map<Integer, EnchantSkillLearnHolder> enchantSkillTrees = new HashMap<>();
	
	public void load()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			// Load character normal skill tree --------------------------------------------------------
			for (ClassId cid : ClassId.values())
			{
				Map<Integer, SkillLearnHolder> skillsToLearn = new HashMap<>();
				
				try (PreparedStatement ps = con.prepareStatement(LOAD_CHARACTER_SKILL_TREE))
				{
					ps.setInt(1, cid.getId());
					try (ResultSet result = ps.executeQuery())
					{
						while (result.next())
						{
							int id = result.getInt("skill_id");
							int lvl = result.getInt("level");
							String name = result.getString("name");
							int minLvl = result.getInt("min_level");
							int cost = result.getInt("sp");
							
							int hashCode = SkillData.getSkillHashCode(id, lvl);
							SkillLearnHolder learnSkill = new SkillLearnHolder(id, lvl, minLvl, name, cost, 0, 0);
							
							skillsToLearn.put(hashCode, learnSkill);
						}
						
						skillTrees.put(cid, skillsToLearn);
					}
				}
			}
			
			for (ClassId cid : ClassId.values())
			{
				if (cid.getParent() != null)
				{
					skillTrees.get(cid).putAll(skillTrees.get(cid.getParent()));
				}
			}
			
			// Load skill tree for fishing -------------------------------------------------------------
			int count1 = 0;
			int count2 = 0;
			
			try (PreparedStatement ps = con.prepareStatement(LOAD_FISHING_SKILL_TREE);
				ResultSet skilltree2 = ps.executeQuery())
			{
				while (skilltree2.next())
				{
					int id = skilltree2.getInt("skill_id");
					int lvl = skilltree2.getInt("level");
					String name = skilltree2.getString("name");
					int minLvl = skilltree2.getInt("min_level");
					int cost = skilltree2.getInt("sp");
					int costId = skilltree2.getInt("costid");
					int costCount = skilltree2.getInt("cost");
					int isDwarven = skilltree2.getInt("isfordwarf");
					
					SkillLearnHolder skill = new SkillLearnHolder(id, lvl, minLvl, name, cost, costId, costCount);
					
					if (isDwarven == 0)
					{
						fishingSkillTrees.add(skill);
					}
					else
					{
						expandDwarfCraftSkillTrees.add(skill);
					}
				}
				
				count1 = fishingSkillTrees.size();
				count2 = expandDwarfCraftSkillTrees.size();
			}
			catch (Exception e)
			{
				LOG.severe("SkillTreeData: Error while creating fishing skill table: " + e);
			}
			
			// Load enchant skill tree -----------------------------------------------------------------
			int count3 = 0;
			try (PreparedStatement ps = con.prepareStatement(LOAD_ENCHANT_SKILL_TREE);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int id = rs.getInt("skill_id");
					int lvl = rs.getInt("level");
					String name = rs.getString("name");
					int baseLvl = rs.getInt("base_lvl");
					int minSkillLvl = rs.getInt("min_skill_lvl");
					int sp = rs.getInt("sp");
					int exp = rs.getInt("exp");
					byte rate76 = rs.getByte("success_rate76");
					byte rate77 = rs.getByte("success_rate77");
					byte rate78 = rs.getByte("success_rate78");
					
					EnchantSkillLearnHolder skill = new EnchantSkillLearnHolder(id, lvl, minSkillLvl, baseLvl, name, sp, exp, rate76, rate77, rate78);
					
					enchantSkillTrees.put(SkillData.getSkillHashCode(id, lvl), skill);
				}
				
				count3 = enchantSkillTrees.size();
			}
			
			UtilPrint.result("SkillTreeData", "Loaded general skills", count1);
			UtilPrint.result("SkillTreeData", "Loaded fishing & dwarven skills", count2);
			UtilPrint.result("SkillTreeData", "Loaded enchant skills", count3);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Each class receives new skill on certain levels, this methods allow the retrieval of the minimun character level of given class required to learn a given skill
	 * @param  skillId  The iD of the skill
	 * @param  classId  The classId of the character
	 * @param  skillLvl The SkillLvl
	 * @return          The min level
	 */
	public static int getMinSkillLevel(int skillId, ClassId classId, int skillLvl)
	{
		Map<Integer, SkillLearnHolder> map = skillTrees.get(classId);
		
		int skillHashCode = SkillData.getSkillHashCode(skillId, skillLvl);
		
		if (map.containsKey(skillHashCode))
		{
			return map.get(skillHashCode).getMinLevel();
		}
		
		return 0;
	}
	
	public int getMinSkillLevel(int skillId, int skillLvl)
	{
		int skillHashCode = SkillData.getSkillHashCode(skillId, skillLvl);
		
		// Look on all classes for this skill (takes the first one found)
		for (Map<Integer, SkillLearnHolder> map : skillTrees.values())
		{
			// checks if the current class has this skill
			if (map.containsKey(skillHashCode))
			{
				return map.get(skillHashCode).getMinLevel();
			}
		}
		
		return 0;
	}
	
	public static Map<ClassId, Map<Integer, SkillLearnHolder>> getSkillTrees()
	{
		return skillTrees;
	}
	
	/**
	 * Obtenemos un listado de los skills en su maximo lvl q puede aprender un personaje.
	 * @param  cha
	 * @return
	 */
	public static List<SkillLearnHolder> getMaxAvailableSkills(L2PcInstance cha)
	{
		List<SkillLearnHolder> result = new ArrayList<>();
		
		for (SkillLearnHolder skillLearn : skillTrees.get(cha.getClassId()).values())
		{
			if (skillLearn.getMinLevel() <= cha.getLevel())
			{
				boolean found = false;
				
				for (Skill sk : cha.getAllSkills())
				{
					if (sk.getId() == skillLearn.getId())
					{
						if (sk.getLevel() < skillLearn.getLevel())
						{
							// this is the next level of a skill that we know
							result.add(skillLearn);
						}
						
						found = true;
						break;
					}
				}
				
				if (!found)
				{
					// this is a new skill
					result.add(skillLearn);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Enviamos una lista con todos los skills q pueden ser aprendidos mediante los trainers
	 * @param  cha
	 * @param  classId
	 * @return
	 */
	public static List<SkillLearnHolder> getAvailableSkillsTrainer(L2PcInstance cha, ClassId classId)
	{
		List<SkillLearnHolder> result = new ArrayList<>();
		
		for (SkillLearnHolder skillLearn : skillTrees.get(classId).values())
		{
			if (skillLearn.getMinLevel() <= cha.getLevel())
			{
				boolean found = false;
				
				for (Skill sk : cha.getAllSkills())
				{
					if (sk.getId() == skillLearn.getId())
					{
						// this is the next level of a skill that we know
						if (sk.getLevel() == (skillLearn.getLevel() - 1))
						{
							result.add(skillLearn);
						}
						
						found = true;
						break;
					}
				}
				
				if (!found && (skillLearn.getLevel() == 1))
				{
					// this is a new skill
					result.add(skillLearn);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Enviamos una lista de los skills q se pueden aprender mediante los npc de pesca.<br>
	 * <B>Fishing Skill Trees</B><br>
	 * <B>Expand Dwarf Craft Skill Trees</B><br>
	 * @param  cha
	 * @return
	 */
	public static List<SkillLearnHolder> getAvailableSkillsFishing(L2PcInstance cha)
	{
		List<SkillLearnHolder> result = new ArrayList<>();
		List<SkillLearnHolder> skills = new ArrayList<>();
		
		skills.addAll(fishingSkillTrees);
		
		if (cha.hasDwarvenCraft() && (expandDwarfCraftSkillTrees != null))
		{
			skills.addAll(expandDwarfCraftSkillTrees);
		}
		
		for (SkillLearnHolder sl : skills)
		{
			if (sl.getMinLevel() <= cha.getLevel())
			{
				boolean found = false;
				
				for (Skill s : cha.getAllSkills())
				{
					if (s.getId() == sl.getId())
					{
						if (s.getLevel() == (sl.getLevel() - 1))
						{
							result.add(sl);
						}
						
						found = true;
						break;
					}
				}
				
				if (!found && (sl.getLevel() == 1))
				{
					result.add(sl);
				}
			}
		}
		
		return result;
	}
	
	public static List<EnchantSkillLearnHolder> getAvailableEnchantSkills(L2PcInstance cha)
	{
		List<EnchantSkillLearnHolder> result = new ArrayList<>();
		
		for (EnchantSkillLearnHolder skillLearn : enchantSkillTrees.values())
		{
			for (Skill skill : cha.getAllSkills())
			{
				if (skill.getId() == skillLearn.getId())
				{
					if (skill.getLevel() == skillLearn.getMinSkillLevel())
					{
						result.add(skillLearn);
						break;
					}
				}
			}
		}
		
		return result;
	}
	
	public static int getMinLevelForNewSkill(L2PcInstance cha, ClassId classId)
	{
		int minLevel = 0;
		
		for (SkillLearnHolder temp : skillTrees.get(classId).values())
		{
			if ((temp.getMinLevel() > cha.getLevel()) && (temp.getSpCost() != 0))
			{
				if ((minLevel == 0) || (temp.getMinLevel() < minLevel))
				{
					minLevel = temp.getMinLevel();
				}
			}
		}
		
		return minLevel;
	}
	
	public static int getMinLevelForNewSkill(L2PcInstance cha)
	{
		int minLevel = 0;
		List<SkillLearnHolder> skills = new ArrayList<>();
		
		skills.addAll(fishingSkillTrees);
		
		if (cha.hasDwarvenCraft() && (expandDwarfCraftSkillTrees != null))
		{
			skills.addAll(expandDwarfCraftSkillTrees);
		}
		
		for (SkillLearnHolder s : skills)
		{
			if (s.getMinLevel() > cha.getLevel())
			{
				if ((minLevel == 0) || (s.getMinLevel() < minLevel))
				{
					minLevel = s.getMinLevel();
				}
			}
		}
		
		return minLevel;
	}
	
	public static int getSkillCost(L2PcInstance player, Skill skill)
	{
		int skillCost = 100000000;
		ClassId classId = player.getSkillLearningClassId();
		int skillHashCode = SkillData.getSkillHashCode(skill);
		
		if (skillTrees.get(classId).containsKey(skillHashCode))
		{
			SkillLearnHolder skillLearn = skillTrees.get(classId).get(skillHashCode);
			if (skillLearn.getMinLevel() <= player.getLevel())
			{
				skillCost = skillLearn.getSpCost();
			}
		}
		
		return skillCost;
	}
	
	public static int getSkillSpCost(L2PcInstance player, Skill skill)
	{
		int skillCost = 100000000;
		
		for (EnchantSkillLearnHolder enchantSkillLearn : getAvailableEnchantSkills(player))
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			if (player.getLevel() < 76)
			{
				continue;
			}
			
			skillCost = enchantSkillLearn.getSpCost();
		}
		
		return skillCost;
	}
	
	public static int getSkillExpCost(L2PcInstance player, Skill skill)
	{
		int skillCost = 100000000;
		List<EnchantSkillLearnHolder> enchantSkillLearnList = getAvailableEnchantSkills(player);
		
		for (EnchantSkillLearnHolder enchantSkillLearn : enchantSkillLearnList)
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			if (player.getLevel() < 76)
			{
				continue;
			}
			
			skillCost = enchantSkillLearn.getExp();
		}
		
		return skillCost;
	}
	
	public static byte getEnchantSkillRate(L2PcInstance player, Skill skill)
	{
		List<EnchantSkillLearnHolder> enchantSkillLearnList = getAvailableEnchantSkills(player);
		
		for (EnchantSkillLearnHolder enchantSkillLearn : enchantSkillLearnList)
		{
			if (enchantSkillLearn.getId() != skill.getId())
			{
				continue;
			}
			
			if (enchantSkillLearn.getLevel() != skill.getLevel())
			{
				continue;
			}
			
			return enchantSkillLearn.getRate(player);
		}
		return 0;
	}
	
	/**
	 * Returns all allowed skills for a given class.
	 * @param  classId
	 * @return         all allowed skills for a given class.
	 */
	public static Collection<SkillLearnHolder> getAllowedSkills(ClassId classId)
	{
		return skillTrees.get(classId).values();
	}
	
	public static SkillTreeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillTreeData INSTANCE = new SkillTreeData();
	}
}
