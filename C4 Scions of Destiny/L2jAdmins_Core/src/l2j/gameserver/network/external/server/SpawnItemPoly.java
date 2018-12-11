package l2j.gameserver.network.external.server;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 15 ee cc 11 43 object id 39 00 00 00 item id 8f 14 00 00 x b7 f1 00 00 y 60 f2 ff ff z 01 00 00 00 show item count 7a 00 00 00 count . format dddddddd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class SpawnItemPoly extends AServerPacket
{
	// TODO this packet never user!
	private final int objectId;
	private final int itemId;
	private int x, y, z;
	private int stackable, count;
	
	public SpawnItemPoly(L2Object object)
	{
		objectId = object.getObjectId();
		itemId = object.getPolyId();
		
		if (object instanceof ItemInstance)
		{
			ItemInstance item = (ItemInstance) object;
			
			x = item.getX();
			y = item.getY();
			z = item.getZ();
			stackable = item.isStackable() ? 0x01 : 0x00;
			count = item.getCount();
		}
		else
		{
			x = object.getX();
			y = object.getY();
			z = object.getZ();
			stackable = 0x00;
			count = 1;
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x0b);
		writeD(objectId);
		writeD(itemId);
		
		writeD(x);
		writeD(y);
		writeD(z);
		// only show item count if it is a stackable item
		writeD(stackable);
		writeD(count);
		writeD(0x00); // c2
	}
}
