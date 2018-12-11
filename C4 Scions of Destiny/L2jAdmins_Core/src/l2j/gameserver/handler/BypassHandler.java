package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.bypass.BypassCPRecovery;
import l2j.gameserver.handler.bypass.BypassChat;
import l2j.gameserver.handler.bypass.BypassEnterRift;
import l2j.gameserver.handler.bypass.BypassLink;
import l2j.gameserver.handler.bypass.BypassMultisell;
import l2j.gameserver.handler.bypass.BypassOlympiadArenaChange;
import l2j.gameserver.handler.bypass.BypassPlayerHelp;
import l2j.gameserver.handler.bypass.BypassQuest;
import l2j.gameserver.handler.bypass.BypassTeleport;
import l2j.gameserver.handler.bypass.BypassTerritoryStatus;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author nBd, UnAfraid, fissban
 */
public class BypassHandler
{
	public static final Logger LOG = Logger.getLogger(IBypassHandler.class.getName());
	
	public interface IBypassHandler
	{
		public static final Logger LOG = Logger.getLogger(IBypassHandler.class.getName());
		
		public boolean useBypass(String command, L2PcInstance activeChar, L2Character target);
		
		public String[] getBypassList();
	}
	
	private static final Map<String, IBypassHandler> bypasses = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new BypassChat());
		registerHandler(new BypassCPRecovery());
		registerHandler(new BypassEnterRift());
		registerHandler(new BypassLink());
		registerHandler(new BypassMultisell());
		registerHandler(new BypassOlympiadArenaChange());
		registerHandler(new BypassPlayerHelp());
		registerHandler(new BypassQuest());
		registerHandler(new BypassTeleport());
		registerHandler(new BypassTerritoryStatus());
		
		LOG.info("BypassHandler: load " + size() + " handlers");
	}
	
	public static void registerHandler(IBypassHandler handler)
	{
		for (String command : handler.getBypassList())
		{
			bypasses.put(command.toLowerCase(), handler);
		}
	}
	
	public static IBypassHandler getHandler(String command)
	{
		if (command.contains(" "))
		{
			command = command.substring(0, command.indexOf(" "));
		}
		return bypasses.get(command.toLowerCase());
	}
	
	public static int size()
	{
		return bypasses.size();
	}
	
	public static BypassHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BypassHandler INSTANCE = new BypassHandler();
	}
}
