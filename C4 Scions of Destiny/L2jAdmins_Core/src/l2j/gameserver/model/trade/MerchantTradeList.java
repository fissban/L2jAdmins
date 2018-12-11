package l2j.gameserver.model.trade;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.5 $ $Date: 2005/03/27 15:29:33 $
 */
public class MerchantTradeList
{
	private final List<ItemInstance> items;
	private final int listId;
	private boolean confirmed;
	private String buyStoreName;
	private String sellStoreName;
	
	private String npcId;
	
	public MerchantTradeList(int listId)
	{
		items = new ArrayList<>();
		this.listId = listId;
		confirmed = false;
	}
	
	public void setNpcId(String id)
	{
		npcId = id;
	}
	
	public String getNpcId()
	{
		return npcId;
	}
	
	public void addItem(ItemInstance item)
	{
		items.add(item);
	}
	
	public void replaceItem(int itemID, int price)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemID)
			{
				item.setPriceToSell(price);
			}
		}
	}
	
	public boolean decreaseCount(int itemId, int count)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemId)
			{
				if (item.getCount() >= count)
				{
					item.setCount(item.getCount() - count);
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeItem(int itemId)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemId)
			{
				items.remove(item);
			}
		}
	}
	
	/**
	 * @return Returns the listId.
	 */
	public int getListId()
	{
		return listId;
	}
	
	public void setSellStoreName(String name)
	{
		sellStoreName = name;
	}
	
	public String getSellStoreName()
	{
		return sellStoreName;
	}
	
	public void setBuyStoreName(String name)
	{
		buyStoreName = name;
	}
	
	public String getBuyStoreName()
	{
		return buyStoreName;
	}
	
	/**
	 * @return Returns the items.
	 */
	public List<ItemInstance> getItems()
	{
		return items;
	}
	
	public List<ItemInstance> getItems(int start, int end)
	{
		return items.subList(start, end);
	}
	
	public int getPriceForItemId(int itemId)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemId)
			{
				return item.getPriceToSell();
			}
		}
		return -1;
	}
	
	public boolean countDecrease(int itemId)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemId)
			{
				return item.getCountDecrease();
			}
		}
		return false;
	}
	
	public boolean containsItemId(int itemId)
	{
		for (ItemInstance item : items)
		{
			if (item.getId() == itemId)
			{
				return true;
			}
		}
		return false;
	}
	
	public ItemInstance getItem(int ObjectId)
	{
		for (ItemInstance item : items)
		{
			if (item.getObjectId() == ObjectId)
			{
				return item;
			}
		}
		return null;
	}
	
	public synchronized void setConfirmedTrade(boolean x)
	{
		confirmed = x;
	}
	
	public synchronized boolean hasConfirmed()
	{
		return confirmed;
	}
	
	public void removeItem(int objId, int count)
	{
		for (ItemInstance item : items)
		{
			if (item.getObjectId() == objId)
			{
				if (count == item.getCount())
				{
					items.remove(item);
				}
				
				break;
			}
		}
	}
}
