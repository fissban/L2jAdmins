package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.action.ArtefactOnAction;
import l2j.gameserver.handler.action.DoorOnAction;
import l2j.gameserver.handler.action.ItemInstanceOnAction;
import l2j.gameserver.handler.action.NpcOnAction;
import l2j.gameserver.handler.action.PcInstanceOnAction;
import l2j.gameserver.handler.action.PetInstanceOnAction;
import l2j.gameserver.handler.action.StaticObjectInstanceOnAction;
import l2j.gameserver.handler.action.SummonOnAction;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;

/**
 * @author fissban
 */
public class ActionHandler
{
	// Interface
	public interface IActionHandler
	{
		public static final Logger LOG = Logger.getLogger(IActionHandler.class.getName());
		
		public boolean action(L2PcInstance player, L2Object target, boolean interact);
		
		public InstanceType getInstanceType();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(ActionHandler.class.getName());
	// Instances
	private static final Map<InstanceType, IActionHandler> actions = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerAction(new ArtefactOnAction());
		registerAction(new DoorOnAction());
		registerAction(new ItemInstanceOnAction());
		registerAction(new NpcOnAction());
		registerAction(new PcInstanceOnAction());
		registerAction(new PetInstanceOnAction());
		registerAction(new StaticObjectInstanceOnAction());
		registerAction(new SummonOnAction());
		
		LOG.info("ActionHandler: load " + size() + " handlers");
	}
	
	/**
	 * Action
	 * @param handler
	 */
	public static void registerAction(IActionHandler handler)
	{
		actions.put(handler.getInstanceType(), handler);
	}
	
	public static IActionHandler getAction(InstanceType iType)
	{
		IActionHandler result = null;
		for (InstanceType t = iType; t != null; t = t.getParent())
		{
			result = actions.get(t);
			if (result != null)
			{
				break;
			}
		}
		return result;
	}
	
	public static int size()
	{
		return actions.size();
	}
	
	public static ActionHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ActionHandler INSTANCE = new ActionHandler();
	}
}
