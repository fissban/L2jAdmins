package l2j.loginserver.datatable;

import java.util.Map;
import java.util.TreeMap;

import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class ServerNameTable extends XmlParser
{
	private final Map<Integer, String> serverNames = new TreeMap<>();
	
	@Override
	public void load()
	{
		loadFile("../game/data/xml/serverName.xml");
		LOG.info(getClass().getSimpleName() + ": Loaded: " + serverNames.size() + " server names");
	}
	
	@Override
	protected void parseFile()
	{
		for (var n : getNodes("server"))
		{
			var attrs = n.getAttributes();
			
			serverNames.put(parseInt(attrs, "id"), parseString(attrs, "name"));
		}
	}
	
	/**
	 * Se obtiene el nombre de un server segun su Id
	 * @param  id
	 * @return    String
	 */
	public String getServerName(int id)
	{
		return serverNames.get(id);
	}
	
	/**
	 * Se obtiene la lista completa de todos los servers
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> getServers()
	{
		return serverNames;
	}
	
	public static ServerNameTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerNameTable INSTANCE = new ServerNameTable();
	}
	
}
