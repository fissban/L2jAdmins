package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ValidateLocationInVehicle extends AServerPacket
{
	private final L2PcInstance player;
	
	/**
	 * 0x73 ValidateLocationInVehicle hdd
	 * @param player
	 */
	public ValidateLocationInVehicle(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x73);
		writeD(player.getObjectId());
		writeD(player.getBoat().getObjectId());
		writeD(player.getInBoatPosition().getX());
		writeD(player.getInBoatPosition().getY());
		writeD(player.getInBoatPosition().getZ());
		writeD(player.getInBoatPosition().getHeading());
	}
}
