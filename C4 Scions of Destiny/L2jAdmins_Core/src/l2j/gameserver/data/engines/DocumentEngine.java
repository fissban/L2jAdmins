package l2j.gameserver.data.engines;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.engines.item.DocumentItem;
import l2j.gameserver.data.engines.item.DocumentItemHolder;
import l2j.gameserver.data.engines.skill.DocumentSkill;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemEtcItem;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.util.UtilPrint;

/**
 * @author mkizub
 */
public class DocumentEngine
{
	protected static final Logger LOG = Logger.getLogger(DocumentEngine.class.getName());
	
	private final List<File> armorFiles = new ArrayList<>();
	private final List<File> weaponFiles = new ArrayList<>();
	private final List<File> etcitemFiles = new ArrayList<>();
	private final List<File> skillFiles = new ArrayList<>();
	
	public DocumentEngine()
	{
		hashFiles("data/xml/stats/armor", armorFiles);
		hashFiles("data/xml/stats/weapon", weaponFiles);
		hashFiles("data/xml/stats/skills", skillFiles);
	}
	
	private void hashFiles(String dirname, List<File> hash)
	{
		File dir = new File(Config.DATAPACK_ROOT, dirname);
		
		if (!dir.exists())
		{
			LOG.config("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}
		
		File[] files = dir.listFiles();
		for (File f : files)
		{
			if (f.getName().endsWith(".xml"))
			{
				if (!f.getName().startsWith("custom"))
				{
					hash.add(f);
				}
			}
		}
		File customfile = new File(Config.DATAPACK_ROOT, dirname + "/custom.xml");
		if (customfile.exists())
		{
			hash.add(customfile);
		}
	}
	
	public List<Skill> loadSkills(File file)
	{
		if (file == null)
		{
			LOG.config("Skill file not found.");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}
	
	public void loadAllSkills(Map<Integer, Skill> allSkills)
	{
		int count = 0;
		for (File file : skillFiles)
		{
			List<Skill> s = loadSkills(file);
			if (s == null)
			{
				continue;
			}
			
			for (Skill skill : s)
			{
				allSkills.put(SkillData.getSkillHashCode(skill), skill);
				count++;
			}
		}
		
		UtilPrint.result("SkillData", "Loaded skills", count);
	}
	
	public List<ItemArmor> loadArmors(Map<Integer, DocumentItemHolder> armorData)
	{
		List<ItemArmor> list = new ArrayList<>();
		for (Item item : loadData(armorData, armorFiles))
		{
			list.add((ItemArmor) item);
		}
		
		return list;
	}
	
	public List<ItemWeapon> loadWeapons(Map<Integer, DocumentItemHolder> weaponData)
	{
		List<ItemWeapon> list = new ArrayList<>();
		for (Item item : loadData(weaponData, weaponFiles))
		{
			list.add((ItemWeapon) item);
		}
		
		return list;
	}
	
	public List<ItemEtcItem> loadItems(Map<Integer, DocumentItemHolder> itemData)
	{
		List<ItemEtcItem> list = new ArrayList<>();
		for (Item item : loadData(itemData, etcitemFiles))
		{
			list.add((ItemEtcItem) item);
		}
		
		if (list.isEmpty())
		{
			for (DocumentItemHolder item : itemData.values())
			{
				list.add(new ItemEtcItem((EtcItemType) item.type, item.set));
			}
			
		}
		return list;
	}
	
	public List<Item> loadData(Map<Integer, DocumentItemHolder> itemData, List<File> files)
	{
		List<Item> list = new ArrayList<>();
		for (File f : files)
		{
			DocumentItem document = new DocumentItem(itemData, f);
			document.parse();
			list.addAll(document.getItemList());
		}
		return list;
	}
	
	public static DocumentEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DocumentEngine INSTANCE = new DocumentEngine();
	}
}
