package l2j.gameserver.model.multisell;

/**
 * @author fissban
 */
public class MultisellIngredient
{
	private int itemId;
	private long itemCount;
	private int enchantmentLevel;
	
	public MultisellIngredient(int itemId, int itemCount)
	{
		this(itemId, itemCount, 0);
	}
	
	public MultisellIngredient(int itemId, long itemCount, int enchantmentLevel)
	{
		setItemId(itemId);
		setItemCount(itemCount);
		setEnchantmentLevel(enchantmentLevel);
	}
	
	public MultisellIngredient(MultisellIngredient e)
	{
		itemId = e.getItemId();
		itemCount = e.getItemCount();
		enchantmentLevel = e.getEnchantmentLevel();
	}
	
	/**
	 * @param itemId The itemId to set.
	 */
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	/**
	 * @return Returns the itemId.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * @param itemCount The itemCount to set.
	 */
	public void setItemCount(long itemCount)
	{
		this.itemCount = itemCount;
	}
	
	/**
	 * @return Returns the itemCount.
	 */
	public long getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * @param enchantmentLevel
	 */
	public void setEnchantmentLevel(int enchantmentLevel)
	{
		this.enchantmentLevel = enchantmentLevel;
	}
	
	/**
	 * @return Returns the enchantLvl.
	 */
	public int getEnchantmentLevel()
	{
		return enchantmentLevel;
	}
}
