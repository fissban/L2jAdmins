package l2j.gameserver.network.internal.gameserver;

import java.util.List;

import l2j.gameserver.network.AGamePacket;

/**
 * @author -Wooden-
 */
public class PlayerInGame extends AGamePacket
{
	public PlayerInGame(String player)
	{
		writeC(0x02);
		writeH(1);
		writeS(player);
	}
	
	public PlayerInGame(List<String> players)
	{
		writeC(0x02);
		writeH(players.size());
		for (String pc : players)
		{
			writeS(pc);
		}
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
