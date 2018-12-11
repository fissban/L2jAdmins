package l2j.gameserver.handler.bypass;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.instancemanager.DimensionalRiftManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class BypassEnterRift implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"EnterRift",
		"ChangeRiftRoom",
		"ExitRift"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		if (command.startsWith("EnterRift"))
		{
			try
			{
				Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
				DimensionalRiftManager.getInstance().start(activeChar, b1, (L2Npc) target);
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("ChangeRiftRoom"))
		{
			if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift())
			{
				activeChar.getParty().getDimensionalRift().manualTeleport(activeChar, (L2Npc) target);
			}
		}
		else if (command.startsWith("ExitRift"))
		{
			if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift())
			{
				activeChar.getParty().getDimensionalRift().manualExitRift(activeChar, (L2Npc) target);
			}
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
}
