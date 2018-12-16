package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.community.AbstractCommunityHandler;
import l2j.gameserver.handler.community.CommunityAddFavorite;
import l2j.gameserver.handler.community.CommunityClan;
import l2j.gameserver.handler.community.CommunityFavorite;
import l2j.gameserver.handler.community.CommunityFriends;
import l2j.gameserver.handler.community.CommunityMail;
import l2j.gameserver.handler.community.CommunityMemo;
import l2j.gameserver.handler.community.CommunityRegion;
import l2j.gameserver.handler.community.CommunityTop;
import l2j.util.UtilPrint;

/**
 * Definimos los commandos usados dentro del community.<br>
 * <li>getCmdList() -> cmdList -> useCommunityCommand</li><br>
 * <li>getWriteList() -> writeList -> useCommunityWrite</li>
 * @author fissban
 */
public class CommunityHandler
{
	public static final Logger LOG = Logger.getLogger(CommunityHandler.class.getName());
	
	private static final Map<String, AbstractCommunityHandler> cmdList = new HashMap<>();
	private static final Map<String, AbstractCommunityHandler> writeList = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new CommunityAddFavorite());
		registerHandler(new CommunityClan());
		registerHandler(new CommunityFavorite());
		registerHandler(new CommunityFriends());
		registerHandler(new CommunityMail());
		registerHandler(new CommunityMemo());
		registerHandler(new CommunityRegion());
		registerHandler(new CommunityTop());
		
		UtilPrint.result("CommunityHandler", "Loaded handlers", size());
	}
	
	public static void registerHandler(AbstractCommunityHandler handler)
	{
		for (String command : handler.getCmdList())
		{
			cmdList.put(command.toLowerCase(), handler);
		}
		
		if ((handler.getWriteList() != null) && !handler.getWriteList().isEmpty())
		{
			writeList.put(handler.getWriteList().toLowerCase(), handler);
		}
	}
	
	public static AbstractCommunityHandler getHandler(String communityCommand)
	{
		if ((communityCommand != null) && (communityCommand != ""))
		{
			String command = communityCommand;
			if (communityCommand.indexOf(";") != -1)
			{
				command = communityCommand.substring(0, communityCommand.indexOf(";"));
			}
			
			if (cmdList.containsKey(command.toLowerCase()))
			{
				return cmdList.get(command.toLowerCase());
			}
			if (writeList.containsKey(command.toLowerCase()))
			{
				return writeList.get(command.toLowerCase());
			}
		}
		
		return null;
	}
	
	public static int size()
	{
		return cmdList.size() + writeList.size();
	}
	
	public static CommunityHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityHandler INSTANCE = new CommunityHandler();
	}
}
