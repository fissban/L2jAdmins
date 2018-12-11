package l2j.gameserver.network.internal.gameserver;

import l2j.gameserver.network.AGamePacket;
import l2j.loginserver.network.SessionKey;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends AGamePacket
{
	public PlayerAuthRequest(String account, SessionKey key)
	{
		writeC(0x05);
		writeS(account);
		writeD(key.playOkID1);
		writeD(key.playOkID2);
		writeD(key.loginOkID1);
		writeD(key.loginOkID2);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
