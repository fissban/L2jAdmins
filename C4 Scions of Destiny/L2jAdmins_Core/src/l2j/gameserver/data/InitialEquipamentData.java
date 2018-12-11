package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class InitialEquipamentData extends XmlParser
{
	private static final Map<Integer, List<Integer>> initialEquipament = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/initialEquipament.xml");
		UtilPrint.result("InitialEquipamentData", "Loaded initial equipament", initialEquipament.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("class"))
		{
			int classId = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
			List<Integer> items = new ArrayList<>();
			
			String[] itemsIds = n.getAttributes().getNamedItem("items").getNodeValue().split(";");
			
			for (String id : itemsIds)
			{
				items.add(Integer.parseInt(id));
			}
			
			initialEquipament.put(classId, items);
		}
	}
	
	/**
	 * We get the items they get the characters at birth by Id.
	 * @param  classId
	 * @return         Integer[]
	 */
	public List<Integer> getItemsById(int classId)
	{
		return initialEquipament.get(classId);
	}
	
	public static InitialEquipamentData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final InitialEquipamentData INSTANCE = new InitialEquipamentData();
	}
}
