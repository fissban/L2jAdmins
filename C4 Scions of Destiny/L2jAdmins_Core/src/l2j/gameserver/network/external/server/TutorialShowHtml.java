package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class TutorialShowHtml extends AServerPacket
{
	private final String html;
	
	public TutorialShowHtml(String html)
	{
		this.html = html;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xa0);
		writeS(html);
	}
}
