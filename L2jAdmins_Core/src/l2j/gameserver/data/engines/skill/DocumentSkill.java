package l2j.gameserver.data.engines.skill;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.data.engines.AbstractDocumentBase;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.conditions.Condition;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;

/**
 * @author mkizub
 */
public final class DocumentSkill extends AbstractDocumentBase
{
	private DocumentSkillHolder currentSkill;
	private final List<Skill> skillsInFile = new ArrayList<>();
	
	public DocumentSkill(File file)
	{
		super(file);
	}
	
	private void setCurrentSkill(DocumentSkillHolder skill)
	{
		currentSkill = skill;
	}
	
	@Override
	protected StatsSet getStatsSet()
	{
		return currentSkill.sets[currentSkill.currentLevel];
	}
	
	public List<Skill> getSkills()
	{
		return skillsInFile;
	}
	
	@Override
	protected String getTableValue(String name)
	{
		try
		{
			return tables.get(name)[currentSkill.currentLevel];
		}
		catch (RuntimeException e)
		{
			LOG.log(Level.SEVERE, "error in table: " + name + " of skill Id " + currentSkill.id, e);
			return "0";
		}
	}
	
	@Override
	protected String getTableValue(String name, int idx)
	{
		try
		{
			return tables.get(name)[idx - 1];
		}
		catch (RuntimeException e)
		{
			LOG.log(Level.SEVERE, "wrong level count in skill Id " + currentSkill.id, e);
			return "0";
		}
	}
	
