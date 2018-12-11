package l2j.loginserver.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class AServerPacket
{
	ByteArrayOutputStream bao;
	
	protected AServerPacket()
	{
		bao = new ByteArrayOutputStream();
	}
	
	protected void writeD(int value)
	{
		bao.write(value & 0xff);
		bao.write((value >> 8) & 0xff);
		bao.write((value >> 16) & 0xff);
		bao.write((value >> 24) & 0xff);
	}
	
	protected void writeH(int value)
	{
		bao.write(value & 0xff);
		bao.write((value >> 8) & 0xff);
	}
	
	protected void writeC(int value)
	{
		bao.write(value & 0xff);
	}
	
	protected void writeF(double org)
	{
		var value = Double.doubleToRawLongBits(org);
		bao.write((int) (value & 0xff));
		bao.write((int) ((value >> 8) & 0xff));
		bao.write((int) ((value >> 16) & 0xff));
		bao.write((int) ((value >> 24) & 0xff));
		bao.write((int) ((value >> 32) & 0xff));
		bao.write((int) ((value >> 40) & 0xff));
		bao.write((int) ((value >> 48) & 0xff));
		bao.write((int) ((value >> 56) & 0xff));
	}
	
	protected void writeS(String text)
	{
		try
		{
			if (text != null)
			{
				bao.write(text.getBytes("UTF-16LE"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		bao.write(0);
		bao.write(0);
	}
	
	protected void writeB(byte[] array)
	{
		try
		{
			bao.write(array);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getLength()
	{
		return bao.size() + 2;
	}
	
	public byte[] getBytes()
	{
		writeD(0x00); // reserve for checksum
		
		var padding = bao.size() % 8;
		if (padding != 0)
		{
			for (var i = padding; i < 8; i++)
			{
				writeC(0x00);
			}
		}
		
		return bao.toByteArray();
	}
	
	public abstract byte[] getContent() throws IOException;
}