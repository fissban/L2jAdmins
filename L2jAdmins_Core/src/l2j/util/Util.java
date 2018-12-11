package l2j.util;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class Util
{
	public static boolean isInternalIP(String ipAddress)
	{
		return ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") ||
		// ipAddress.startsWith("172.16.") ||
		// Removed because there are some net IPs in this range.
		// TODO: Use regexp or something to only include 172.16.0.0 => 172.16.31.255
			ipAddress.startsWith("127.0.0.1");
	}
	
	/**
	 * @param  raw
	 * @return
	 */
	public static String printData(byte[] raw)
	{
		return printData(raw, raw.length);
	}
	
	/**
	 * @param  data
	 * @param  len
	 * @return
	 */
	public static String printData(byte[] data, int len)
	{
		final StringBuilder sb = new StringBuilder();
		
		int counter = 0;
		
		for (int i = 0; i < len; i++)
		{
			if ((counter % 16) == 0)
			{
				sb.append(fillHex(i, 4) + ": ");
			}
			
			sb.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16)
			{
				sb.append("   ");
				
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++)
				{
					final int t1 = data[charpoint++];
					if ((t1 > 0x1f) && (t1 < 0x80))
					{
						sb.append((char) t1);
					}
					else
					{
						sb.append('.');
					}
				}
				
				sb.append("\n");
				counter = 0;
			}
		}
		
		final int rest = data.length % 16;
		if (rest > 0)
		{
			for (int i = 0; i < (17 - rest); i++)
			{
				sb.append("   ");
			}
			
			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++)
			{
				final int t1 = data[charpoint++];
				if ((t1 > 0x1f) && (t1 < 0x80))
				{
					sb.append((char) t1);
				}
				else
				{
					sb.append('.');
				}
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	private static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);
		
		for (int i = number.length(); i < digits; i++)
		{
			number = "0" + number;
		}
		
		return number;
	}
}
