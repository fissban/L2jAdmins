package l2j.gameserver.network.external.client;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.FriendRecvMsg;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Recieve Private (Friend) Message - 0xCC Format: c SS S: Message S: Receiving Player
 * @author Tempy
 */
public class RequestSendFriendMsg extends AClientPacket
{
	private static final Logger LOG_CHAT = Logger.getLogger(RequestSendFriendMsg.class.getName());
	
	private String message;
	private String receiver;
	
	@Override
	protected void readImpl()
	{
		message = readS();
		receiver = readS();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((message == null) || message.isEmpty() || (message.length() > 300))
		{
			return;
		}
		
		final L2PcInstance targetPlayer = L2World.getInstance().getPlayer(receiver);
		if (targetPlayer == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME));
			return;
		}
		
		if (Config.LOG_CHAT)
		{
			final LogRecord record = new LogRecord(Level.INFO, message);
			record.setLoggerName("chat");
			record.setParameters(new Object[]
			{
				"PRIV_MSG",
				"[" + activeChar.getName() + " to " + receiver + "]"
			});
			LOG_CHAT.log(record);
		}
		
		targetPlayer.sendPacket(new FriendRecvMsg(activeChar, receiver, message));
	}
}
