package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class CameraMode extends AServerPacket
{
	public enum CameraModeType
	{
		THIRD_PERSON,
		FIRST_PERSON,
	}
	
	CameraModeType mode;
	
	/**
	 * Forces client camera mode change
	 * @param mode 0 - third person cam 1 - first person cam
	 */
	public CameraMode(CameraModeType mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf1);
		writeD(mode.ordinal());
	}
}
