package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.enums.MountType;
import l2j.gameserver.network.AServerPacket;

public class Ride extends AServerPacket
{
	public enum RideType
	{
		DISMOUNT,
		MOUNT,
	}
	
	private final int id;
	private final RideType rideType;
	private MountType mountType = MountType.NONE;
	private final int rideClassId;
	
	/**
	 * 0x86 UnknownPackets dddd
	 * @param id
	 * @param rideType
	 * @param rideClassId
	 */
	public Ride(int id, RideType rideType, int rideClassId)
	{
		this.id = id; // charobjectID
		this.rideType = rideType;
		this.rideClassId = rideClassId + 1000000; // npcID
		
		switch (rideClassId)
		{
			case 12526: // wind strider
			case 12527: // star strider
			case 12528:
				mountType = MountType.STRIDDER;
				break;
			case 12621:
				mountType = MountType.WYVERN;
				break;
		}
	}
	
	public MountType getMountType()
	{
		return mountType;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x86);
		writeD(id);
		writeD(rideType.ordinal());
		writeD(mountType.ordinal());
		writeD(rideClassId);
	}
}
