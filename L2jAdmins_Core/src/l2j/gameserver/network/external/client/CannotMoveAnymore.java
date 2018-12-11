package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PartyMemberPosition;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class CannotMoveAnymore extends AClientPacket
{
	private int x;
	private int y;
	private int z;
	private int heading;
	
	@Override
	protected void readImpl()
	{
		x = readD();
		y = readD();
		z = readD();
		heading = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		if (player.getAI() != null)
		{
			player.getAI().notifyEvent(CtrlEventType.ARRIVED_BLOCKED, new LocationHolder(x, y, z, heading));
		}
		
		if (player.getParty() != null)
		{
			player.getParty().broadcastToPartyMembers((player), new PartyMemberPosition(player));
		}
	}
}
