package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.items.enums.ItemType1;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 1d 1e 00 00 00 // ?? 5c 4a a0 7c // buy list id 02 00 // item count 04 00 // itemType1 0-weapon/ring/earring/necklace 1-armor/shield 4-item/questitem/adena 00 00 00 00 // objectid 32 04 00 00 // itemid 00 00 00 00 // count 05 00 // itemType2 0-weapon 1-shield/armor 2-ring/earring/necklace
 * 3-questitem 4-adena 5-item 00 00 60 09 00 00 // price 00 00 00 00 00 00 b6 00 00 00 00 00 00 00 00 00 00 00 80 00 // body slot these 4 values are only used if itemtype1 = 0 or 1 00 00 // 00 00 // 00 00 // 50 c6 0c 00 format dd h (h dddhh hhhh d) revision 377 format dd h (h dddhh dhhh d) revision
 * 377
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class BuyList extends AServerPacket
{
	private int listId;
	private List<ItemInstance> list = new ArrayList<>();
	private int money;
	private double taxRate;
	
	public BuyList(MerchantTradeList list, int currentMoney, double taxRate)
	{
		listId = list.getListId();
		this.list = list.getItems();
		money = currentMoney;
		this.taxRate = taxRate;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x11);
		writeD(money); // current money
		writeD(listId);
		
		writeH(list.size());
		
		for (ItemInstance item : list)
		{
			if ((item.getCount() > 0) || (item.getCount() == -1))
			{
				writeH(item.getItem().getType1().getMask()); // item type1
				writeD(item.getObjectId());
				writeD(item.getId());
				writeD((item.getCount() > 0) ? item.getCount() : 0);
				writeH(item.getItem().getType2().ordinal()); // item type2
				writeH(0x00); // ?
				
				if (item.getItem().getType1() != ItemType1.ITEM_QUESTITEM_ADENA)
				{
					writeD(item.getItem().getBodyPart().getMask()); // 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
					writeH(item.getEnchantLevel()); // Enchant level
					writeH(0x00); // Custom Type
					writeH(0x00); // Augment
				}
				else
				{
					writeD(0x00); // 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
					writeH(0x00); // Enchant level
					writeH(0x00); // Custom Type
					writeH(0x00); // Augment
				}
				
				if ((item.getId() >= 3960) && (item.getId() <= 4026))
				{
					writeD((int) (item.getPriceToSell() * Config.RATE_SIEGE_GUARDS_PRICE * (1 + taxRate)));
				}
				else
				{
					writeD((int) (item.getPriceToSell() * (1 + taxRate)));
				}
			}
		}
	}
}
