package l2j.gameserver.handler.bypass;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class BypassOlympiadArenaChange implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"OlympiadArenaChange"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		// if (!activeChar.inObserverMode())
		// {
		// return false;
		// }
		//
		// int arena = Olympiad.getManager() != null ? Olympiad.getManager().getSpectatedGame(activeChar) : -1;
		// if (arena < 0)
		// {
		// return false;
		// }
		//
		// String[] commands = command.split(" ");
		// int id = Integer.parseInt(commands[1]);
		//
		// Olympiad.getInstance().removeSpectator(arena, activeChar);
		// Olympiad.getInstance().addSpectator(id, activeChar, false);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
