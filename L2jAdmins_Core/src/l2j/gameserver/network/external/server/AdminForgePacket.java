package l2j.gameserver.network.external.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * This class is made to create packets with any format
 * @author Maktakien
 */
public class AdminForgePacket extends AServerPacket
{
	List<Part> parts = new ArrayList<>();
	
	private class Part
	{
		public byte b;
		public String str;
		
		public Part(byte bb, String string)
		{
			b = bb;
			str = string;
		}
	}
	
	@Override
	public void writeImpl()
	{
		for (Part p : parts)
		{
			generate(p.b, p.str);
		}
	}
	
	/**
	 * @param  b
	 * @param  string
	 * @return
	 */
	public boolean generate(byte b, String string)
	{
		if ((b == 'C') || (b == 'c'))
		{
			writeC(Integer.decode(string));
			return true;
		}
		if ((b == 'D') || (b == 'd'))
		{
			writeD(Integer.decode(string));
			return true;
		}
		if ((b == 'H') || (b == 'h'))
		{
			writeH(Integer.decode(string));
			return true;
		}
		if ((b == 'F') || (b == 'f'))
		{
			writeF(Double.parseDouble(string));
			return true;
		}
		if ((b == 'S') || (b == 's'))
		{
			writeS(string);
			return true;
		}
		if ((b == 'B') || (b == 'b') || (b == 'X') || (b == 'x'))
		{
			writeB(new BigInteger(string).toByteArray());
			return true;
		}
		return false;
	}
	
	public void addPart(byte b, String string)
	{
		parts.add(new Part(b, string));
	}
}
