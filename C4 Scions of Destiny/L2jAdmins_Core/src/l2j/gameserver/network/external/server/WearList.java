package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.Config;
import l2j.gameserver.model.items.enums.ItemType1;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.AServerPacket;

public class WearList extends AServerPacket
{
	private final int listId;
	private final List<ItemInstance> itemList;
	private final int money;
	private int expertise;
	
	public WearList(MerchantTradeList list, int currentMoney, int expertiseIndex)
	{
		listId = list.getListId();
		itemList = list.getItems();
		money = currentMoney;
		expertise = expertiseIndex;
	}
	
	public WearList(List<ItemInstance> items, int listId, int currentMoney)
	{
		this.listId = listId;
		itemList = items;
		money = currentMoney;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xef);
		writeC(0xc0); // ?
		writeC(0x13); // ?
		writeC(0x00); // ?
		writeC(0x00); // ?
		writeD(money); // current money
		writeD(listId);
		
		int newlength = 0;
		for (ItemInstance item : itemList)
		{
			if ((item.getItem().getCrystalType().ordinal() <= expertise) && item.isEquipable())
			{
				newlength++;
			}
		}
		writeH(newlength);
		
		for (ItemInstance item : itemList)
		{
			if ((item.getItem().getCrystalType().ordinal() <= expertise) && item.isEquipable())
			{
				writeD(item.getId());
				writeH(item.getItem().getType2().ordinal()); // item type2
				
				if (item.getItem().getType1() != ItemType1.ITEM_QUESTITEM_ADENA)
				{
					writeH(item.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}
				else
				{
					writeH(0x00); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
				}
				
				writeD(Config.WEAR_PRICE);
			}
		}
	}
}
