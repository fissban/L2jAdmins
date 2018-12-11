package l2j.gameserver.network.internal.gameserver;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import l2j.gameserver.network.AGamePacket;

/**
 * @author -Wooden-
 */
public class BlowFishKey extends AGamePacket
{
	private static final Logger LOG = Logger.getLogger(BlowFishKey.class.getName());
	
	/**
	 * @param blowfishKey
	 * @param publicKey
	 */
	public BlowFishKey(byte[] blowfishKey, RSAPublicKey publicKey)
	{
		writeC(0x00);
		
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encrypted = rsaCipher.doFinal(blowfishKey);
			writeD(encrypted.length);
			writeB(encrypted);
		}
		catch (GeneralSecurityException e)
		{
			LOG.severe("Error While encrypting blowfish key for transmision (Crypt error)");
			e.printStackTrace();
		}
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
