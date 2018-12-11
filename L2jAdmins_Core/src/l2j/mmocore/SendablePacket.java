package l2j.mmocore;

public abstract class SendablePacket<T extends MMOClient<?>> extends AbstractPacket<T>
{
	public void putInt(final int value)
	{
		buff.putInt(value);
	}
	
	public void putDouble(final double value)
	{
		buff.putDouble(value);
	}
	
	public void putFloat(final float value)
	{
		buff.putFloat(value);
	}
	
	public void writeC(final int data)
	{
		buff.put((byte) data);
	}
	
	public void writeF(final double value)
	{
		buff.putDouble(value);
	}
	
	public void writeH(final int value)
	{
		buff.putShort((short) value);
	}
	
	public void writeD(final int value)
	{
		buff.putInt(value);
	}
	
	public void writeQ(final long value)
	{
		buff.putLong(value);
	}
	
	public void writeB(final byte[] data)
	{
		buff.put(data);
	}
	
	public void writeS(final String text)
	{
		if (text != null)
		{
			final int len = text.length();
			for (int i = 0; i < len; i++)
			{
				buff.putChar(text.charAt(i));
			}
		}
		
		buff.putChar('\000');
	}
	
	public abstract void write();
}