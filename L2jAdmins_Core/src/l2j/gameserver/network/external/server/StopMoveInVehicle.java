package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Maktakien
 */
public class StopMoveInVehicle extends AServerPacket
{
	private final L2PcInstance player;
	private final int boatId;
	
	public StopMoveInVehicle(L2PcInstance player, int boatId)
	{
		this.player = player;
		this.boatId = boatId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x72);
		writeD(player.getObjectId());
		writeD(boatId);
		writeD(player.getInBoatPosition().getX());
		writeD(player.getInBoatPosition().getY());
		writeD(player.getInBoatPosition().getZ());
		writeD(player.getInBoatPosition().getHeading());
	}
}
