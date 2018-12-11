package l2j.gameserver.network.internal.gameserver;

import l2j.gameserver.network.AGamePacket;

/**
 * @author -Wooden-
 */
public class PlayerLogout extends AGamePacket
{
	public PlayerLogout(String player)
	{
		writeC(0x03);
		writeS(player);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
