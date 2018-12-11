package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;
import l2j.loginserver.network.SessionKey;

/**
 * C4: Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ?<br>
 * C6: Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ? b: 16 bytes - unknown<br>
 */
public final class LoginOk extends ALoginServerPacket
{
	private final int loginOk1, loginOk2;
	
	public LoginOk(SessionKey sessionKey)
	{
		loginOk1 = sessionKey.loginOkID1;
		loginOk2 = sessionKey.loginOkID2;
	}
	
	@Override
	public void write()
	{
		writeC(0x03);
		writeD(loginOk1);
		writeD(loginOk2);
		writeD(0x00);
		writeD(0x00);
		writeD(0x000003ea);
		writeD(0x00);
		writeD(0x00);
		writeD(0x02); // in C6 use 0x00
		// in C6
		// writeB(new byte[16]);
	}
}