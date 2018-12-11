package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.StopMoveInVehicle;

/**
 * @author Maktakien
 */
public class CannotMoveAnymoreInVehicle extends AClientPacket
{
	private int x;
	private int y;
	private int z;
	private int heading;
	private int boatId;
	
	@Override
	protected void readImpl()
	{
		boatId = readD();
		x = readD();
		y = readD();
		z = readD();
		heading = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		if (player.isInBoat())
		{
			if (player.getBoatId() == boatId)
			{
				player.setInBoatPosition(new LocationHolder(x, y, z, heading));
				player.setHeading(heading);
				player.broadcastPacket(new StopMoveInVehicle(player, boatId));
			}
		}
	}
}
