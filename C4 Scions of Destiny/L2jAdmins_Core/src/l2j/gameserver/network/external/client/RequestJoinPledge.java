package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.request.RequestPacketType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AskJoinPledge;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestJoinPledge extends AClientPacket
{
	private int targetId;
	
	@Override
	protected void readImpl()
	{
		targetId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance newMember = L2World.getInstance().getPlayer(targetId);
		
		if (newMember == null)
		{
			return;
		}
		
		if (!activeChar.getClan().checkClanJoinCondition(activeChar, newMember))
		{
			return;
		}
		
		if (!activeChar.getRequestInvite().startRequest(newMember, RequestPacketType.JOIN_PLEDGE))
		{
			return;
		}
		
		newMember.sendPacket(new AskJoinPledge(activeChar));
	}
}
