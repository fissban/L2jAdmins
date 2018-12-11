package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.AServerPacket;

public class BuyListSeed extends AServerPacket
{
	private final int manorId;
	private List<ItemInstance> list = new ArrayList<>();
	private final int money;
	
	public BuyListSeed(MerchantTradeList list, int manorId, int currentMoney)
	{
		money = currentMoney;
		this.manorId = manorId;
		this.list = list.getItems();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xE8);
		
		writeD(money); // current money
		writeD(manorId); // manor id
		
		writeH(list.size()); // list length
		
		for (ItemInstance item : list)
		{
			writeH(0x04); // item->type1
			writeD(0x00); // objectId
			writeD(item.getId()); // item id
			writeD(item.getCount()); // item count
			writeH(0x04); // item->type2
			writeH(0x00); // unknown :)
			writeD(item.getPriceToSell()); // price
		}
	}
}
