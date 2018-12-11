package l2j.gameserver.model.holder;

import l2j.gameserver.data.ItemData;

/**
 * Holder for itemId/count/objectId.
 * @author UnAfraid
 */
public class ItemHolder
{
	private final int id;
	private final int objectId;
	private int count;
	
	public ItemHolder(int id, int count)
	{
		this.id = id;
		objectId = -1;
		this.count = count;
	}
	
	public ItemHolder(int id, int objectId, int count)
	{
		this.id = id;
		this.objectId = objectId;
		this.count = count;
	}
	
	/**
	 * @return the item/object identifier.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * @return the object Id
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * @return the item count.
	 */
	public int getCount()
	{
		return count;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public String getItemName()
	{
		return ItemData.getInstance().getTemplate(id).getName();
	}
}
