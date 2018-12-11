package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.GetOffVehicle;
import l2j.gameserver.network.external.server.StopMoveInVehicle;

/**
 * @author Maktakien
 */
public class RequestGetOffVehicle extends AClientPacket
{
	private int boatId, x, y, z;
	
	@Override
	protected void readImpl()
	{
		boatId = readD();
		x = readD();
		y = readD();
		z = readD();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!activeChar.isInBoat() || (activeChar.getBoatId() != boatId) || activeChar.getBoat().isMoving() || !activeChar.isInsideRadius(x, y, z, 1000, true, false))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getBoat().isMoving())
		{
			activeChar.broadcastPacket(new StopMoveInVehicle(activeChar, boatId));
		}
		
		activeChar.setBoat(null);
		sendPacket(ActionFailed.STATIC_PACKET);
		activeChar.broadcastPacket(new GetOffVehicle(activeChar.getObjectId(), boatId, x, y, z));
		activeChar.setXYZ(x, y, z + 50);
		activeChar.revalidateZone(true);
	}
}
