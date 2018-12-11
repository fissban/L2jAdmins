package l2j.gameserver.handler.bypass;

import java.util.List;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;

/**
 * @author fissban
 */
public class BypassChat implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Chat"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		int val = 0;
		try
		{
			val = Integer.parseInt(command.substring(5));
		}
		catch (Exception ioobe)
		{
			//
		}
		
		final L2Npc npc = (L2Npc) target;
		final List<Script> firstTalk = npc.getTemplate().getEventScript(ScriptEventType.ON_FIRST_TALK);
		if ((val == 0) && (firstTalk != null) && (firstTalk.size() == 1))
		{
			firstTalk.get(0).notifyFirstTalk(npc, activeChar);
		}
		else
		{
			npc.showChatWindow(activeChar, val);
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
