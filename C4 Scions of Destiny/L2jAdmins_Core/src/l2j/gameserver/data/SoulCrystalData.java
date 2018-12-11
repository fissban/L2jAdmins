package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.holder.LevelingInfoHolder;
import l2j.gameserver.model.holder.SoulCrystalHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * This class loads and stores following Soul Crystal infos :
 * <ul>
 * <li>{@link SoulCrystalHolder} infos related to items (such as level, initial / broken / succeeded itemId) ;</li>
 * <li>{@link LevelingInfoHolder} infos related to NPCs (such as absorb type, chances of fail/success, if the item cast needs to be done and the list of allowed crystal levels).</li>
 * </ul>
 */
public class SoulCrystalData extends XmlParser
{
	private final Map<Integer, SoulCrystalHolder> soulCrystals = new HashMap<>();
	private final Map<Integer, LevelingInfoHolder> levelingInfos = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/soulCrystals.xml");
		UtilPrint.result("SoulCrystalData", "Loaded soul crystals", soulCrystals.size());
		UtilPrint.result("SoulCrystalData", "Loaded npc leveling data", levelingInfos.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("crystals"))
		{
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!"crystal".equalsIgnoreCase(d.getNodeName()))
				{
					continue;
				}
				
				NamedNodeMap attrs = d.getAttributes();
				
				int level = parseInt(attrs, "level");
				int initialItemId = parseInt(attrs, "initial");
				int stagedItemId = parseInt(attrs, "staged");
				int brokenItemId = parseInt(attrs, "broken");
				
				soulCrystals.put(initialItemId, new SoulCrystalHolder(level, initialItemId, stagedItemId, brokenItemId));
			}
		}
		
		for (Node n : getNodes("npcs"))
		{
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!"npc".equalsIgnoreCase(d.getNodeName()))
				{
					continue;
				}
				
				NamedNodeMap attrs = d.getAttributes();
				
				String absorbType = parseString(attrs, "absorbType");
				boolean skill = parseBoolean(attrs, "skill");
				int chanceStage = parseInt(attrs, "chanceStage");
				int chanceBreak = parseInt(attrs, "chanceBreak");
				String levelList = parseString(attrs, "levelList");
				
				levelingInfos.put(parseInt(attrs, "id"), new LevelingInfoHolder(absorbType, skill, chanceStage, chanceBreak, levelList));
			}
		}
	}
	
	public final Map<Integer, SoulCrystalHolder> getSoulCrystals()
	{
		return soulCrystals;
	}
	
	public final Map<Integer, LevelingInfoHolder> getLevelingInfos()
	{
		return levelingInfos;
	}
	
	public static SoulCrystalData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SoulCrystalData INSTANCE = new SoulCrystalData();
	}
}
