package l2j.gameserver.network.external.client;

import l2j.gameserver.data.BoatData;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.MoveToLocationInVehicle;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestMoveToLocationInVehicle extends AClientPacket
{
	private int boatId;
	private LocationHolder pos;
	private LocationHolder originPos;
	
	@Override
	protected void readImpl()
	{
		int x, y, z;
		boatId = readD(); // objectId of boat
		x = readD();
		y = readD();
		z = readD();
		pos = new LocationHolder(x, y, z);
		x = readD();
		y = readD();
		z = readD();
		originPos = new LocationHolder(x, y, z);
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isSitting() || (activeChar.isAttackingNow() || (activeChar.isCastingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getType() == WeaponType.BOW))))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getPet() != null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.RELEASE_PET_ON_BOAT));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2BoatInstance boat;
		if (activeChar.isInBoat())
		{
			if (activeChar.getBoatId() != boatId)
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			boat = activeChar.getBoat();
		}
		else
		{
			boat = BoatData.get(boatId);
			if ((boat == null) || !boat.isInsideRadius(activeChar, 300, true, false))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			activeChar.setBoat(boat);
		}
		
		activeChar.setInBoatPosition(pos);
		activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, pos, originPos));
	}
}
