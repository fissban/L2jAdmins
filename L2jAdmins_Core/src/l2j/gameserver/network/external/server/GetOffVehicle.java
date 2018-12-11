package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class GetOffVehicle extends AServerPacket
{
	private final int x;
	private final int y;
	private final int z;
	private final int charObjId;
	private final int boatObjId;
	
	/**
	 * @param charObjId
	 * @param boatObjId
	 * @param x
	 * @param y
	 * @param z
	 */
	public GetOffVehicle(int charObjId, int boatObjId, int x, int y, int z)
	{
		this.charObjId = charObjId;
		this.boatObjId = boatObjId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		
		writeC(0x5d);
		writeD(charObjId);
		writeD(boatObjId);
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
