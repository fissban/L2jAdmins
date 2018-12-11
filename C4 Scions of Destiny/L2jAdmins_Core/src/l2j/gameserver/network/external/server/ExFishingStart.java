package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * Format (ch)ddddd
 * @author -Wooden-
 */
public class ExFishingStart extends AServerPacket
{
	private final L2Character character;
	private final LocationHolder loc;
	private final int fishType;
	
	public ExFishingStart(L2Character character, int fishType, LocationHolder loc)
	{
		this.character = character;
		this.fishType = fishType;
		this.loc = loc;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x13);
		writeD(character.getObjectId());
		writeD(fishType); // fish type
		writeD(loc.getX());
		writeD(loc.getY());
		writeD(loc.getZ());
	}
}
