package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.enums.NpcRaceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.MinionHolder;
import l2j.util.UtilPrint;

/**
 * @author missing, fissban
 */
public class NpcData
{
	// Log
	private static final Logger LOG = Logger.getLogger(NpcData.class.getName());
	// Querys
	private static final String NPC_QUERY = "SELECT `id`, `idTemplate`, `name`, `title`, `class`, `collision_radius`, `collision_height`, `level`, `type`, `attackrange`, `hp`, `mp`, `hpreg`, `mpreg`, `str`, `con`, `dex`, `int`, `wit`, `men`, `exp`, `sp`, `patk`, `pdef`, `matk`, `mdef`, `atkspd`, `aggro`, `matkspd`, `rhand`, `lhand`, `walkspd`, `runspd`, `faction_id`, `faction_range`, `ss`, `bss`, `ss_rate` FROM npc";
	private static final String NPC_CUSTOM_QUERY = "SELECT `id`, `idTemplate`, `name`, `title`, `class`, `collision_radius`, `collision_height`, `level`, `type`, `attackrange`, `hp`, `mp`, `hpreg`, `mpreg`, `str`, `con`, `dex`, `int`, `wit`, `men`, `exp`, `sp`, `patk`, `pdef`, `matk`, `mdef`, `atkspd`, `aggro`, `matkspd`, `rhand`, `lhand`, `walkspd`, `runspd`, `faction_id`, `faction_range`, `ss`, `bss`, `ss_rate` FROM custom_npc";
	
	private static final String NPC_QUERY_SKILLS = "SELECT npcid,skillid,level FROM npc_skills";
	private static final String NPC_CUSTOM_QUERY_SKILLS = "SELECT npcid,skillid,level FROM custom_npc_skills";
	
	// Instances
	private final Map<Integer, NpcTemplate> npcs = new HashMap<>();
	
	public NpcData()
	{
		readTable();
	}
	
	public void load()
	{
		readTable();
	}
	
	public void reload()
	{
		readTable();
	}
	
	private void readTable()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			// Load npcs -------------------------------------------------------
			loadNpcs(con, NPC_QUERY);
			
			if (Config.CUSTOM_NPC_TABLE)
			{
				loadNpcs(con, NPC_CUSTOM_QUERY);
			}
			
			// Load npcs skills ------------------------------------------------
			loadNpcSkills(con, NPC_QUERY_SKILLS);
			
			if (Config.CUSTOM_NPC_SKILLS_TABLE)
			{
				loadNpcSkills(con, NPC_CUSTOM_QUERY_SKILLS);
			}
			
			// Load skill learns -----------------------------------------------
			loadSkillLearn(con);
			
