package l2j.gameserver.network.external.server;

import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 15 ee cc 11 43 object id 39 00 00 00 item id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 01 00 00 00 show item count 7a 00 00 00 count . format dddddddd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SpawnItem extends AServerPacket
{
	private final ItemInstance item;
	
	public SpawnItem(ItemInstance item)
	{
		this.item = item;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x0b);
		writeD(item.getObjectId());
		writeD(item.getId());
		
		writeD(item.getX());
		writeD(item.getY());
		writeD(item.getZ());
		// only show item count if it is a stackable item
		writeD(item.isStackable() ? 0x01 : 0x00);
		writeD(item.getCount());
		writeD(0x00); // c2
	}
}
