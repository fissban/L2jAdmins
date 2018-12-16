package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.command.user.UserChannelDelete;
import l2j.gameserver.handler.command.user.UserChannelLeave;
import l2j.gameserver.handler.command.user.UserChannelListUpdate;
import l2j.gameserver.handler.command.user.UserClanPenalty;
import l2j.gameserver.handler.command.user.UserClanWarsList;
import l2j.gameserver.handler.command.user.UserDisMount;
import l2j.gameserver.handler.command.user.UserEscape;
import l2j.gameserver.handler.command.user.UserLoc;
import l2j.gameserver.handler.command.user.UserMount;
import l2j.gameserver.handler.command.user.UserOlympiadStat;
import l2j.gameserver.handler.command.user.UserPartyInfo;
import l2j.gameserver.handler.command.user.UserSiegeStatus;
import l2j.gameserver.handler.command.user.UserTime;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class CommandUserHandler
{
	public interface IUserCommandHandler
	{
		public boolean useUserCommand(int id, L2PcInstance activeChar);
		
		public int[] getUserCommandList();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(CommandUserHandler.class.getName());
	// Instances
	private static final Map<Integer, IUserCommandHandler> commands = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new UserChannelDelete());
		registerHandler(new UserChannelLeave());
		registerHandler(new UserChannelListUpdate());
		registerHandler(new UserClanPenalty());
		registerHandler(new UserClanWarsList());
		registerHandler(new UserDisMount());
		registerHandler(new UserEscape());
		registerHandler(new UserLoc());
		registerHandler(new UserMount());
		registerHandler(new UserOlympiadStat());
		registerHandler(new UserPartyInfo());
		registerHandler(new UserSiegeStatus());
		registerHandler(new UserTime());
		
		UtilPrint.result("CommandUserHandler", "Loaded handlers", size());
	}
	
	public void registerHandler(IUserCommandHandler handler)
	{
		for (int id : handler.getUserCommandList())
		{
			commands.put(id, handler);
		}
	}
	
	public IUserCommandHandler getHandler(Integer val)
	{
		return commands.get(val);
	}
	
	public int size()
	{
		return commands.size();
	}
	
	public static CommandUserHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommandUserHandler INSTANCE = new CommandUserHandler();
	}
}
