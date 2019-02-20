package l2j.gameserver.illegalaction;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class IllegalAction
{
	public static void report(L2PcInstance actor, String message, IllegalActionType punishmentType)
	{
		ThreadPoolManager.schedule(new IllegalActionTask(actor, message, punishmentType), 5000);
	}
	
	public static void report(L2PcInstance actor, String message)
	{
		switch (Config.DEFAULT_PUNISH)
		{
			case 1:
				ThreadPoolManager.schedule(new IllegalActionTask(actor, message, IllegalActionType.PUNISH_BROADCAST), 5000);
				break;
			case 2:
				ThreadPoolManager.schedule(new IllegalActionTask(actor, message, IllegalActionType.PUNISH_KICK), 5000);
				break;
			case 3:
				ThreadPoolManager.schedule(new IllegalActionTask(actor, message, IllegalActionType.PUNISH_KICKBAN), 5000);
				break;
			case 4:
				ThreadPoolManager.schedule(new IllegalActionTask(actor, message, IllegalActionType.PUNISH_JAIL), 5000);
				break;
			default:
				ThreadPoolManager.schedule(new IllegalActionTask(actor, message, IllegalActionType.PUNISH_JAIL), 5000);
		}
	}
}
