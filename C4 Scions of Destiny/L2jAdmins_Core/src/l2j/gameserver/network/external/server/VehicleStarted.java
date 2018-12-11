package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author Kerberos
 */
public class VehicleStarted extends AServerPacket
{
	private final int objectId;
	private final int state;
	
	/**
	 * @param objectId
	 * @param state
	 */
	public VehicleStarted(int objectId, int state)
	{
		this.objectId = objectId;
		this.state = state;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xba);
		writeD(objectId);
		writeD(state);
	}
}
