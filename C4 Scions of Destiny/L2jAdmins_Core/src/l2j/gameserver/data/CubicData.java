package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.holder.CubicHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class CubicData extends XmlParser
{
	private static final Map<CubicType, CubicHolder> cubics = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/cubics.xml");
		UtilPrint.result("CubicData", "Loaded cubics ", cubics.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("cubic"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			CubicType type = CubicType.valueOf(attrs.getNamedItem("type").getNodeValue());
			
			List<Integer> skillsId = new ArrayList<>();
			int chance = 0;
			int delayAction = 0;
			int disappearTime = 0;
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				attrs = c.getAttributes();
				
				switch (c.getNodeName())
				{
					case "chanceAction":
						chance = parseInt(attrs, "val");
						break;
					case "delayAction":
						delayAction = parseInt(attrs, "val");
						break;
					case "delayDisappear":
						disappearTime = parseInt(attrs, "val");
						break;
					case "skills":
						for (String skill : parseString(attrs, "val").split(","))
						{
							skillsId.add(Integer.parseInt(skill));
						}
						break;
				}
			}
			
			cubics.put(type, new CubicHolder(type, chance, delayAction, disappearTime, skillsId));
		}
	}
	
	public CubicHolder getCubicByType(CubicType type)
	{
		return cubics.get(type);
	}
	
	public static CubicData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CubicData INSTANCE = new CubicData();
	}
}
