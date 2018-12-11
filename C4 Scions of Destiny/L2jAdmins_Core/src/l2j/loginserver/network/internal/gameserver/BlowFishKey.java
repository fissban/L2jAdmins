package l2j.loginserver.network.internal.gameserver;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import l2j.loginserver.network.AClientPacket;

public class BlowFishKey extends AClientPacket
{
	protected static final Logger LOG = Logger.getLogger(BlowFishKey.class.getName());
	
	byte[] key;
	
	public BlowFishKey(byte[] decrypt, RSAPrivateKey privateKey)
	{
		super(decrypt);
		int size = readD();
		byte[] tempKey = readB(size);
		try
		{
			byte[] tempDecryptKey;
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
			tempDecryptKey = rsaCipher.doFinal(tempKey);
			// there are nulls before the key we must remove them
			int i = 0;
			int len = tempDecryptKey.length;
			for (; i < len; i++)
			{
				if (tempDecryptKey[i] != 0)
				{
					break;
				}
			}
			key = new byte[len - i];
			System.arraycopy(tempDecryptKey, i, key, 0, len - i);
		}
		catch (GeneralSecurityException e)
		{
			LOG.severe("Error While decrypting blowfish key (RSA)");
			e.printStackTrace();
		}
	}
	
	public byte[] getKey()
	{
		return key;
	}
}