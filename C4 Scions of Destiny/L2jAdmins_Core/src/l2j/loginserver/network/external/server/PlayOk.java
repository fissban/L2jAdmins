package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;
import l2j.loginserver.network.SessionKey;

public final class PlayOk extends ALoginServerPacket
{
	private final int playOk1, playOk2;
	
	public PlayOk(SessionKey sessionKey)
	{
		playOk1 = sessionKey.playOkID1;
		playOk2 = sessionKey.playOkID2;
	}
	
	@Override
	public void write()
	{
		writeC(0x07);
		writeD(playOk1);
		writeD(playOk2);
	}
}