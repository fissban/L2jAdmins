package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.templates.PcTemplate;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class CharTemplateData extends XmlParser
{
	private static final Map<Integer, PcTemplate> charTemplates = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/stats/charTemplates.xml");
		UtilPrint.result("CharNameData", "Loaded char templates", charTemplates.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("class"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			StatsSet set = new StatsSet();
			set.set("id", attrs.getNamedItem("id").getNodeValue());
			set.set("name", attrs.getNamedItem("name").getNodeValue());
			set.set("race", attrs.getNamedItem("race").getNodeValue());
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				if (c.getNodeName().equals("set"))
				{
					attrs = c.getAttributes();
					String name = attrs.getNamedItem("name").getNodeValue();
					String value = attrs.getNamedItem("val").getNodeValue();
					set.set(name, value);
				}
			}
			
			charTemplates.put(set.getInteger("id", 0), new PcTemplate(set));
		}
	}
	
	public PcTemplate getTemplate(ClassId classId)
	{
		return getTemplate(classId.getId());
	}
	
	public PcTemplate getTemplate(int classId)
	{
		return charTemplates.get(classId);
	}
	
	public static CharTemplateData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CharTemplateData INSTANCE = new CharTemplateData();
	}
}
