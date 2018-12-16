package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.handler.actionshift.DoorOnActionShift;
import l2j.gameserver.handler.actionshift.NpcOnActionShift;
import l2j.gameserver.handler.actionshift.PcInstanceOnActionShift;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class ActionShiftHandler
{
	// Log
	public static final Logger LOG = Logger.getLogger(ActionShiftHandler.class.getName());
	// Instances
	private static final Map<InstanceType, IActionHandler> actionsShift = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new DoorOnActionShift());
		registerHandler(new NpcOnActionShift());
		registerHandler(new PcInstanceOnActionShift());
		
		UtilPrint.result("ActionShiftHandler", "Loaded handlers", size());
	}
	
	public static void registerHandler(IActionHandler handler)
	{
		actionsShift.put(handler.getInstanceType(), handler);
	}
	
	public static IActionHandler getHandler(InstanceType iType)
	{
		IActionHandler result = null;
		for (InstanceType t = iType; t != null; t = t.getParent())
		{
			result = actionsShift.get(t);
			if (result != null)
			{
				break;
			}
		}
		return result;
	}
	
	public static int size()
	{
		return actionsShift.size();
	}
	
	public static ActionShiftHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ActionShiftHandler INSTANCE = new ActionShiftHandler();
	}
}
