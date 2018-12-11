package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author -Wooden-
 */
public class PackageSendableList extends AServerPacket
{
	private final List<ItemInstance> items;
	private final int playerObjId;
	
	public PackageSendableList(List<ItemInstance> items, int playerOID)
	{
		this.items = items;
		playerObjId = playerOID;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xC3);
		
		writeD(playerObjId);
		writeD(getClient().getActiveChar().getInventory().getAdena());
		writeD(items.size());
		for (ItemInstance item : items) // format inside the for taken from SellList part use should be about the same
		{
			writeH(item.getItem().getType1().getMask());
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal());
			writeH(item.getCustomType1());
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getEnchantLevel());
			writeH(0x00);
			writeH(item.getCustomType2());
			
			writeD(item.getObjectId()); // some item identifier later used by client to answer (see RequestPackageSend) not item id nor object id maybe some freight system id??
		}
	}
}
