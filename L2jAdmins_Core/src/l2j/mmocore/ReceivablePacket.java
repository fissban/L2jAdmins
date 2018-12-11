package l2j.mmocore;

public abstract class ReceivablePacket<T extends MMOClient<?>> extends AbstractPacket<T> implements Runnable
{
	private StringBuilder sb;
	
	public ReceivablePacket()
	{
		//
	}
	
	public abstract boolean read();
	
	@Override
	public abstract void run();
	
	/**
	 * Transfer byte to buf.get(dst) -> dst
	 * @param dst
	 */
	protected final void readB(final byte[] dst)
	{
		buff.get(dst);
	}
	
	/**
	 * Create new byte[] and transfer specific byte[] size
	 * @param  size
	 * @return
	 */
	public final byte[] readB(int size)
	{
		byte[] data = new byte[size];
		buff.get(data);
		return data;
	}
	
	public final void readB(final byte[] dst, final int offset, final int len)
	{
		buff.get(dst, offset, len);
	}
	
	public int readC()
	{
		return buff.get() & 0xFF;
	}
	
	public int readH()
	{
		return buff.getShort() & 0xFFFF;
	}
	
	public int readD()
	{
		return buff.getInt();
	}
	
	public long readQ()
	{
		return buff.getLong();
	}
	
	public double readF()
	{
		return buff.getDouble();
	}
	
	public String readS()
	{
		sb = new StringBuilder();
		char ch;
		
		while ((ch = buff.getChar()) != 0)
		{
			sb.append(ch);
		}
		
		return sb.toString();
	}
}