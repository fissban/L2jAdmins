package l2j.gameserver.network.external.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import l2j.DatabaseManager;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.FriendList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestFriendDel extends AClientPacket
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
		SystemMessage sm;
		
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Check if target is friend and delete him from friends list
		int id = CharNameData.getInstance().getIdByName(name);
		
		if ((id == -1) || !activeChar.getFriendList().contains(id))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.C1_NOT_ON_YOUR_FRIENDS_LIST).addString(name));
			return;
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE (char_id=? AND friend_id=?) OR (char_id=? AND friend_id=?)"))
		{
			ps.setInt(1, activeChar.getObjectId());
			ps.setInt(2, id);
			ps.setInt(3, id);
			ps.setInt(4, activeChar.getObjectId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "could not del friend objectid: ", e);
		}
		
		activeChar.getFriendList().remove(Integer.valueOf(id));
		activeChar.sendPacket(new FriendList(activeChar)); // update friendList *heavy method*
		
		L2PcInstance friend = L2World.getInstance().getPlayer(name);
		if (friend != null)
		{
			friend.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
			friend.sendPacket(new FriendList(friend));
		}
		
		// Player deleted from your friendlist
		sm = new SystemMessage(SystemMessage.S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST);
		sm.addString(name);
		activeChar.sendPacket(sm);
		sm = null;
	}
}
