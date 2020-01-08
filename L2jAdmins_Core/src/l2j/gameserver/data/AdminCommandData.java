package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.List;

import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class AdminCommandData extends XmlParser
{
	private static final List<AdminCommand> adminCommandAccessRights = new ArrayList<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/adminCommands.xml");
		UtilPrint.result("AdminCommandData", "Loaded admin acces commands", adminCommandAccessRights.size());
	}
	
	public void reLoad()
	{
		adminCommandAccessRights.clear();
		load();
	}
	
	@Override
	protected void parseFile()
	{
		for (var n : getNodes("admin"))
		{
			var attrs = n.getAttributes();
			
			adminCommandAccessRights.add(new AdminCommand(parseString(attrs, "command"), parseInt(attrs, "accessLevel")));
		}
	}
	
	/**
	 * Obtenemos el accesLevel requerido para usar un comando en especial.<br>
	 * En caso de q el comando no exista dentro de adminCommands.xml devolvera 1 y se imprimira un log
	 * @param  command
	 * @return
	 */
	public int getAccesCommandAdmin(String command)
	{
		for (var admin : adminCommandAccessRights)
		{
			if (admin.getComandName().equals(command))
			{
				return admin.getCommandLevel();
			}
		}
		
		LOG.warn("AdminCommandTable: missing AdminCommandHandler < " + command + " > in adminCommands.xml");
		return 1;
	}
	
	private class AdminCommand
	{
		private final String name;
		private final int level;
		
		public AdminCommand(String commandName, int commandLevel)
		{
			name = commandName;
			level = commandLevel;
		}
		
		public String getComandName()
		{
			return name;
		}
		
		public int getCommandLevel()
		{
			return level;
		}
	}
	
	public static AdminCommandData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandData INSTANCE = new AdminCommandData();
	}
}
