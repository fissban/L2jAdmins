package l2j.gameserver.handler.bypass;

import l2j.gameserver.data.MultisellData;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class BypassMultisell implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"multisell",
		"exc_multisell"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		if (command.startsWith("multisell"))
		{
			MultisellData.getInstance().createMultiSell(Integer.parseInt(command.substring(9).trim()), activeChar, false, (L2Npc) target);
		}
		else if (command.startsWith("exc_multisell"))
		{
			MultisellData.getInstance().createMultiSell(Integer.parseInt(command.substring(13).trim()), activeChar, true, (L2Npc) target);
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
