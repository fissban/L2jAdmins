package l2j.gameserver.model.holder;

/**
 * @author fissban
 */
public class ItemRequestHolder
{
	int objectId;
	int itemId;
	int count;
	int price;
	
	public ItemRequestHolder(int objectId, int count, int price)
	{
		this.objectId = objectId;
		this.count = count;
		this.price = price;
	}
	
	public ItemRequestHolder(int objectId, int itemId, int count, int price)
	{
		this.objectId = objectId;
		this.itemId = itemId;
		this.count = count;
		this.price = price;
	}
	
	public int getObjectId()
	{
		return objectId;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getPrice()
	{
		return price;
	}
}
