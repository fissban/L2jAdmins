package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.manager.pc.party.PartyMatchRoom;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Gnacik
 */
public class PartyMatchDetail extends AServerPacket
{
	private final PartyMatchRoom room;
	
	/**
	 * @param room
	 */
	public PartyMatchDetail(PartyMatchRoom room)
	{
		this.room = room;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x97);
		
		writeD(room.getId()); // Room ID
		writeD(room.getMaxMembers()); // Max Members
		writeD(room.getMinLvl()); // Level Min
		writeD(room.getMaxLvl()); // Level Max
		writeD(room.getLootType()); // Loot Type
		writeD(room.getLocation()); // Room Location
		writeS(room.getTitle()); // Room title
	}
}
