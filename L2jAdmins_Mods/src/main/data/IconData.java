package main.data;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.util.Rnd;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;
import main.enums.ItemIconType;
import main.util.builders.html.Icon;

/**
 * @author fissban
 */
public class IconData extends XmlParser
{
	protected static final Logger LOG = Logger.getLogger(IconData.class.getName());
	
	private final static Map<Integer, String> items = new HashMap<>();
	
	@Override
	public void load()
	{
		// Duplicate data is prevented if this method is reloaded
		items.clear();
		loadFile("data/xml/engine/icons.xml");
		UtilPrint.result("IconData", "Loaded icons", items.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (var n : getNodes("item"))
		{
			var attrs = n.getAttributes();
			
			var id = parseInt(attrs, "id");
			var icon = parseString(attrs, "icon");
			items.put(id, icon);
		}
	}
	
	/**
	 * You get an icon of a specific item id
	 * @param  itemId
	 * @return
	 */
	public static String getIconByItemId(int itemId)
	{
		return items.get(itemId);
	}
	
	/**
	 * You get an icon of a random type of a specific type of item
	 * @param  itemIconType
	 * @param  rnd
	 * @return
	 */
	public static String getRandomItemType(ItemIconType itemIconType, int rnd)
	{
		String returnIcon = "";
		
		while (returnIcon.equals(""))
		{
			for (var icon : items.values())
			{
				if (icon.startsWith(itemIconType.getSearchItem()))
				{
					// it leaves aside the icons that can generate confusion.
					if ((Rnd.get(rnd) == 0) && !icon.equals(Icon.noimage) && !icon.equals(Icon.weapon_monster_i00))
					{
						returnIcon = icon;
					}
				}
			}
		}
		return returnIcon;
	}
	
	public static IconData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IconData INSTANCE = new IconData();
	}
}
