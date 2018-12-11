package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class VehicleInfo extends AServerPacket
{
	private final L2BoatInstance boat;
	
	/**
	 * @param boat
	 */
	public VehicleInfo(L2BoatInstance boat)
	{
		this.boat = boat;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x59);
		writeD(boat.getObjectId());
		writeD(boat.getX());
		writeD(boat.getY());
		writeD(boat.getZ());
		writeD(boat.getHeading());
	}
}
