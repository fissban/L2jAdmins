package l2j.gameserver.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import l2j.gameserver.scripts.Script;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class ScriptsData extends XmlParser
{
	/** List containing all the Scripts. */
	private static Map<String, Script> scripts = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/scripts.xml");
		UtilPrint.result("RecipeData", "Loaded scripts", scripts.size());
	}
	
	@Override
	protected void parseFile()
	{
		for (var n : getNodes("script"))
		{
			var attrs = n.getAttributes();
			
			var path = parseString(attrs, "path");
			
			try
			{
				add((Script) Class.forName("l2j.gameserver.scripts." + path).getDeclaredConstructor().newInstance());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the script by given quest name.
	 * @param  name : The name of the quest.
	 * @return      Quest : script to be returned, null if script does not exist.
	 */
	public static Script get(String name)
	{
		return scripts.getOrDefault(name, null);
	}
	
	public static Script get(int id)
	{
		return scripts.values().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
	}
	
	/**
	 * Add a new quest.
	 * @param script the quest to be added
	 */
	public static void add(Script script)
	{
		scripts.put(script.getName(), script);
	}
	
	/**
	 * Remove script
	 * @param  script
	 * @return
	 */
	public static boolean remove(Script script)
	{
		if (scripts.containsKey(script.getName()))
		{
			scripts.remove(script.getName());
			return true;
		}
		return false;
	}
	
	/**
	 * Remove all scripts
	 */
	public static void removeAll()
	{
		scripts.clear();
	}
	
	/**
	 * Get all quests
	 * @return
	 */
	public static Collection<Script> getAllQuests()
	{
		return scripts.values().stream().filter(s -> s.isRealQuest()).collect(Collectors.toList());
	}
	
	/**
	 * Get all scripts
	 * @return
	 */
	public static Collection<Script> getAllScripts()
	{
		return scripts.values().stream().filter(s -> !s.isRealQuest()).collect(Collectors.toList());
	}
	
	public static ScriptsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ScriptsData INSTANCE = new ScriptsData();
	}
}
