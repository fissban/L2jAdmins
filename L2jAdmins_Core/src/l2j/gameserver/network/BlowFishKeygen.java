package l2j.gameserver.network;

/**
 * Blowfish keygen for GameServer client connections
 * @author KenM
 */
public class BlowFishKeygen
{
	// TODO C4 use 8 bytes
	// TODO C6 use 16 bits
	private static final byte[] CRYPT_KEYS =
	{
		(byte) 0x94,
		(byte) 0x35,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0xa1,
		(byte) 0x6c,
		(byte) 0x54,
		(byte) 0x87
		// CRYPT_KEYS[i][8] = (byte) 0xc8;
		// CRYPT_KEYS[i][9] = (byte) 0x27;
		// CRYPT_KEYS[i][10] = (byte) 0x93;
		// CRYPT_KEYS[i][11] = (byte) 0x01;
		// CRYPT_KEYS[i][12] = (byte) 0xa1;
		// CRYPT_KEYS[i][13] = (byte) 0x6c;
		// CRYPT_KEYS[i][14] = (byte) 0x31;
		// CRYPT_KEYS[i][15] = (byte) 0x97;
	};
	
	private BlowFishKeygen()
	{
		//
	}
	
	/**
	 * Returns a key from this keygen pool, the logical ownership is retained by this keygen.<BR>
	 * Thus when getting a key with interests other then read-only a copy must be performed.<BR>
	 * @return A key from this keygen pool.
	 */
	public static byte[] getKey()
	{
		return CRYPT_KEYS;
	}
}
