package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.instance.L2MerchantInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.3.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class SellList extends AServerPacket
{
	private final L2PcInstance player;
	private final L2MerchantInstance lease;
	private final List<ItemInstance> sellList = new ArrayList<>();
	
	public SellList(L2PcInstance player)
	{
		this.player = player;
		lease = null;
		lease();
	}
	
	public SellList(L2PcInstance player, L2MerchantInstance lease)
	{
		this.player = player;
		this.lease = lease;
		lease();
	}
	
	private void lease()
	{
		if (lease == null)
		{
			for (ItemInstance item : player.getInventory().getItems())
			{
				if (!item.isEquipped() && item.getItem().isSellable() && ((player.getPet() == null) || (item.getObjectId() != player.getPet().getControlItemId())))
				{
					sellList.add(item);
				}
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x10);
		writeD(player.getInventory().getAdena());
		writeD(lease == null ? 0x00 : 1000000 + lease.getTemplate().getId());
		
		writeH(sellList.size());
		
		for (ItemInstance item : sellList)
		{
			writeH(item.getItem().getType1().getMask());
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal());
			writeH(0x00);
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getEnchantLevel());
			writeH(0x00);
			writeH(item.getCustomType2());
			
			if (lease == null)
			{
				writeD(item.getItem().getReferencePrice() / 2);
			}
		}
	}
}
