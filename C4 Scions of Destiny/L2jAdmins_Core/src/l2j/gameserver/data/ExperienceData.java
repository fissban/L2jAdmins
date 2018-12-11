package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class ExperienceData extends XmlParser
{
	private final Map<Integer, Long> expTable = new HashMap<>();
	private byte maxLevel;
	
	@Override
	public void load()
	{
		loadFile("data/xml/stats/experience.xml");
		
		UtilPrint.result("ExperienceData", "Loaded levels ", expTable.size());
		UtilPrint.result("ExperienceData", "Loaded max player level ", maxLevel - 1);
	}
	
	@Override
	protected void parseFile()
	{
		maxLevel = (Byte.parseByte(getCurrentDocument().getFirstChild().getAttributes().getNamedItem("maxLevel").getNodeValue()));
		
		for (Node n : getNodes("experience"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			expTable.put(parseInt(attrs, "level"), parseLong(attrs, "toLevel"));
		}
	}
	
	/**
	 * Gets the exp for level.
	 * @param  level the level required.
	 * @return       the experience points required to reach the given level.
	 */
	public long getExpForLevel(int level)
	{
		return expTable.get(level);
	}
	
	/**
	 * Gets the max level.
	 * @return the maximum level acquirable by a player.
	 */
	public byte getMaxLevel()
	{
		return maxLevel;
	}
	
	public static ExperienceData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ExperienceData INSTANCE = new ExperienceData();
	}
}
