package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class VehicleDeparture extends AServerPacket
{
	private final L2BoatInstance boat;
	
	/**
	 * @param boat
	 */
	public VehicleDeparture(L2BoatInstance boat)
	{
		this.boat = boat;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x5a);
		writeD(boat.getObjectId());
		writeD((int) boat.getStat().getMoveSpeed());
		writeD(boat.getStat().getRotationSpeed());
		writeD(boat.getXdestination());
		writeD(boat.getYdestination());
		writeD(boat.getZdestination());
	}
}
