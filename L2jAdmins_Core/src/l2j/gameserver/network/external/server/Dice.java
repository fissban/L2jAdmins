package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:40 $
 */
public class Dice extends AServerPacket
{
	private final int playerId;
	private final int itemId;
	private final int number;
	private final int x;
	private final int y;
	private final int z;
	
	/**
	 * 0xd4 Dice dddddd
	 * @param player
	 * @param itemId
	 * @param number
	 * @param x
	 * @param y
	 * @param z
	 */
	public Dice(L2PcInstance player, int itemId, int number, int x, int y, int z)
	{
		playerId = player.getObjectId();
		this.itemId = itemId;
		this.number = number;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xD4);
		writeD(playerId); // object id of player
		writeD(itemId); // item id of dice (spade) 4625,4626,4627,4628
		writeD(number); // number rolled
		writeD(x); // x
		writeD(y); // y
		writeD(z); // z
	}
}
