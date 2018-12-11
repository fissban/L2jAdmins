package main.packets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.holders.objects.NpcHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * OriginalPacket -> PartyMemberPosition
 * @author fissban
 */
public class ObjectPosition extends AServerPacket
{
	private final Map<Integer, LocationHolder> locations = new HashMap<>();
	
	public ObjectPosition(List<NpcHolder> chests)
	{
		locations.clear();
		
		chests.stream().filter(c -> c.getInstance() != null).forEach(c -> locations.put(c.getObjectId(), c.getInstance().getWorldPosition()));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa7);
		writeD(locations.size());
		for (Map.Entry<Integer, LocationHolder> entry : locations.entrySet())
		{
			LocationHolder loc = entry.getValue();
			writeD(entry.getKey());
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
		}
	}
	
}
