package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.GameTimeTaskManager;

public class UserTime implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			77
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		final int hour = GameTimeTaskManager.getInstance().getGameHour();
		final int minute = GameTimeTaskManager.getInstance().getGameMinute();
		
		final String min = ((minute < 10) ? "0" : "") + minute;
		
		activeChar.sendPacket(new SystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessage.TIME_S1_S2_IN_THE_NIGHT : SystemMessage.TIME_S1_S2_IN_THE_DAY).addNumber(hour).addString(min));
		
		return true;
	}
}
