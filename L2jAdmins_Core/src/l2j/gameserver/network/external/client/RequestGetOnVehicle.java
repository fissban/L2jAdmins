package l2j.gameserver.network.external.client;

import l2j.gameserver.data.BoatData;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.GetOnVehicle;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGetOnVehicle extends AClientPacket
{
	private int boatId;
	
	private int x;
	private int y;
	private int z;
	
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
		
		L2BoatInstance boat = null;
		
		if (activeChar.isInBoat())
		{
			boat = activeChar.getBoat();
			if (boat.getObjectId() != boatId)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		else
		{
			boat = BoatData.get(boatId);
			if ((boat == null) || boat.isMoving() || !activeChar.isInsideRadius(boat, 1000, true, false))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		LocationHolder pos = new LocationHolder(x, y, z, activeChar.getHeading());
		activeChar.setInBoatPosition(pos);
		activeChar.setBoat(boat);
		activeChar.broadcastPacket(new GetOnVehicle(activeChar.getObjectId(), boat.getObjectId(), pos));
		activeChar.setXYZ(boat.getX(), boat.getY(), boat.getZ());
		activeChar.revalidateZone(true);
	}
}
