package l2j.gameserver.network.internal.gameserver;

import l2j.gameserver.network.AGamePacket;

/**
 * @author -Wooden-
 */
public class ChangeAccessLevel extends AGamePacket
{
	public ChangeAccessLevel(String player, int access)
	{
		writeC(0x04);
		writeD(access);
		writeS(player);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
