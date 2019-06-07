package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.task.continuous.GameTimeTaskManager;

public class ClientSetTime extends AServerPacket
{
	
	@Override
	protected void writeImpl()
	{
		writeC(0xEC);
		writeD(GameTimeTaskManager.getInstance().getGameTime()); // time in client minutes
		writeD(6); // constant to match the server time( this determines the speed of the client clock)
		
	}
	
}
