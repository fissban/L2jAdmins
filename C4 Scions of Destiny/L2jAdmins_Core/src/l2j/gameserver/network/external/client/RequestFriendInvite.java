package l2j.gameserver.network.external.client;

import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.request.RequestPacketType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AskJoinFriend;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestFriendInvite extends AClientPacket
{
	private String name;
	
	@Override
	protected void readImpl()
	{
		name = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance friend = L2World.getInstance().getPlayer(name);
		
		if ((friend == null) || friend.getInvisible())
		{
			// Target is not found in the game.
			activeChar.sendPacket(SystemMessage.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
			return;
		}
		if (friend == activeChar)
		{
			// You cannot add yourself to your own friend list.
			activeChar.sendPacket(SystemMessage.YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST);
			return;
		}
		
		if (activeChar.getFriendList().contains(friend.getObjectId()))
		{
			// Player already is in your friendlist
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_ALREADY_IN_FRIENDS_LIST).addString(name));
			return;
		}
		
		if (PcBlockList.isBlocked(activeChar, friend))
		{
			activeChar.sendMessage("You have blocked " + name + ".");
			return;
		}
		
		if (PcBlockList.isBlocked(friend, activeChar))
		{
			activeChar.sendMessage("You are in " + name + "'s block list.");
			return;
		}
		
		if (!activeChar.getRequestInvite().startRequest(friend, RequestPacketType.FRIEND_INVITE))
		{
			return;
		}
		
		if (!friend.isRequestActive())
		{
			// request to become friend
			friend.sendPacket(new SystemMessage(SystemMessage.C1_REQUESTED_TO_BECOME_FRIENDS).addString(name));
			friend.sendPacket(new AskJoinFriend(activeChar));
		}
	}
}
