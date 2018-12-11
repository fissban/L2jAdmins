package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.Config;
import l2j.gameserver.model.holder.ArmorSetHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class ArmorSetsData extends XmlParser
{
	private final Map<Integer, ArmorSetHolder> armorSets = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/stats/armorSets.xml");
		UtilPrint.result("ArmorSetsData", "Loaded armor sets", armorSets.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("armorsets"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			int chest = 0;
			int legs = 0;
			int head = 0;
			int gloves = 0;
			int feet = 0;
			int skill_id = 0;
			int shield_id = 0;
			int shield_skill_id = 0;
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				attrs = c.getAttributes();
				switch (c.getNodeName())
				{
					case "chest":
						chest = parseInt(attrs, "id");
						break;
					case "legs":
						legs = parseInt(attrs, "id");
						break;
					case "head":
						head = parseInt(attrs, "id");
						break;
					case "gloves":
						gloves = parseInt(attrs, "id");
						break;
					case "feet":
						feet = parseInt(attrs, "id");
						break;
					case "skill":
						skill_id = parseInt(attrs, "id");
						break;
					case "shield":
						shield_id = parseInt(attrs, "id");
						break;
					case "shield_skill":
						shield_skill_id = parseInt(attrs, "id");
						break;
				}
			}
			
			if (Config.DEBUG)
			{
				LOG.info(getClass().getSimpleName() + " load: armorSets ID: " + chest + "chest:" + chest + " legs:" + legs + " head:" + head + " gloves:" + gloves + " feet:" + feet + " skillId:" + skill_id + " shield:" + shield_id + " shield_skill:" + shield_skill_id);
			}
			
			armorSets.put(chest, new ArmorSetHolder(chest, legs, head, gloves, feet, skill_id, shield_id, shield_skill_id));
		}
	}
	
	public ArmorSetHolder getArmorSets(int chestId)
	{
		return armorSets.get(chestId);
	}
	
	public static ArmorSetsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ArmorSetsData INSTANCE = new ArmorSetsData();
	}
}
