package l2j.gameserver.network;

/**
 * @author KenM, fissban
 */
public class GameCrypt
{
	// TODO
	// C4: 8 byts
	// C6: 16 byts
	private static final int ZISE = 8;
	
	private final byte[] inKey = new byte[ZISE];
	private final byte[] outKey = new byte[ZISE];
	private boolean enabled = false;
	
	public void setKey(byte[] key)
	{
		System.arraycopy(key, 0, inKey, 0, ZISE);
		System.arraycopy(key, 0, outKey, 0, ZISE);
	}
	
	public void decrypt(byte[] raw, final int offset, final int size)
	{
		if (!enabled)
		{
			enabled = true;
			return;
		}
		
		var temp = 0;
		for (var i = 0; i < size; i++)
		{
			var temp2 = raw[offset + i] & 0xFF;
			raw[offset + i] = (byte) (temp2 ^ inKey[i & (ZISE - 1)] ^ temp);
			temp = temp2;
		}
		
		var pos = ZISE - 8;
		
		var old = inKey[pos++] & 0xff;
		old |= (inKey[pos++] << 8) & 0xff00;
		old |= (inKey[pos++] << 0x10) & 0xff0000;
		old |= (inKey[pos++] << 0x18) & 0xff000000;
		
		old += size;
		
		pos = ZISE - 8;
		inKey[pos++] = (byte) (old & 0xff);
		inKey[pos++] = (byte) ((old >> 0x08) & 0xff);
		inKey[pos++] = (byte) ((old >> 0x10) & 0xff);
		inKey[pos++] = (byte) ((old >> 0x18) & 0xff);
	}
	
	public void encrypt(byte[] raw, final int offset, final int size)
	{
		var temp = 0;
		for (var i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			temp = temp2 ^ outKey[i & (ZISE - 1)] ^ temp;
			raw[offset + i] = (byte) temp;
		}
		
		var pos = ZISE - 8;
		var old = outKey[pos++] & 0xff;
		old |= (outKey[pos++] << 8) & 0xff00;
		old |= (outKey[pos++] << 0x10) & 0xff0000;
		old |= (outKey[pos++] << 0x18) & 0xff000000;
		
		old += size;
		
		pos = ZISE - 8;
		outKey[pos++] = (byte) (old & 0xff);
		outKey[pos++] = (byte) ((old >> 0x08) & 0xff);
		outKey[pos++] = (byte) ((old >> 0x10) & 0xff);
		outKey[pos++] = (byte) ((old >> 0x18) & 0xff);
	}
}
