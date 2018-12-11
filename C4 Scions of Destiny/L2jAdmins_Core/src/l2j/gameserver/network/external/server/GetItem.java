package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 17 1a 95 20 48 9b da 12 40 44 17 02 00 03 f0 fc ff 98 f1 ff ff ..... format ddddd
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class GetItem extends AServerPacket
{
	private final ItemInstance item;
	private final int chaObjId;
	
	public GetItem(ItemInstance item, L2Character cha)
	{
		this.item = item;
		chaObjId = cha.getObjectId();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x0d);
		writeD(chaObjId);
		writeD(item.getObjectId());
		
		writeD(item.getX());
		writeD(item.getY());
		writeD(item.getZ());
	}
}
