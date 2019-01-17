package l2j.gameserver.network.internal.gameserver;

import l2j.gameserver.network.AGamePacket;

public class AuthRequest extends AGamePacket
{
	/**
	 * Format: cccSddb c desired ID c accept alternative ID c reserve Host s ExternalHostName d max players d hexid size b hexid
	 * @param id
	 * @param acceptAlternate
	 * @param hexid
	 * @param externalHost
	 * @param internalHost
	 * @param port
	 * @param reserveHost
	 * @param maxplayer
	 */
	public AuthRequest(int id, boolean acceptAlternate, byte[] hexid, String externalHost, int port, boolean reserveHost, int maxplayer)
	{
		writeC(0x01);
		writeC(id);
		writeC(acceptAlternate ? 0x01 : 0x00);
		writeC(reserveHost ? 0x01 : 0x00);
		writeS(externalHost);
		// writeS(internalHost);
		writeH(port);
		writeD(maxplayer);
		writeD(hexid.length);
		writeB(hexid);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
