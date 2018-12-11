package l2j.gameserver.model.holder;

import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;

/**
 * Su unica funcion es la recoleccion de datos dentro de la clase InventoryUpdate
 * @author fissban
 */
public class ItemInfoHolder
{
	/** Identifier of the L2ItemInstance */
	private int objectId;
	/** The L2Item template of the L2ItemInstance */
	private Item item;
	/** The level of enchant on the L2ItemInstance */
	private int enchant;
	/** The quantity of L2ItemInstance */
	private int count;
	/** The price of the L2ItemInstance */
	private int price;
	/** The custom L2ItemInstance types (used loto, race tickets) */
	private int type1;
	private int type2;
	/** If True the L2ItemInstance is equipped */
	private int equipped;
	/** The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE) */
	private ChangeType change;
	
	/**
	 * Get all information from L2ItemInstance to generate ItemInfo.
	 * @param item
	 */
	public ItemInfoHolder(ItemInstance item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the L2ItemInstance
		objectId = item.getObjectId();
		// Get the L2Item of the L2ItemInstance
		this.item = item.getItem();
		// Get the enchant level of the L2ItemInstance
		enchant = item.getEnchantLevel();
		// Get the quantity of the L2ItemInstance
		count = item.getCount();
		// Get custom item types (used loto, race tickets)
		type1 = item.getCustomType1();
		type2 = item.getCustomType2();
		// Verify if the L2ItemInstance is equipped
		equipped = item.isEquipped() ? 1 : 0;
		// Get the action to do clientside
		change = item.getLastChange();
	}
	
	public ItemInfoHolder(ItemInstance item, ChangeType change)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the L2ItemInstance
		objectId = item.getObjectId();
		// Get the L2Item of the L2ItemInstance
		this.item = item.getItem();
		// Get the enchant level of the L2ItemInstance
		enchant = item.getEnchantLevel();
		// Get the quantity of the L2ItemInstance
		count = item.getCount();
		// Get custom item types (used loto, race tickets)
		type1 = item.getCustomType1();
		type2 = item.getCustomType2();
		// Verify if the L2ItemInstance is equipped
		equipped = item.isEquipped() ? 1 : 0;
		// Get the action to do clientside
		this.change = change;
	}
	
	public int getObjectId()
	{
		return objectId;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public int getEnchant()
	{
		return enchant;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public int getCustomType1()
	{
		return type1;
	}
	
	public int getCustomType2()
	{
		return type2;
	}
	
	public int getEquipped()
	{
		return equipped;
	}
	
	public ChangeType getChange()
	{
		return change;
	}
}
