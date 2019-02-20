package l2j.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class PcBlockList
{
	private static final Logger LOG = Logger.getLogger(PcBlockList.class.getName());
	private static Map<Integer, List<Integer>> offlineList = new HashMap<>();
	
	private final L2PcInstance owner;
	private List<Integer> blockList;
	
	public PcBlockList(L2PcInstance owner)
	{
		this.owner = owner;
		blockList = offlineList.get(owner.getObjectId());
		if (blockList == null)
		{
			blockList = loadList(owner.getObjectId());
		}
	}
	
	private synchronized void addToBlockList(int target)
	{
		blockList.add(target);
		updateInDB(target, true);
	}
	
	private synchronized void removeFromBlockList(int target)
	{
		blockList.remove(Integer.valueOf(target));
		updateInDB(target, false);
	}
	
	public void playerLogout()
	{
		offlineList.put(owner.getObjectId(), blockList);
	}
	
	private static List<Integer> loadList(int objId)
	{
		List<Integer> list = new ArrayList<>();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT friend_id FROM character_friends WHERE char_id = ? AND relation = 1"))
		{
			statement.setInt(1, objId);
			try (ResultSet rset = statement.executeQuery())
			{
				int friendId;
				while (rset.next())
				{
					friendId = rset.getInt("friend_id");
					if (friendId == objId)
					{
						continue;
					}
					
					list.add(friendId);
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Error found in " + objId + " friendlist while loading BlockList: " + e.getMessage(), e);
		}
		return list;
	}
	
	private void updateInDB(int targetId, boolean state)
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			if (state)
			{
				try (PreparedStatement ps = con.prepareStatement("INSERT INTO character_friends (char_id, friend_id, relation) VALUES (?, ?, 1)"))
				{
					ps.setInt(1, owner.getObjectId());
					ps.setInt(2, targetId);
					ps.execute();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE char_id = ? AND friend_id = ? AND relation = 1"))
				{
					ps.setInt(1, owner.getObjectId());
					ps.setInt(2, targetId);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Could not add/remove block player: " + e.getMessage(), e);
		}
	}
	
	public boolean isInBlockList(int objectId)
	{
		return blockList.contains(objectId);
	}
	
	private boolean isBlockAll()
	{
		return owner.isInRefusalMode();
	}
	
	public static boolean isBlocked(L2PcInstance listOwner, L2PcInstance target)
	{
		return listOwner.getBlockList().isInBlockList(target.getObjectId());
	}
	
	public static boolean isBlocked(L2PcInstance listOwner, int targetId)
	{
		return listOwner.getBlockList().isInBlockList(targetId);
	}
	
	private void setBlockAll(boolean state)
	{
		owner.setInRefusalMode(state);
	}
	
	public List<Integer> getBlockList()
	{
		return blockList;
	}
	
	public static void addToBlockList(L2PcInstance listOwner, int targetId)
	{
		if (listOwner == null)
		{
			return;
		}
		
		String charName = CharNameData.getInstance().getNameById(targetId);
		
		if (listOwner.getFriendList().contains(targetId))
		{
			listOwner.sendPacket(new SystemMessage(SystemMessage.S1_ALREADY_IN_FRIENDS_LIST).addString(charName));
			return;
		}
		
		if (listOwner.getBlockList().getBlockList().contains(targetId))
		{
			listOwner.sendMessage("Already in ignore list.");
			return;
		}
		
		listOwner.getBlockList().addToBlockList(targetId);
		
		listOwner.sendPacket(new SystemMessage(SystemMessage.S1_WAS_ADDED_TO_YOUR_IGNORE_LIST).addString(charName));
		
		L2PcInstance player = L2World.getInstance().getPlayer(targetId);
		
		if (player != null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addString(listOwner.getName()));
		}
	}
	
	public static void removeFromBlockList(L2PcInstance listOwner, int targetId)
	{
		if (listOwner == null)
		{
			return;
		}
		
		String charName = CharNameData.getInstance().getNameById(targetId);
		
		if (!listOwner.getBlockList().getBlockList().contains(targetId))
		{
			listOwner.sendPacket(new SystemMessage(SystemMessage.TARGET_IS_INCORRECT));
			return;
		}
		
		listOwner.getBlockList().removeFromBlockList(targetId);
		listOwner.sendPacket(new SystemMessage(SystemMessage.S1_WAS_REMOVED_FROM_YOUR_IGNORE_LIST).addString(charName));
	}
	
	public static boolean isInBlockList(L2PcInstance listOwner, L2PcInstance target)
	{
		return listOwner.getBlockList().isInBlockList(target.getObjectId());
	}
	
	public boolean isBlockAll(L2PcInstance listOwner)
	{
		return listOwner.getBlockList().isBlockAll();
	}
	
	public static void setBlockAll(L2PcInstance listOwner, boolean newValue)
	{
		listOwner.getBlockList().setBlockAll(newValue);
	}
	
	public static void sendListToOwner(L2PcInstance listOwner)
	{
		int i = 1;
		listOwner.sendPacket(SystemMessage.BLOCK_LIST_HEADER);
		
		for (int playerId : listOwner.getBlockList().getBlockList())
		{
			listOwner.sendMessage((i++) + ". " + CharNameData.getInstance().getNameById(playerId));
		}
		
		listOwner.sendPacket(SystemMessage.FRIEND_LIST_FOOTER);
	}
	
	/**
	 * @param  ownerId  object id of owner block list
	 * @param  targetId object id of potential blocked player
	 * @return          true if blocked
	 */
	public static boolean isInBlockList(int ownerId, int targetId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(ownerId);
		
		if (player != null)
		{
			return PcBlockList.isBlocked(player, targetId);
		}
		
		if (!offlineList.containsKey(ownerId))
		{
			offlineList.put(ownerId, loadList(ownerId));
		}
		
		return offlineList.get(ownerId).contains(targetId);
	}
}
