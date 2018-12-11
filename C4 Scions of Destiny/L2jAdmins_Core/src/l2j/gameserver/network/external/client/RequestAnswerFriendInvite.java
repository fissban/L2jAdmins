package l2j.gameserver.network.external.client;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.FriendList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * sample 5F 01 00 00 00 format cdd
 * @version $Revision: 1.7.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAnswerFriendInvite extends AClientPacket
{
	private int response;
	
	@Override
	protected void readImpl()
	{
		response = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player != null)
		{
			L2PcInstance requestor = player.getRequestInvite().getPartner();
			if (requestor == null)
			{
				return;
			}
			
			if (response == 1)
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement ps = con.prepareStatement("INSERT INTO character_friends (char_id, friend_id) VALUES (?, ?), (?, ?)"))
				{
					ps.setInt(1, requestor.getObjectId());
					ps.setInt(2, player.getObjectId());
					ps.setInt(3, player.getObjectId());
					ps.setInt(4, requestor.getObjectId());
					ps.execute();
					
					requestor.sendPacket(SystemMessage.YOU_HAVE_SUCCEEDED_INVITING_FRIEND);
					
					// Player added to your friendlist
					requestor.sendPacket(new SystemMessage(SystemMessage.S1_ADDED_TO_FRIENDS).addString(player.getName()));
					requestor.getFriendList().add(player.getObjectId());
					
					// has joined as friend.
					player.sendPacket(new SystemMessage(SystemMessage.S1_JOINED_AS_FRIEND).addString(requestor.getName()));
					player.getFriendList().add(requestor.getObjectId());
					
					// Send notifications for both players in order to show them online
					player.sendPacket(new FriendList(player));
					requestor.sendPacket(new FriendList(requestor));
				}
				catch (Exception e)
				{
					LOG.warning("could not add friend objectid: " + e);
				}
			}
			else
			{
				requestor.sendPacket(SystemMessage.FAILED_TO_INVITE_A_FRIEND);
			}
			
			requestor.getRequestInvite().endRequest();
		}
	}
}
