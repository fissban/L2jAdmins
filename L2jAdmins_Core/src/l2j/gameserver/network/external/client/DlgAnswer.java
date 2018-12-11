package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Dezmond_snz Format: cddd
 */
public class DlgAnswer extends AClientPacket
{
	public enum DlgAnswerType
	{
		CLOSE,
		OPEN,
	}
	
	private int messageId;
	private int answer;
	
	@Override
	protected void readImpl()
	{
		messageId = readD();
		answer = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		switch (messageId)
		{
			case SystemMessage.DO_YOU_WANT_TO_BE_RESTORED:
			case SystemMessage.RESSURECTION_REQUEST_BY_C1_FOR_S2_XP:
				activeChar.getRequestRevive().reviveAnswer(answer);
				break;
			
			case SystemMessage.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE:
				activeChar.getRequestDoor().getDoor(answer, DlgAnswerType.CLOSE);
				break;
			
			case SystemMessage.WOULD_YOU_LIKE_TO_OPEN_THE_GATE:
				activeChar.getRequestDoor().getDoor(answer, DlgAnswerType.OPEN);
				break;
			
			default:
				LOG.info(DlgAnswer.class.getSimpleName() + ": wrong messageID ->" + messageId);
		}
	}
}
