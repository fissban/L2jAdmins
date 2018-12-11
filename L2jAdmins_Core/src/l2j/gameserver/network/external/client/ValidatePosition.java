package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.GetOnVehicle;
import l2j.gameserver.network.external.server.PartyMemberPosition;
import l2j.gameserver.network.external.server.ValidateLocation;

/**
 * This class ...
 * @version $Revision: 1.13.4.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class ValidatePosition extends AClientPacket
{
	private int x;
	private int y;
	private int z;
	private int heading;
	private int data; // vehicle id
	
	@Override
	protected void readImpl()
	{
		x = readD();
		y = readD();
		z = readD();
		heading = readD();
		data = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if ((activeChar == null) || activeChar.isTeleporting() || activeChar.inObserverMode())
		{
			return;
		}
		
		int realX = activeChar.getX();
		int realY = activeChar.getY();
		int realZ = activeChar.getZ();
		
		if ((x == 0) && (y == 0))
		{
			if (realX != 0)
			{
				return;
			}
		}
		
		int dx, dy, dz;
		double diffSq;
		
		if (activeChar.isInBoat())
		{
			dx = x - activeChar.getInBoatPosition().getX();
			dy = y - activeChar.getInBoatPosition().getY();
			dz = z - activeChar.getInBoatPosition().getZ();
			diffSq = ((dx * dx) + (dy * dy));
			if (diffSq > 250000)
			{
				sendPacket(new GetOnVehicle(activeChar.getObjectId(), data, activeChar.getInBoatPosition()));
			}
			
			return;
		}
		
		if (activeChar.isFalling(z))
		{
			return;
		}
		
		dx = x - realX;
		dy = y - realY;
		dz = z - realZ;
		diffSq = ((dx * dx) + (dy * dy));
		
		if ((activeChar.getParty() != null) && (activeChar.getLastPartyPositionDistance(x, y, z) > 150))
		{
			activeChar.setLastPartyPosition(x, y, z);
			activeChar.getParty().broadcastToPartyMembers(activeChar, new PartyMemberPosition(activeChar));
		}
		
		if (activeChar.isFlying() || activeChar.isInsideZone(ZoneType.WATER))
		{
			activeChar.setXYZ(realX, realY, z);
			if (diffSq > 90000)
			{
				activeChar.sendPacket(new ValidateLocation(activeChar));
			}
		}
		else if ((diffSq < 360000) && ((diffSq > 250000) || (Math.abs(dz) > 200))) // if too large, messes observation
		{
			if ((Math.abs(dz) > 200) && (Math.abs(dz) < 1500) && (Math.abs(z - activeChar.getClientLoc().getZ()) < 800))
			{
				activeChar.setXYZ(realX, realY, z);
				realZ = z;
			}
			else
			{
				activeChar.sendPacket(new ValidateLocation(activeChar));
			}
		}
		activeChar.setClientLoc(x, y, z, heading);
	}
}
