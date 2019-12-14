package l2j.gameserver.model.actor.manager.pc.party;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.ExClosePartyRoom;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author Gnacik
 */
public class PartyMatchRoomList
{
	private int maxid;
	private final Map<Integer, PartyMatchRoom> rooms = new HashMap<>();
	
	public PartyMatchRoomList()
	{
		//
	}
	
	public synchronized void addPartyMatchRoom(PartyMatchRoom room)
	{
		rooms.put(room.getId(), room);
	}
	
	public void deleteRoom(int id)
	{
		for (L2PcInstance member : getRoom(id).getMembers())
		{
			if (member == null)
			{
				continue;
			}
			
			member.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			member.sendPacket(SystemMessage.PARTY_ROOM_DISBANDED);
			
			member.setPartyRoom(0);
			member.broadcastUserInfo();
		}
		rooms.remove(id);
	}
	
	public PartyMatchRoom getRoom(int id)
	{
		return rooms.get(id);
	}
	
	public Collection<PartyMatchRoom> getRooms()
	{
		return rooms.values();
	}
	
	public int getPartyMatchRoomCount()
	{
		return rooms.size();
	}
	
	public int getAutoIncrementId()
	{
		// reset all ids as free
		// if room list is empty
		if (rooms.isEmpty())
		{
			maxid = 0;
		}
		
		maxid++;
		
		return maxid;
	}
	
	public PartyMatchRoom getPlayerRoom(L2PcInstance player)
	{
		for (PartyMatchRoom room : rooms.values())
		{
			for (L2PcInstance member : room.getMembers())
			{
				if (member.equals(player))
				{
					return room;
				}
			}
		}
		return null;
	}
	
	public int getPlayerRoomId(L2PcInstance player)
	{
		for (PartyMatchRoom room : rooms.values())
		{
			for (L2PcInstance member : room.getMembers())
			{
				if (member.equals(player))
				{
					return room.getId();
				}
			}
		}
		return -1;
	}
	
	public static PartyMatchRoomList getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PartyMatchRoomList INSTANCE = new PartyMatchRoomList();
	}
}
