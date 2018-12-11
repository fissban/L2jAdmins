package l2j.gameserver.model.recipes;

/**
 * This class describes a RecipeList component (1 line of the recipe : Item-Quantity needed).
 */
public class RecipeInstance
{
	/** The Identifier of the item needed in the RecipeHolder */
	private final int itemId;
	/** The item quantity needed in the RecipeHolder */
	private final int quantity;
	
	/**
	 * Constructor of RecipeHolder (create a new line in a RecipeList).
	 * @param itemId
	 * @param quantity
	 */
	public RecipeInstance(int itemId, int quantity)
	{
		this.itemId = itemId;
		this.quantity = quantity;
	}
	
	/**
	 * Return the Identifier of the RecipeHolder Item needed.
	 * @return
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Return the Item quantity needed of the RecipeHolder.
	 * @return
	 */
	public int getQuantity()
	{
		return quantity;
	}
}
