package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.say.SayAll;
import l2j.gameserver.handler.say.SayAlliance;
import l2j.gameserver.handler.say.SayChannelAll;
import l2j.gameserver.handler.say.SayChannelLeader;
import l2j.gameserver.handler.say.SayClan;
import l2j.gameserver.handler.say.SayHeroe;
import l2j.gameserver.handler.say.SayParty;
import l2j.gameserver.handler.say.SayPartyRoom;
import l2j.gameserver.handler.say.SayPetition;
import l2j.gameserver.handler.say.SayShout;
import l2j.gameserver.handler.say.SayTell;
import l2j.gameserver.handler.say.SayTrade;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class SayHandler
{
	// Interface
	public interface ISayHandler
	{
		public void handleSay(SayType type, L2PcInstance activeChar, String target, String text);
		
		public SayType[] getSayTypeList();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(SayHandler.class.getName());
	// Instance
	private static final Map<SayType, ISayHandler> says = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new SayAll());
		registerHandler(new SayAlliance());
		registerHandler(new SayChannelAll());
		registerHandler(new SayChannelLeader());
		registerHandler(new SayClan());
		registerHandler(new SayHeroe());
		registerHandler(new SayParty());
		registerHandler(new SayPartyRoom());
		registerHandler(new SayPetition());
		registerHandler(new SayShout());
		registerHandler(new SayTell());
		registerHandler(new SayTrade());
		
		UtilPrint.result("SayHandler", "Loaded handlers", size());
	}
	
	public static void registerHandler(ISayHandler handler)
	{
		for (SayType id : handler.getSayTypeList())
		{
			says.put(id, handler);
		}
	}
	
	public static ISayHandler getHandler(SayType chatType)
	{
		return says.get(chatType);
	}
	
	public static int size()
	{
		return says.size();
	}
	
	public static SayHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SayHandler INSTANCE = new SayHandler();
	}
}
