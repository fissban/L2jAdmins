package l2j.gameserver.network.external.server;

import java.util.Map;

import l2j.gameserver.network.AServerPacket;

/**
 * Format: (c) d[dS] d: list size [ d: char ID S: char Name ]
 * @author -Wooden-
 */
public class PackageToList extends AServerPacket
{
	private final Map<Integer, String> players;
	
	// Lecter : i put a char list here, but i'm unsure these really are Pc. I duno how freight work tho...
	public PackageToList(Map<Integer, String> players)
	{
		this.players = players;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xC2);
		writeD(players.size());
		for (int objId : players.keySet())
		{
			writeD(objId); // you told me char id, i guess this was object id?
			writeS(players.get(objId));
		}
	}
}
