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
package l2j.mmocore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import l2j.gameserver.network.AGamePacket;
import l2j.loginserver.network.AServerPacket;
import l2j.util.crypt.Checksum;
import l2j.util.crypt.NewCrypt;

/**
 * @author fissban
 */
public class AbstractThread extends Thread
{
	protected static final Logger LOG = Logger.getLogger(AbstractThread.class.getName());
	
	public static final String CRYPT = "_;v.]05-31!|+-%xT!^[$\00";
	
	/**
	 * The BlowFish engine used to encrypt packets<br>
	 * It is first initialized with a unified key:<br>
	 * "_;v.]05-31!|+-%xT!^[$\00"<br>
	 * <br>
	 * and then after handshake, with a new key sent by<br>
	 * login server during the handshake. This new key is stored<br>
	 * in {@link #CRYPT}
	 */
	public NewCrypt blowfish;
	public byte[] blowfishKey;
	// in data
	public InputStream in;
	// out data
	public OutputStream out;
	
	public AbstractThread(String name)
	{
		super(name);
	}
	
	public byte[] readData() throws IOException
	{
		var lengthLo = in.read();
		var lengthHi = in.read();
		var length = (lengthHi * 256) + lengthLo;
		
		if (length < 2)
		{
			LOG.finer("LoginServer: Client terminated the connection or sent illegal packet size.");
			return null;
		}
		
		byte[] incoming = new byte[length];
		incoming[0] = (byte) lengthLo;
		incoming[1] = (byte) lengthHi;
		
		int receivedBytes = 0;
		int newBytes = 0;
		while ((newBytes != -1) && (receivedBytes < (length - 2)))
		{
			newBytes = in.read(incoming, 2, length - 2);
			receivedBytes = receivedBytes + newBytes;
		}
		
		if (receivedBytes != (length - 2))
		{
			LOG.warning("Incomplete Packet is sent to the server, closing connection.");
			return null;
		}
		
		byte[] decrypt = new byte[length - 2];
		System.arraycopy(incoming, 2, decrypt, 0, decrypt.length);
		
		// decrypt if we have a key
		decrypt = blowfish.decrypt(decrypt);
		if (!Checksum.verify(decrypt))
		{
			LOG.warning("Incorrect packet checksum, closing connection.");
			return null;
		}
		
		return decrypt;
	}
	
	/**
	 * Used in LoginServerThread
	 * @param sl
	 */
	public void sendPacket(AGamePacket sl)
	{
		try
		{
			sendPacket(sl.getContent());
		}
		catch (Exception e)
		{
			LOG.severe("IOException while sending packet " + sl.getClass().getSimpleName() + ".");
			e.printStackTrace();
		}
	}
	
	/**
	 * Used in GameServerThread
	 * @param sl
	 */
	public void sendPacket(AServerPacket sl)
	{
		try
		{
			sendPacket(sl.getContent());
		}
		catch (Exception e)
		{
			LOG.severe("IOException while sending packet " + sl.getClass().getSimpleName() + ".");
			e.printStackTrace();
		}
	}
	
	private void sendPacket(byte[] data) throws IOException
	{
		Checksum.append(data);
		data = blowfish.crypt(data);
		
		var len = data.length + 2;
		synchronized (out)
		{
			out.write(len & 0xff);
			out.write((len >> 8) & 0xff);
			out.write(data);
			out.flush();
		}
	}
}