	@Override
	protected void parseDocument(Document doc)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("skill".equalsIgnoreCase(d.getNodeName()))
					{
						setCurrentSkill(new DocumentSkillHolder());
						parseSkill(d);
						skillsInFile.addAll(currentSkill.skills);
						resetTable();
					}
				}
			}
			else if ("skill".equalsIgnoreCase(n.getNodeName()))
			{
				setCurrentSkill(new DocumentSkillHolder());
				parseSkill(n);
				skillsInFile.addAll(currentSkill.skills);
			}
		}
	}
	
	protected void parseSkill(Node n)
	{
		NamedNodeMap attrs = n.getAttributes();
		int enchantLevels1 = 0;
		int enchantLevels2 = 0;
		int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
		String skillName = attrs.getNamedItem("name").getNodeValue();
		String levels = attrs.getNamedItem("levels").getNodeValue();
		int lastLvl = Integer.parseInt(levels);
		
		if (attrs.getNamedItem("enchantLevels1") != null)
		{
			enchantLevels1 = Integer.parseInt(attrs.getNamedItem("enchantLevels1").getNodeValue());
		}
		if (attrs.getNamedItem("enchantLevels2") != null)
		{
			enchantLevels2 = Integer.parseInt(attrs.getNamedItem("enchantLevels2").getNodeValue());
		}
		
		currentSkill.id = skillId;
		currentSkill.name = skillName;
		currentSkill.sets = new StatsSet[lastLvl];
		currentSkill.enchsets1 = new StatsSet[enchantLevels1];
		currentSkill.enchsets2 = new StatsSet[enchantLevels2];
		
		for (int i = 0; i < lastLvl; i++)
		{
			currentSkill.sets[i] = new StatsSet();
			currentSkill.sets[i].set("skill_id", currentSkill.id);
			currentSkill.sets[i].set("level", i + 1);
			currentSkill.sets[i].set("name", currentSkill.name);
		}
		
		if (currentSkill.sets.length != lastLvl)
		{
			throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + lastLvl + " levels expected");
		}
		
		Node first = n.getFirstChild();
		for (n = first; n != null; n = n.getNextSibling())
		{
			if ("table".equalsIgnoreCase(n.getNodeName()))
			{
				parseTable(n);
			}
		}
		
		for (int i = 1; i <= lastLvl; i++)
		{
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("set".equalsIgnoreCase(n.getNodeName()))
				{
					parseBeanSet(n, currentSkill.sets[i - 1], i);
				}
			}
		}
		
		for (int i = 0; i < enchantLevels1; i++)
		{
			currentSkill.enchsets1[i] = new StatsSet();
			currentSkill.enchsets1[i].set("skill_id", currentSkill.id);
			
			currentSkill.enchsets1[i].set("level", i + 101);
			currentSkill.enchsets1[i].set("name", currentSkill.name);
			
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("set".equalsIgnoreCase(n.getNodeName()))
				{
					parseBeanSet(n, currentSkill.enchsets1[i], currentSkill.sets.length);
				}
			}
			
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("enchant1".equalsIgnoreCase(n.getNodeName()))
				{
					parseBeanSet(n, currentSkill.enchsets1[i], i + 1);
				}
			}
		}
		
		if (currentSkill.enchsets1.length != enchantLevels1)
		{
			throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + enchantLevels1 + " levels expected");
		}
		
		for (int i = 0; i < enchantLevels2; i++)
		{
			currentSkill.enchsets2[i] = new StatsSet();
			
			currentSkill.enchsets2[i].set("skill_id", currentSkill.id);
			currentSkill.enchsets2[i].set("level", i + 141);
			currentSkill.enchsets2[i].set("name", currentSkill.name);
			
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("set".equalsIgnoreCase(n.getNodeName()))
				{
					parseBeanSet(n, currentSkill.enchsets2[i], currentSkill.sets.length);
				}
			}
			
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("enchant2".equalsIgnoreCase(n.getNodeName()))
				{
					parseBeanSet(n, currentSkill.enchsets2[i], i + 1);
				}
			}
		}
		
		if (currentSkill.enchsets2.length != enchantLevels2)
		{
			throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + enchantLevels2 + " levels expected");
		}
		
		makeSkills();
		
		for (int i = 0; i < lastLvl; i++)
		{
			currentSkill.currentLevel = i;
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("cond".equalsIgnoreCase(n.getNodeName()))
				{
					Condition condition = parseCondition(n.getFirstChild(), currentSkill.currentSkills.get(i));
					Node msg = n.getAttributes().getNamedItem("msg");
					
					if ((condition != null) && (msg != null))
					{
						condition.setMessage(msg.getNodeValue());
					}
					
					currentSkill.currentSkills.get(i).attach(condition, false);
				}
				
				if ("for".equalsIgnoreCase(n.getNodeName()))
				{
					parseTemplate(n, currentSkill.currentSkills.get(i));
				}
			}
		}
		
		for (int i = lastLvl; i < (lastLvl + enchantLevels1); i++)
		{
			currentSkill.currentLevel = i - lastLvl;
			boolean found = false;
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("enchant1cond".equalsIgnoreCase(n.getNodeName()))
				{
					found = true;
					Condition condition = parseCondition(n.getFirstChild(), currentSkill.currentSkills.get(i));
					Node msg = n.getAttributes().getNamedItem("msg");
					if ((condition != null) && (msg != null))
					{
						condition.setMessage(msg.getNodeValue());
					}
					currentSkill.currentSkills.get(i).attach(condition, false);
				}
				
				if ("enchant1for".equalsIgnoreCase(n.getNodeName()))
				{
					found = true;
					parseTemplate(n, currentSkill.currentSkills.get(i));
				}
			}
			
			// If none found, the enchanted skill will take effects from maxLvL of norm skill
			if (!found)
			{
				currentSkill.currentLevel = lastLvl - 1;
				for (n = first; n != null; n = n.getNextSibling())
				{
					if ("cond".equalsIgnoreCase(n.getNodeName()))
					{
						Condition condition = parseCondition(n.getFirstChild(), currentSkill.currentSkills.get(i));
						Node msg = n.getAttributes().getNamedItem("msg");
						if ((condition != null) && (msg != null))
						{
							condition.setMessage(msg.getNodeValue());
						}
						currentSkill.currentSkills.get(i).attach(condition, false);
					}
					
					if ("for".equalsIgnoreCase(n.getNodeName()))
					{
						parseTemplate(n, currentSkill.currentSkills.get(i));
					}
				}
			}
		}
		
		for (int i = lastLvl + enchantLevels1; i < (lastLvl + enchantLevels1 + enchantLevels2); i++)
		{
			boolean found = false;
			currentSkill.currentLevel = i - lastLvl - enchantLevels1;
			for (n = first; n != null; n = n.getNextSibling())
			{
				if ("enchant2cond".equalsIgnoreCase(n.getNodeName()))
				{
					found = true;
					Condition condition = parseCondition(n.getFirstChild(), currentSkill.currentSkills.get(i));
					Node msg = n.getAttributes().getNamedItem("msg");
					if ((condition != null) && (msg != null))
					{
						condition.setMessage(msg.getNodeValue());
					}
					currentSkill.currentSkills.get(i).attach(condition, false);
				}
				
				if ("enchant2for".equalsIgnoreCase(n.getNodeName()))
				{
					found = true;
					parseTemplate(n, currentSkill.currentSkills.get(i));
				}
			}
			
			// If none found, the enchanted skill will take effects from maxLvL of normal skill
			if (!found)
			{
				currentSkill.currentLevel = lastLvl - 1;
				for (n = first; n != null; n = n.getNextSibling())
				{
					if ("cond".equalsIgnoreCase(n.getNodeName()))
					{
						Condition condition = parseCondition(n.getFirstChild(), currentSkill.currentSkills.get(i));
						Node msg = n.getAttributes().getNamedItem("msg");
						if ((condition != null) && (msg != null))
						{
							condition.setMessage(msg.getNodeValue());
						}
						currentSkill.currentSkills.get(i).attach(condition, false);
					}
					
					if ("for".equalsIgnoreCase(n.getNodeName()))
					{
						parseTemplate(n, currentSkill.currentSkills.get(i));
					}
				}
			}
		}
		currentSkill.skills.addAll(currentSkill.currentSkills);
	}
	
	private void makeSkills()
	{
		int count = 0;
		currentSkill.currentSkills = new ArrayList<>(currentSkill.sets.length + currentSkill.enchsets1.length + currentSkill.enchsets2.length);
		
		for (int i = 0; i < currentSkill.sets.length; i++)
		{
			try
			{
				currentSkill.currentSkills.add(i, currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.sets[i]));
				count++;
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "Skill id=" + currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.sets[i]).getId() + "level" + currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.sets[i]).getLevel());
			}
		}
		
		int c = count;
		for (int i = 0; i < currentSkill.enchsets1.length; i++)
		{
			try
			{
				currentSkill.currentSkills.add(c + i, currentSkill.enchsets1[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets1[i]));
				count++;
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "Skill id=" + currentSkill.enchsets1[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets1[i]).getId() + " level=" + currentSkill.enchsets1[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets1[i]).getLevel());
			}
		}
		
		c = count;
		for (int i = 0; i < currentSkill.enchsets2.length; i++)
		{
			try
			{
				currentSkill.currentSkills.add(c + i, currentSkill.enchsets2[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets2[i]));
				count++;
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "Skill id=" + currentSkill.enchsets2[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets2[i]).getId() + " level=" + currentSkill.enchsets2[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.enchsets2[i]).getLevel());
			}
		}
	}
}
