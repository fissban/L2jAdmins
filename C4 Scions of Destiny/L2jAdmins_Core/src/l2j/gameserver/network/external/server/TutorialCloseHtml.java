package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class TutorialCloseHtml extends AServerPacket
{
	public static final TutorialCloseHtml STATIC_PACKET = new TutorialCloseHtml();
	
	@Override
	public void writeImpl()
	{
		writeC(0xa3);
	}
}
