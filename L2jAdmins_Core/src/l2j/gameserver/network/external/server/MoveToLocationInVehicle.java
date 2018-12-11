package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class MoveToLocationInVehicle extends AServerPacket
{
	private final int playerObjId;
	private final int boatObjId;
	private final LocationHolder destination;
	private final LocationHolder origin;
	
	/**
	 * @param player
	 * @param destination
	 * @param origin
	 */
	public MoveToLocationInVehicle(L2PcInstance player, LocationHolder destination, LocationHolder origin)
	{
		playerObjId = player.getObjectId();
		boatObjId = player.getBoat().getObjectId();
		this.destination = destination;
		this.origin = origin;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x71);
		
		writeD(playerObjId);
		
		writeD(boatObjId);
		writeD(destination.getX());
		writeD(destination.getY());
		writeD(destination.getY());
		writeD(origin.getX());
		writeD(origin.getY());
		writeD(origin.getZ());
	}
}
