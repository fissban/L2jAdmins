package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author kamy
 */
public class UserOlympiadStat implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			109
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		SystemMessage sm = new SystemMessage(SystemMessage.THE_CURRENT_RECORD_FOR_THIS_OLYMPIAD_SESSION_IS_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
		sm.addNumber(Olympiad.getInstance().getCompetitionWon(activeChar.getObjectId()));
		sm.addNumber(Olympiad.getInstance().getCompetitionLost(activeChar.getObjectId()));
		sm.addNumber(Olympiad.getInstance().getNoblePoints(activeChar.getObjectId()));
		activeChar.sendPacket(sm);
		return true;
	}
}
