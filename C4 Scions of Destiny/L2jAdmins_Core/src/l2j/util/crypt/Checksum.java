/*
 * Copyright (C) 2014-2018 L2jAdmins
 * This file is part of L2jAdmins.
 * L2jAdmins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2jAdmins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2j.util.crypt;

/**
 * @author fissban
 */
public class Checksum
{
	public static boolean verify(byte[] raw)
	{
		return verify(raw, 0, raw.length);
	}
	
	public static boolean verify(byte[] raw, final int offset, final int size)
	{
		// check if size is multiple of 4 and if there is more then only the checksum
		if (((size & 3) != 0) || (size <= 4))
		{
			return false;
		}
		
		long chksum = 0;
		int count = size - 4;
		long check = -1;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			check = raw[i] & 0xff;
			check |= (raw[i + 1] << 8) & 0xff00;
			check |= (raw[i + 2] << 0x10) & 0xff0000;
			check |= (raw[i + 3] << 0x18) & 0xff000000;
			
			chksum ^= check;
		}
		
		check = raw[i] & 0xff;
		check |= (raw[i + 1] << 8) & 0xff00;
		check |= (raw[i + 2] << 0x10) & 0xff0000;
		check |= (raw[i + 3] << 0x18) & 0xff000000;
		
		return check == chksum;
	}
	
	public static void append(byte[] raw)
	{
		append(raw, 0, raw.length);
	}
	
	public static void append(byte[] raw, final int offset, final int size)
	{
		long chksum = 0;
		int count = size - 4;
		long ecx;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			ecx = raw[i] & 0xff;
			ecx |= (raw[i + 1] << 8) & 0xff00;
			ecx |= (raw[i + 2] << 0x10) & 0xff0000;
			ecx |= (raw[i + 3] << 0x18) & 0xff000000;
			
			chksum ^= ecx;
		}
		
		ecx = raw[i] & 0xff;
		ecx |= (raw[i + 1] << 8) & 0xff00;
		ecx |= (raw[i + 2] << 0x10) & 0xff0000;
		ecx |= (raw[i + 3] << 0x18) & 0xff000000;
		
		raw[i] = (byte) (chksum & 0xff);
		raw[i + 1] = (byte) ((chksum >> 0x08) & 0xff);
		raw[i + 2] = (byte) ((chksum >> 0x10) & 0xff);
		raw[i + 3] = (byte) ((chksum >> 0x18) & 0xff);
	}
}
