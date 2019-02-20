package l2j.gameserver.handler.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2j.DatabaseManager;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.FriendList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class CommunityFriends extends AbstractCommunityHandler
{
	private static final String FRIENDLIST_DELETE_BUTTON = "<br>\n<table><tr><td width=10></td><td>Are you sure you want to delete all friends from your Friends List?</td><td width=20></td><td><button value=\"OK\" action=\"bypass _friendlist_0_;delall\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td></tr></table>";
	private static final String BLOCKLIST_DELETE_BUTTON = "<br>\n<table><tr><td width=10></td><td>Are you sure you want to delete all players from your Block List?</td><td width=20></td><td><button value=\"OK\" action=\"bypass _friendlist_0_;block delall\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td></tr></table>";
	
	@Override
	public String[] getCmdList()
	{
		return new String[]
		{
			"_friendlist_0_"
		};
	}
	
	@Override
	public void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar)
	{
		st.nextToken();// friendlist_0_
		
		if (!st.hasMoreTokens())
		{
			showFriendsList(activeChar, false);
		}
		else
		{
			// showFriendsList(activeChar, false);
			String event = st.nextToken();
			
			switch (event)
			{
				case "blocklist":
					showBlockList(activeChar, false);
					break;
				
				case "select":
					activeChar.selectFriend((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
					showFriendsList(activeChar, false);
					break;
				
				case "deselect":
					activeChar.deselectFriend((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
					showFriendsList(activeChar, false);
					break;
				
				case "delall":
					try (Connection con = DatabaseManager.getConnection();
						PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE char_id = ? OR friend_id = ?"))
					{
						ps.setInt(1, activeChar.getObjectId());
						ps.setInt(2, activeChar.getObjectId());
						ps.execute();
					}
					catch (Exception e)
					{
						LOG.warning("could not delete friends objectid: " + e);
					}
					
					for (int friendId : activeChar.getFriendList())
					{
						L2PcInstance player = L2World.getInstance().getPlayer(friendId);
						if (player != null)
						{
							player.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
							player.getSelectedFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
							
							player.sendPacket(new FriendList(player)); // update friendList *heavy method*
						}
					}
					
					activeChar.getFriendList().clear();
					activeChar.getSelectedFriendList().clear();
					showFriendsList(activeChar, false);
					
					activeChar.sendMessage("You have cleared your friend list.");
					activeChar.sendPacket(new FriendList(activeChar));
					break;
				
				case "delconfirm":
					showFriendsList(activeChar, true);
					break;
				
				case "del":
					try (Connection con = DatabaseManager.getConnection())
					{
						for (int friendId : activeChar.getSelectedFriendList())
						{
							try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_friends WHERE (char_id = ? AND friend_id = ?) OR (char_id = ? AND friend_id = ?)"))
							{
								ps.setInt(1, activeChar.getObjectId());
								ps.setInt(2, friendId);
								ps.setInt(3, friendId);
								ps.setInt(4, activeChar.getObjectId());
								ps.execute();
							}
							
							String name = CharNameData.getInstance().getNameById(friendId);
							
							L2PcInstance player = L2World.getInstance().getPlayer(friendId);
							if (player != null)
							{
								player.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
								player.sendPacket(new FriendList(player)); // update friendList *heavy method*
							}
							
							// Player deleted from your friendlist
							activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST).addString(name));
							activeChar.getFriendList().remove(Integer.valueOf(friendId));
						}
					}
					catch (Exception e)
					{
						LOG.warning("could not delete friend objectid: " + e);
					}
					
					activeChar.getSelectedFriendList().clear();
					showFriendsList(activeChar, false);
					
					activeChar.sendPacket(new FriendList(activeChar)); // update friendList *heavy method*
					break;
				
				case "mail":
					if (!activeChar.getSelectedFriendList().isEmpty())
					{
						showMailWrite(activeChar);
					}
					break;
				
				case "block":
					String action = st.nextToken();
					switch (action)
					{
						case "select":
							activeChar.selectBlock((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
							showBlockList(activeChar, false);
							break;
						
						case "deselect":
							activeChar.deselectBlock((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
							showBlockList(activeChar, false);
							break;
						
						case "delall":
							List<Integer> list = new ArrayList<>();
							list.addAll(activeChar.getBlockList().getBlockList());
							
							for (Integer blockId : list)
							{
								PcBlockList.removeFromBlockList(activeChar, blockId);
							}
							
							activeChar.getSelectedBlocksList().clear();
							showBlockList(activeChar, false);
							break;
						
						case "delconfirm":
							showBlockList(activeChar, true);
							break;
						
						case "del":
							for (Integer blockId : activeChar.getSelectedBlocksList())
							{
								PcBlockList.removeFromBlockList(activeChar, blockId);
							}
							
							activeChar.getSelectedBlocksList().clear();
							showBlockList(activeChar, false);
							break;
						
						default:
							separateAndSend("<html><body><br><br><center>the command: " + st.toString() + " is not implemented yet</center><br><br></body></html>", activeChar);
							break;
					}
					
				default:
					separateAndSend("<html><body><br><br><center>the command: " + st.toString() + " is not implemented yet</center><br><br></body></html>", activeChar);
					break;
			}
			
		}
	}
	
	@Override
	public String getWriteList()
	{
		return "Friend";
	}
	
	@Override
	public void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		if (ar1.equalsIgnoreCase("mail"))
		{
			CommunityMail.sendLetter(ar2, ar4, ar5, activeChar);
			showFriendsList(activeChar, false);
		}
	}
	
	// METODOS PARSECMD ---------------------------------------------------------------------------------------
	
	private static void showBlockList(L2PcInstance activeChar, boolean delMsg)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "friend/friend-blocklist.htm");
		if (content == null)
		{
			return;
		}
		
		// Retrieve activeChar's blocklist and selected
		final List<Integer> list = activeChar.getBlockList().getBlockList();
		final List<Integer> slist = activeChar.getSelectedBlocksList();
		
		// Blocklist
		if (list.isEmpty())
		{
			content = content.replaceAll("%blocklist%", "");
		}
		else
		{
			String selectedBlocks = "";
			
			for (Integer id : list)
			{
				if (slist.contains(id))
				{
					continue;
				}
				
				String blockName = CharNameData.getInstance().getNameById(id);
				if (blockName == null)
				{
					continue;
				}
				
				L2PcInstance block = L2World.getInstance().getPlayer(blockName);
				selectedBlocks += "<a action=\"bypass _friendlist_0_;block;select;" + id + "\">[Select]</a>&nbsp;" + blockName + " " + (((block != null) && block.isOnline()) ? "(on)" : "(off)") + "<br1>";
			}
			
			content = content.replaceAll("%blocklist%", selectedBlocks);
		}
		
		// Selected Blocklist
		if (slist.isEmpty())
		{
			content = content.replaceAll("%selectedBlocksList%", "");
		}
		else
		{
			String selectedBlocks = "";
			
			for (Integer id : slist)
			{
				String blockName = CharNameData.getInstance().getNameById(id);
				if (blockName == null)
				{
					continue;
				}
				
				L2PcInstance block = L2World.getInstance().getPlayer(blockName);
				selectedBlocks += "<a action=\"bypass _friendlist_0_;block;deselect;" + id + "\">[Deselect]</a>&nbsp;" + blockName + " " + (((block != null) && block.isOnline()) ? "(on)" : "(off)") + "<br1>";
			}
			
			content = content.replaceAll("%selectedBlocksList%", selectedBlocks);
		}
		
		// Delete button.
		content = content.replaceAll("%deleteMSG%", (delMsg) ? BLOCKLIST_DELETE_BUTTON : "");
		
		separateAndSend(content, activeChar);
	}
	
	private final static void showMailWrite(L2PcInstance activeChar)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "friend/friend-mail.htm");
		if (content == null)
		{
			return;
		}
		
		StringBuilder toList = new StringBuilder();
		for (int id : activeChar.getSelectedFriendList())
		{
			String friendName = CharNameData.getInstance().getNameById(id);
			if (friendName == null)
			{
				continue;
			}
			
			if (toList.length() > 0)
			{
				toList.append(";");
			}
			
			toList.append(friendName);
		}
		
		content = content.replaceAll("%list%", toList.toString());
		
		separateAndSend(content, activeChar);
	}
	
	private static void showFriendsList(L2PcInstance activeChar, boolean delMsg)
	{
		String content = HtmData.getInstance().getHtm(CB_PATH + "friend/friend-list.htm");
		if (content == null)
		{
			return;
		}
		
		// Retrieve activeChar's friendlist and selected
		final List<Integer> list = activeChar.getFriendList();
		final List<Integer> slist = activeChar.getSelectedFriendList();
		
		// Friendlist
		if (list.isEmpty())
		{
			content = content.replaceAll("%friendslist%", "");
		}
		else
		{
			String friends = "";
			
			for (Integer id : list)
			{
				if (slist.contains(id))
				{
					continue;
				}
				
				String friendName = CharNameData.getInstance().getNameById(id);
				if (friendName == null)
				{
					continue;
				}
				
				L2PcInstance friend = L2World.getInstance().getPlayer(friendName);
				friends += "<a action=\"bypass _friendlist_0_;select;" + id + "\">[Select]</a>&nbsp;" + friendName + " " + (((friend != null) && friend.isOnline()) ? "(on)" : "(off)") + "<br1>";
			}
			
			content = content.replaceAll("%friendslist%", friends);
		}
		
		// Selected friendlist
		if (slist.isEmpty())
		{
			content = content.replaceAll("%selectedFriendsList%", "");
		}
		else
		{
			String selectedFriends = "";
			
			for (Integer id : slist)
			{
				String friendName = CharNameData.getInstance().getNameById(id);
				if (friendName == null)
				{
					continue;
				}
				
				L2PcInstance friend = L2World.getInstance().getPlayer(friendName);
				selectedFriends += "<a action=\"bypass _friendlist_0_;deselect;" + id + "\">[Deselect]</a>&nbsp;" + friendName + " " + (((friend != null) && friend.isOnline()) ? "(on)" : "(off)") + "<br1>";
			}
			
			content = content.replaceAll("%selectedFriendsList%", selectedFriends);
		}
		
		// Delete button.
		content = content.replaceAll("%deleteMSG%", (delMsg) ? FRIENDLIST_DELETE_BUTTON : "");
		
		separateAndSend(content, activeChar);
	}
}
