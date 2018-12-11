package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.actor.instance.enums.PetItemType;
import l2j.gameserver.model.holder.SummonItemHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author Micr0
 */
public class SummonItemsData extends XmlParser
{
	private final Map<Integer, SummonItemHolder> summonItems = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/SummonData.xml");
		UtilPrint.result("SummonItemsData", "Loaded summon items", summonItems.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("Summon"))
		{
			NamedNodeMap attrs = n.getAttributes();
			String name = parseString(attrs, "name");
			PetItemType type = PetItemType.valueOf(parseString(attrs, "type"));
			int itemId = parseInt(attrs, "itemId");
			int npcId = parseInt(attrs, "npcId");
			summonItems.put(itemId, new SummonItemHolder(name, itemId, npcId, type));
		}
	}
	
	public SummonItemHolder getSummonItem(int itemId)
	{
		return summonItems.get(itemId);
	}
	
	public int[] getSummonItemIds()
	{
		int[] result = new int[summonItems.size()];
		int i = 0;
		for (SummonItemHolder si : summonItems.values())
		{
			result[i] = si.getItemId();
			i++;
		}
		return result;
	}
	
	public static SummonItemsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SummonItemsData INSTANCE = new SummonItemsData();
	}
}
