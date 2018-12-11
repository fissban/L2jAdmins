package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * Send Private (Friend) Message Format: c dSSS d: Unknown S: Sending Player S: Receiving Player S: Message
 * @author Tempy
 */
public class FriendRecvMsg extends AServerPacket
{
	private final String sender, receiver, message;
	
	public FriendRecvMsg(L2PcInstance player, String receiver, String message)
	{
		sender = player.getName();
		this.receiver = receiver;
		this.message = message;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfd);
		
		writeD(0); // ??
		
		writeS(receiver);
		writeS(sender);
		writeS(message);
	}
}
