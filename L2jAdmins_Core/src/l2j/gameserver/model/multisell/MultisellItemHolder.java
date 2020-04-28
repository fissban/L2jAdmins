package l2j.gameserver.model.multisell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fissban
 */
public class MultisellItemHolder
{
	private int entryId;
	
	private final List<ProductHolder> products = new ArrayList<>();
	private final List<ProductHolder> ingredients = new ArrayList<>();
	
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
	public void addProduct(ProductHolder product)
	{
		products.add(product);
	}
	
	/**
	 * @return the products.
	 */
	public List<ProductHolder> getProducts()
	{
		return products;
	}
	
	/**
	 * @param ingredient The ingredients to set.
	 */
	public void addIngredient(ProductHolder ingredient)
	{
		ingredients.add(ingredient);
	}
	
	/**
	 * @return the ingredients.
	 */
	public List<ProductHolder> getIngredients()
	{
		return ingredients;
	}
}
