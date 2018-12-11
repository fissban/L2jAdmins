package l2j.gameserver.model.holder;

import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * This class ...
 * @version $Revision: 1.2.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public final class TradeItemHolder
{
	private int objectId;
	private Item item;
	private int enchant;
	private int type2;
	private int count;
	private int price;
	
	public TradeItemHolder(ItemInstance item, int count, int price)
	{
		objectId = item.getObjectId();
		this.item = item.getItem();
		enchant = item.getEnchantLevel();
		type2 = item.getCustomType2();
		this.count = count;
		this.price = price;
	}
	
	public TradeItemHolder(Item item, int count, int price)
	{
		objectId = 0;
		this.item = item;
		enchant = 0;
		type2 = 0;
		this.count = count;
		this.price = price;
	}
	
	public TradeItemHolder(TradeItemHolder item, int count, int price)
	{
		objectId = item.getObjectId();
		this.item = item.getItem();
		enchant = item.getEnchant();
		type2 = 0;
		this.count = count;
		this.price = price;
	}
	
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	public int getObjectId()
	{
		return objectId;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public void setEnchant(int enchant)
	{
		this.enchant = enchant;
	}
	
	public int getEnchant()
	{
		return enchant;
	}
	
	public int getCustomType2()
	{
		return type2;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public void setPrice(int price)
	{
		this.price = price;
	}
	
	public int getPrice()
	{
		return price;
	}
}
