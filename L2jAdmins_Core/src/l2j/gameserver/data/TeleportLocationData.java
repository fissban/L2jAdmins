package l2j.gameserver.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.holder.LocationTeleportHolder;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * Load data from teleport.xml
 * @version $Revision: 1.1 $ $Date: 2015/01/21 09:40:24 $
 * @author  Orphus
 */
public class TeleportLocationData extends XmlParser
{
	private final Map<Integer, LocationTeleportHolder> teleports = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/teleports.xml");
		UtilPrint.result("SummonItemsData", "Loaded teleports", teleports.size());
	}
	
	public void reload()
	{
		teleports.clear();
		load();
	}
	
	@Override
	protected void parseFile()
	{
		for (Node n : getNodes("teleport"))
		{
			NamedNodeMap attrs = n.getAttributes();
			
			int id = parseInt(attrs, "id");
			teleports.put(id, new LocationTeleportHolder(id, parseInt(attrs, "x"), parseInt(attrs, "y"), parseInt(attrs, "z"), parseInt(attrs, "price"), parseBoolean(attrs, "forNoble")));
		}
	}
	
	/**
	 * @param  templateId
	 * @return
	 */
	public LocationTeleportHolder getTemplate(int templateId)
	{
		return teleports.get(templateId);
	}
	
	public static TeleportLocationData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportLocationData INSTANCE = new TeleportLocationData();
	}
}
