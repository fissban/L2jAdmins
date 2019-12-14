package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AskJoinAlly;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestJoinAlly extends AClientPacket
{
	private int objectId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance target = L2World.getInstance().getPlayer(objectId);
		
		if (target == null)
		{
			return;
		}
		
		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_A_CLAN_MEMBER));
			return;
		}
		
		if (!clan.checkAllyJoinCondition(activeChar, target))
		{
			return;
		}
		
		if (!activeChar.getRequestInvite().startRequest(target, RequestPacketType.JOIN_ALLY))
		{
			return;
		}
		
		target.sendPacket(new SystemMessage(SystemMessage.S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE).addString(activeChar.getName()).addString(clan.getAllyName()));
		target.sendPacket(new AskJoinAlly(activeChar));
	}
}
