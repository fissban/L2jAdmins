package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * @author fissban
 */
public final class FlyToLocation extends AServerPacket
{
	private final L2Character cha;
	private final int destX, destY, destZ;
	private final FlyType type;
	
	public enum FlyType
	{
		THROW_UP,
		THROW_HORIZONTAL,
		DUMMY; // no effect
	}
	
	public FlyToLocation(L2Character cha, int destX, int destY, int destZ, FlyType type)
	{
		this.cha = cha;
		this.destX = destX;
		this.destY = destY;
		this.destZ = destZ;
		this.type = type;
	}
	
	public FlyToLocation(L2Character cha, L2Object dest, FlyType type)
	{
		this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xC5);
		writeD(cha.getObjectId());
		writeD(destX);
		writeD(destY);
		writeD(destZ);
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
		writeD(type.ordinal());
	}
}
