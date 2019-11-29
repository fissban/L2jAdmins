package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;
import l2j.loginserver.network.LoginClient;

/**
 * <b>C4:</b><br>
 * Format: ddb <br>
 * d: session id<br>
 * d: protocol revision<br>
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key 0x10 bytes at 0x00 <b>C6</b><br>
 * Format: dd b dddd s<br>
 * d: session id d: protocol revision<br>
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key 0x10 bytes at 0x00<br>
 * d: unknow<br>
 * d: unknow<br>
 * d: unknow<br>
 * d: unknow<br>
 * s: blowfish key<br>
 */
public final class Init extends ALoginServerPacket
{
	private final int sessionId;
	private final byte[] publicKey;
	// private final byte[] blowfishKey;
	
	public Init(LoginClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}
	
	public Init(byte[] publicKey, byte[] blowfishKey, int sessionId)
	{
		this.sessionId = sessionId;
		this.publicKey = publicKey;
		// this.blowfishKey = blowfishKey;
	}
	
	@Override
	public void write()
	{
		writeC(0x00); // init packet id
		
		writeD(sessionId); // session id
		writeD(0x0000c621); // protocol revision
		
		writeB(publicKey); // RSA Public Key
		
		// unk GG related? always 0x00 in C4
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		
		// writeD(0x29DD954E);
		// writeD(0x77C39CFC);
		// writeD(0x97ADB620);
		// writeD(0x07BDE0F7);
		// writeB(blowfishKey); // BlowFish key
		// writeC(0x00); // null termination ;)
	}
}
