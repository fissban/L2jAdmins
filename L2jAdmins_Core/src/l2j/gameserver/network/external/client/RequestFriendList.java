package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestFriendList extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(SystemMessage.FRIEND_LIST_HEADER);
		
		for (Integer friend : activeChar.getFriendList())
		{
			SystemMessage sm;
			
			L2PcInstance onlineFriend = L2World.getInstance().getPlayer(friend);
			if (onlineFriend == null)
			{
				// (Currently: Offline)
				sm = new SystemMessage(SystemMessage.S1_OFFLINE);
				sm.addString(CharNameData.getInstance().getNameById(friend));
			}
			else
			{
				// (Currently: Online)
				sm = new SystemMessage(SystemMessage.S1_ONLINE);
				sm.addString(onlineFriend.getName());
			}
			
			activeChar.sendPacket(sm);
		}
		
		activeChar.sendPacket(SystemMessage.FRIEND_LIST_FOOTER);
	}
}
