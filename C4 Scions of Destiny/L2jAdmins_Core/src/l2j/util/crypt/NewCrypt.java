package l2j.util.crypt;

import java.io.IOException;

/**
 * This class ...
 * @version $Revision: 1.3.4.1 $ $Date: 2005/03/27 15:30:09 $
 */
public class NewCrypt
{
	private final BlowfishEngine crypt;
	private final BlowfishEngine decrypt;
	
	public NewCrypt(byte[] blowfishKey)
	{
		crypt = new BlowfishEngine();
		crypt.init(true, blowfishKey);
		decrypt = new BlowfishEngine();
		decrypt.init(false, blowfishKey);
	}
	
	public NewCrypt(String key)
	{
		this(key.getBytes());
	}
	
	/**
	 * Packet is first XOR encoded with <code>key</code>.<br>
	 * Then, the last 4 bytes are overwritten with the the XOR "key".<br>
	 * Thus this assume that there is enough room for the key to fit without overwriting data.
	 * @param raw The raw bytes to be encrypted
	 * @param key The 4 bytes (int) XOR key
	 */
	public static void encXORPass(byte[] raw, int key)
	{
		NewCrypt.encXORPass(raw, 0, raw.length, key);
	}
	
	/**
	 * Packet is first XOR encoded with <code>key</code>.<br>
	 * Then, the last 4 bytes are overwritten with the the XOR "key".<br>
	 * Thus this assume that there is enough room for the key to fit without overwriting data.
	 * @param raw    The raw bytes to be encrypted
	 * @param offset The begining of the data to be encrypted
	 * @param size   Length of the data to be encrypted
	 * @param key    The 4 bytes (int) XOR key
	 */
	public static void encXORPass(byte[] raw, final int offset, final int size, int key)
	{
		int stop = size - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key; // Initial xor key
		
		while (pos < stop)
		{
			edx = raw[pos] & 0xFF;
			edx |= (raw[pos + 1] & 0xFF) << 8;
			edx |= (raw[pos + 2] & 0xFF) << 16;
			edx |= (raw[pos + 3] & 0xFF) << 24;
			
			ecx += edx;
			
			edx ^= ecx;
			
			raw[pos++] = (byte) (edx & 0xFF);
			raw[pos++] = (byte) ((edx >> 8) & 0xFF);
			raw[pos++] = (byte) ((edx >> 16) & 0xFF);
			raw[pos++] = (byte) ((edx >> 24) & 0xFF);
		}
		
		raw[pos++] = (byte) (ecx & 0xFF);
		raw[pos++] = (byte) ((ecx >> 8) & 0xFF);
		raw[pos++] = (byte) ((ecx >> 16) & 0xFF);
		raw[pos] = (byte) ((ecx >> 24) & 0xFF);
	}
	
	public byte[] decrypt(byte[] raw) throws IOException
	{
		byte[] result = new byte[raw.length];
		int count = raw.length / 8;
		
		for (int i = 0; i < count; i++)
		{
			decrypt.processBlock(raw, i * 8, result, i * 8);
		}
		
		return result;
	}
	
	public void decrypt(byte[] raw, final int offset, final int size) throws IOException
	{
		byte[] result = new byte[size];
		int count = size / 8;
		
		for (int i = 0; i < count; i++)
		{
			decrypt.processBlock(raw, offset + (i * 8), result, i * 8);
		}
		
		System.arraycopy(result, 0, raw, offset, size);
	}
	
	public byte[] crypt(byte[] raw) throws IOException
	{
		int count = raw.length / 8;
		byte[] result = new byte[raw.length];
		
		for (int i = 0; i < count; i++)
		{
			crypt.processBlock(raw, i * 8, result, i * 8);
		}
		
		return result;
	}
	
	public void crypt(byte[] raw, final int offset, final int size) throws IOException
	{
		int count = size / 8;
		byte[] result = new byte[size];
		
		for (int i = 0; i < count; i++)
		{
			crypt.processBlock(raw, offset + (i * 8), result, i * 8);
		}
		
		System.arraycopy(result, 0, raw, offset, size);
	}
}
