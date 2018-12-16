package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.command.voiced.VoicedCastle;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.util.UtilPrint;

public class CommandVoicedHandler
{
	// Interface
	public interface IVoicedCommandHandler
	{
		public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target);
		
		public String[] getVoicedCommandList();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(CommandVoicedHandler.class.getName());
	// Instances
	private static final Map<String, IVoicedCommandHandler> voiceds = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new VoicedCastle());
		
		UtilPrint.result("CommandVoicedHandler", "Loaded handlers", size());
	}
	
	public static void registerHandler(IVoicedCommandHandler handler)
	{
		for (String id : handler.getVoicedCommandList())
		{
			voiceds.put(new String(id), handler);
		}
	}
	
	public static IVoicedCommandHandler getHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		return voiceds.get(command);
	}
	
	public static int size()
	{
		return voiceds.size();
	}
	
	public static CommandVoicedHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommandVoicedHandler INSTANCE = new CommandVoicedHandler();
	}
}
