package l2j.gameserver.model.multisell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fissban
 */
public class MultisellEntry
{
	private int entryId;
	
	private final List<MultisellIngredient> products = new ArrayList<>();
	private final List<MultisellIngredient> ingredients = new ArrayList<>();
	
	/**
	 * @param entryId The entryId to set.
	 */
	public void setEntryId(int entryId)
	{
		this.entryId = entryId;
	}
	
	/**
	 * @return the entryId.
	 */
	public int getEntryId()
	{
		return entryId;
	}
	
	/**
	 * @param product The product to add.
	 */
	public void addProduct(MultisellIngredient product)
	{
		products.add(product);
	}
	
	/**
	 * @return the products.
	 */
	public List<MultisellIngredient> getProducts()
	{
		return products;
	}
	
	/**
	 * @param ingredient The ingredients to set.
	 */
	public void addIngredient(MultisellIngredient ingredient)
	{
		ingredients.add(ingredient);
	}
	
	/**
	 * @return the ingredients.
	 */
	public List<MultisellIngredient> getIngredients()
	{
		return ingredients;
	}
}
