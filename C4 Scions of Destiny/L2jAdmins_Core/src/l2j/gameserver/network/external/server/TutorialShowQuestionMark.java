package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class TutorialShowQuestionMark extends AServerPacket
{
	private final int blink;
	
	public TutorialShowQuestionMark(int blink)
	{
		this.blink = blink; // this influences the blinking frequency :S
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa1);
		writeD(blink);
	}
}
