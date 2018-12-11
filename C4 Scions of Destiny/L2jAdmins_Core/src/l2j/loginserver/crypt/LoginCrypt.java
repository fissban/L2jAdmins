package l2j.loginserver.crypt;

import java.io.IOException;

import l2j.util.Rnd;
import l2j.util.crypt.Checksum;
import l2j.util.crypt.NewCrypt;

public class LoginCrypt
{
	private static final byte[] STATIC_BLOWFISH_KEY =
	{
		(byte) 0x6b,
		(byte) 0x60,
		(byte) 0xcb,
		(byte) 0x5b,
		(byte) 0x82,
		(byte) 0xce,
		(byte) 0x90,
		(byte) 0xb1
		
		// TODO C6
		// (byte) 0xcc,
		// (byte) 0x2b,
		// (byte) 0x6c,
		// (byte) 0x55,
		// (byte) 0x6c,
		// (byte) 0x6c,
		// (byte) 0x6c,
		// (byte) 0x6c
	};
	
	private NewCrypt staticCrypt;
	private NewCrypt crypt;
	private boolean isStatic = false;// TODO true
	
	public void setKey(byte[] key)
	{
		staticCrypt = new NewCrypt(STATIC_BLOWFISH_KEY);
		crypt = new NewCrypt(key);
	}
	
	public boolean decrypt(byte[] raw, final int offset, final int size) throws IOException
	{
		crypt.decrypt(raw, offset, size);
		return Checksum.verify(raw, offset, size);
	}
	
	public int encrypt(byte[] raw, final int offset, int size) throws IOException
	{
		// reserve checksum
		size += 4;
		
		if (isStatic)
		{
			// reserve for XOR "key"
			size += 4;
			// padding
			size += 8 - (size % 8);
			NewCrypt.encXORPass(raw, offset, size, Rnd.nextInt());
			staticCrypt.crypt(raw, offset, size);
			
			isStatic = false;
		}
		else
		{
			// padding
			size += 8 - (size % 8);
			Checksum.append(raw, offset, size);
			crypt.crypt(raw, offset, size);
		}
		return size;
	}
}
