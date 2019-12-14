package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoomList;
import l2j.gameserver.network.AServerPacket;

/**
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class PartyMatchList extends AServerPacket
{
	private final L2PcInstance cha;
	private final int loc;
	private final int lim;
	private final List<PartyMatchRoom> rooms;
	
	/**
	 * @param player
	 * @param auto
	 * @param location
	 * @param limit
	 */
	public PartyMatchList(L2PcInstance player, int auto, int location, int limit)
	{
		cha = player;
		loc = location;
		lim = limit;
		rooms = new ArrayList<>();
	}
	
	@Override
	public void writeImpl()
	{
		for (PartyMatchRoom room : PartyMatchRoomList.getInstance().getRooms())
		{
			if ((room.getMembersCount() < 1) || (room.getOwner() == null) || (!room.getOwner().isOnline()) || (room.getOwner().getPartyRoom() != room.getId()))
			{
				PartyMatchRoomList.getInstance().deleteRoom(room.getId());
				continue;
			}
			
			if ((loc > 0) && (loc != room.getLocation()))
			{
				continue;
			}
			
			if ((lim == 0) && ((cha.getLevel() < room.getMinLvl()) || (cha.getLevel() > room.getMaxLvl())))
			{
				continue;
			}
			
			rooms.add(room);
		}
		
		int size = rooms.size();
		
		writeC(0x96);
		
		if (size > 0)
		{
			writeD(1);
		}
		else
		{
			writeD(0);
		}
		
		writeD(size);
		
		for (PartyMatchRoom room : rooms)
		{
			writeD(room.getId());
			writeS(room.getTitle());
			writeD(room.getLocation());
			writeD(room.getMinLvl());
			writeD(room.getMaxLvl());
			writeD(room.getMembersCount());
			writeD(room.getMaxMembers());
			writeS(room.getOwner().getName());
		}
	}
}
