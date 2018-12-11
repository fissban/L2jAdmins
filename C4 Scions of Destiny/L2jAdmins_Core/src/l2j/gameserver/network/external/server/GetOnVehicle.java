package l2j.gameserver.network.external.server;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class GetOnVehicle extends AServerPacket
{
	private final int charObjId;
	private final int boatObjId;
	private final LocationHolder pos;
	
	/**
	 * @param charObjId
	 * @param boatObjId
	 * @param pos
	 */
	public GetOnVehicle(int charObjId, int boatObjId, LocationHolder pos)
	{
		this.charObjId = charObjId;
		this.boatObjId = boatObjId;
		this.pos = pos;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x5c);
		writeD(charObjId);
		writeD(boatObjId);
		writeD(pos.getX());
		writeD(pos.getY());
		writeD(pos.getZ());
	}
}
