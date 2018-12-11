package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

public class BeginRotation extends AServerPacket
{
	private final int objectId;
	private final int degree;
	private final int side;
	private final int speed;
	
	public BeginRotation(L2Character cha, int degree, int side, int speed)
	{
		objectId = cha.getObjectId();
		this.degree = degree;
		this.side = side;
		this.speed = speed;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x62);
		writeD(objectId);
		writeD(degree);
		writeD(side);
		writeD(speed);
	}
}
