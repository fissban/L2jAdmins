package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class TutorialEnableClientEvent extends AServerPacket
{
	private int eventId = 0;
	
	public TutorialEnableClientEvent(int event)
	{
		eventId = event;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa2);
		writeD(eventId);
	}
}
