package l2j.gameserver.data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class NpcDrops extends XmlParser
{
	
	@Override
	public void load()
	{
		loadFile("data/xml/npcDrops.xml");
	}
	
	public void reLoad()
	{
		load();
	}
	
	@Override
	protected void parseFile()
	{
		var count = 0;
		NamedNodeMap attrs = null;
		
		for (Node n : getNodes("drop"))
		{
			attrs = n.getAttributes();
			
			var npcId = parseInt(attrs, "npcId");
			
			var template = NpcData.getInstance().getTemplate(npcId);
			
			var isBoss = template.getType().equalsIgnoreCase("L2RaidBoss") || template.getType().equalsIgnoreCase("L2GrandBoss");
			
			// read category's
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				if (c.getNodeName().equals("category"))
				{
					attrs = c.getAttributes();
					
					var categoryId = parseInt(attrs, "id");
					var categoryChance = parseDouble(attrs, "chance");
					// new category
					var category = new DropCategory(categoryId, categoryChance, isBoss);
					
					// read item's
					for (Node d = c.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equals("item"))
						{
							attrs = d.getAttributes();
							var itemId = parseInt(attrs, "id");
							var min = parseInt(attrs, "min");
							var max = parseInt(attrs, "max");
							var chance = parseDouble(attrs, "chance");
							
							// new drop
							var drop = new DropInstance(itemId, min, max, chance);
							// add drop in category
							category.addDrop(drop, isBoss);
							
							count++;
						}
					}
					
					category.balancedDrops();
					
					template.addDropCategory(category);
				}
			}
		}
		
		UtilPrint.result("NpcDrops", "Loaded npc drops", count);
	}
	
	public static NpcDrops getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcDrops INSTANCE = new NpcDrops();
	}
}
