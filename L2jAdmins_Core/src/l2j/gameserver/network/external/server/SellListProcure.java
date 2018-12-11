package l2j.gameserver.network.external.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

public class SellListProcure extends AServerPacket
{
	private final int money;
	private final Map<ItemInstance, Integer> sellList = new HashMap<>();
	
	public SellListProcure(L2PcInstance player, int castleId)
	{
		money = player.getInventory().getAdena();
		List<CropProcureHolder> procureList = CastleData.getInstance().getCastleById(castleId).getCropProcure(0);
		for (CropProcureHolder c : procureList)
		{
			ItemInstance item = player.getInventory().getItemById(c.getId());
			if ((item != null) && (c.getAmount() > 0))
			{
				sellList.put(item, c.getAmount());
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xE9);
		writeD(money); // money
		writeD(0x00); // lease ?
		writeH(sellList.size()); // list size
		
		for (ItemInstance item : sellList.keySet())
		{
			writeH(item.getItem().getType1().getMask());
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(sellList.get(item)); // count
			writeH(item.getItem().getType2().ordinal());
			writeH(0); // unknown
			writeD(0); // price, u shouldnt get any adena for crops, only raw materials
		}
	}
}