			// Load minions from boss ------------------------------------------
			loadMinions(con);
		}
		catch (Exception e)
		{
			LOG.severe("NpcData: Error creating NPC table: " + e);
			e.printStackTrace();
		}
		
		UtilPrint.result("NpcData", "Loaded npc template", npcs.size());
	}
	
	private void loadNpcs(Connection con, String query) throws Exception
	{
		try (PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final StatsSet npcDat = new StatsSet();
				final int id = rs.getInt("id");
				
				assert id < 1000000;
				
				npcDat.set("npcId", id);
				npcDat.set("idTemplate", rs.getInt("idTemplate"));
				final int level = rs.getInt("level");
				npcDat.set("level", level);
				npcDat.set("jClass", rs.getString("class"));
				
				npcDat.set("baseShldDef", 0);
				npcDat.set("baseShldRate", 0);
				npcDat.set("baseCritRate", 38);
				
				npcDat.set("name", rs.getString("name"));
				npcDat.set("title", rs.getString("title"));
				npcDat.set("collisionRadius", rs.getDouble("collision_radius"));
				npcDat.set("collisionHeight", rs.getDouble("collision_height"));
				npcDat.set("type", rs.getString("type"));
				npcDat.set("atkRange", rs.getInt("attackrange"));
				npcDat.set("rewardExp", rs.getInt("exp"));
				npcDat.set("rewardSp", rs.getInt("sp"));
				npcDat.set("pAtkSpd", rs.getInt("atkspd"));
				npcDat.set("mAtkSpd", rs.getInt("matkspd"));
				npcDat.set("aggroRange", rs.getInt("aggro"));
				npcDat.set("rhand", rs.getInt("rhand"));
				npcDat.set("lhand", rs.getInt("lhand"));
				npcDat.set("walkSpd", rs.getInt("walkspd"));
				npcDat.set("runSpd", rs.getInt("runspd"));
				
				// constants, until we have stats in DB
				npcDat.set("str", rs.getInt("str"));
				npcDat.set("con", rs.getInt("con"));
				npcDat.set("dex", rs.getInt("dex"));
				npcDat.set("int", rs.getInt("int"));
				npcDat.set("wit", rs.getInt("wit"));
				npcDat.set("men", rs.getInt("men"));
				
				npcDat.set("hpBase", rs.getFloat("hp"));
				npcDat.set("cpBase", 0);
				npcDat.set("mpBase", rs.getInt("mp"));
				npcDat.set("hpReg", rs.getFloat("hpreg") > 0 ? rs.getFloat("hpreg") : 1.5 + ((level - 1) / 10));
				npcDat.set("mpReg", rs.getFloat("mpreg") > 0 ? rs.getFloat("mpreg") : 0.9 + (0.3 * ((level - 1) / 10)));
				npcDat.set("pAtk", rs.getFloat("patk"));
				npcDat.set("pDef", rs.getFloat("pdef"));
				npcDat.set("mAtk", rs.getFloat("matk"));
				npcDat.set("mDef", rs.getFloat("mdef"));
				
				npcDat.set("factionId", rs.getString("faction_id"));
				npcDat.set("factionRange", rs.getInt("faction_range"));
				
				npcDat.set("ss", rs.getInt("ss"));
				npcDat.set("bss", rs.getInt("bss"));
				npcDat.set("ssRate", rs.getInt("ss_rate"));
				
				npcs.put(id, new NpcTemplate(npcDat));
			}
		}
	}
	
	private void loadNpcSkills(Connection con, String query)
	{
		try (PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				int mobId = rs.getInt("npcid");
				NpcTemplate template = npcs.get(mobId);
				
				if (template == null)
				{
					LOG.severe("NpcData: Error reading NPC skill (template) npcId: " + mobId);
					continue;
				}
				
				final int id = rs.getInt("skillid");
				final int level = rs.getInt("level");
				
				if (mobId == 12899)
				{
					template.setRace(NpcRaceType.CREATURE_VALAKAS);
				}
				else if (template.getRace() == NpcRaceType.NONE)
				{
					if ((id >= 4290) && (id <= 4302))
					{
						switch (id)
						{
							case 4290:
								template.setRace(NpcRaceType.UNDEAD);
								break;
							case 4292:
								template.setRace(NpcRaceType.BEAST);
								break;
							case 4293:
								template.setRace(NpcRaceType.ANIMAL);
								break;
							case 4294:
								template.setRace(NpcRaceType.PLANT);
								break;
							case 4299:
								template.setRace(NpcRaceType.DRAGON);
								break;
							case 4300:
								template.setRace(NpcRaceType.GIANT);
								break;
							case 4301:
								template.setRace(NpcRaceType.BUG);
								break;
							case 4302:
								template.setRace(NpcRaceType.MAGIC_CREATURE);
								break;
						}
					}
				}
				
				Skill npcSkill = SkillData.getInstance().getSkill(id, level);
				if (npcSkill == null)
				{
					LOG.severe("NpcData: Error reading NPC skill -> npcId: " + mobId + " skillId: " + id + " level: " + level);
					continue;
				}
				
				template.addSkill(npcSkill);
			}
		}
		catch (Exception e)
		{
			LOG.severe("NpcData: Error reading NPC skills table: " + e);
		}
	}
	
	private void loadSkillLearn(Connection con)
	{
		try (PreparedStatement ps = con.prepareStatement("SELECT npc_id, class_id FROM skill_learn");
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int npcId = rs.getInt("npc_id");
				final int classId = rs.getInt("class_id");
				
				final NpcTemplate npc = getTemplate(npcId);
				if (npc == null)
				{
					LOG.warning("NpcData: Error getting NPC template ID " + npcId + " while trying to load skill trainer data.");
					continue;
				}
				
				npc.addTeachInfo(ClassId.getById(classId));
			}
		}
		catch (Exception e)
		{
			LOG.severe("NpcData: Error reading NPC trainer data: " + e);
		}
	}
	
	private void loadMinions(Connection con)
	{
		try (PreparedStatement ps = con.prepareStatement("SELECT boss_id, minion_id, amount_min, amount_max FROM npc_minions");
			ResultSet rs = ps.executeQuery())
		{
			int cnt = 0;
			
			while (rs.next())
			{
				npcs.get(rs.getInt("boss_id")).addMinion(new MinionHolder(rs.getInt("minion_id"), rs.getInt("amount_min"), rs.getInt("amount_max")));
				cnt++;
			}
			
			UtilPrint.result("NpcData", "Loaded minions", cnt);
		}
		catch (Exception e)
		{
			LOG.severe("Error loading minion data: " + e);
		}
	}
	
	public void saveNpc(StatsSet npc)
	{
		final Map<String, Object> set = npc.getSet();
		
		String name = "";
		String values = "";
		
		for (Object obj : set.keySet())
		{
			name = (String) obj;
			
			if (name.equalsIgnoreCase("npcId"))
			{
				continue;
			}
			
			if (!values.isEmpty())
			{
				values += ", ";
			}
			
			values += name + " = '" + set.get(name) + "'";
		}
		
		int updated = 0;
		try (Connection con = DatabaseManager.getConnection())
		{
			if (Config.CUSTOM_NPC_TABLE)
			{
				try (PreparedStatement ps = con.prepareStatement("UPDATE custom_npc SET " + values + " WHERE id=?"))
				{
					ps.setInt(1, npc.getInteger("npcId"));
					updated = ps.executeUpdate();
				}
			}
			
			if (updated == 0)
			{
				try (PreparedStatement ps = con.prepareStatement("UPDATE npc SET " + values + " WHERE id=?"))
				{
					ps.setInt(1, npc.getInteger("npcId"));
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("NpcData: Could not store new NPC data in database: " + e);
		}
	}
	
	public void replaceTemplate(NpcTemplate npc)
	{
		npcs.put(npc.getId(), npc);
	}
	
	public NpcTemplate getTemplate(int id)
	{
		return npcs.get(id);
	}
	
	/**
	 * All the templates of the npc of a certain name.
	 * @param  name
	 * @return
	 */
	public NpcTemplate getTemplateByName(String name)
	{
		return npcs.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * All the templates of the npc of a certain classType.
	 * @param  classTypes to search.
	 * @return            the template list of NPCs for a given class.
	 */
	public List<NpcTemplate> getAllNpcOfClassType(String classTypes)
	{
		return npcs.values().stream().filter(t -> t.isType(classTypes)).collect(Collectors.toList());
	}
	
	/**
	 * All the templates of the npc of a certain level.
	 * @param  lvl
	 * @return
	 */
	public List<NpcTemplate> getAllOfLevel(int lvl)
	{
		return npcs.values().stream().filter(t -> t.getLevel() == lvl).collect(Collectors.toList());
	}
	
	public static NpcData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcData INSTANCE = new NpcData();
	}
}
