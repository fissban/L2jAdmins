package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class OnVehicleCheckLocation extends AServerPacket
{
	private final L2BoatInstance boat;
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * @param boat
	 * @param x
	 * @param y
	 * @param z
	 */
	public OnVehicleCheckLocation(L2BoatInstance boat, int x, int y, int z)
	{
		this.boat = boat;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x5b);
		writeD(boat.getObjectId());
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(boat.getHeading());
	}
}
