package l2j.gameserver.model.recipes;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a Recipe used by Dwarf to craft Item.<BR>
 * All RecipeHolder are made of RecipeHolder (1 line of the recipe : Item-Quantity needed).
 */
public class RecipeList
{
	/** The table containing all RecipeHolder (1 line of the recipe : Item-Quantity needed) of the RecipeHolder */
	private final List<RecipeInstance> recipes;
	/** The Identifier of the Instance */
	private final int id;
	/** The crafting level needed to use this RecipeHolder */
	private final int level;
	/** The Identifier of the RecipeHolder */
	private final int recipeId;
	/** The name of the RecipeHolder */
	private final String recipeName;
	/** The crafting success rate when using the RecipeHolder */
	private final int successRate;
	/** The crafting MP cost of this RecipeHolder */
	private final int mpCost;
	/** The Identifier of the Item crafted with this RecipeHolder */
	private final int itemId;
	/** The quantity of Item crafted when using this RecipeHolder */
	private final int count;
	/** If this a common or a dwarven recipe */
	private final boolean isDwarvenRecipe;
	
	/**
	 * Constructor of RecipeHolder (create a new Recipe).
	 * @param id
	 * @param level
	 * @param recipeId
	 * @param recipeName
	 * @param successRate
	 * @param mpCost
	 * @param itemId
	 * @param count
	 * @param isDwarvenRecipe
	 */
	public RecipeList(int id, int level, int recipeId, String recipeName, int successRate, int mpCost, int itemId, int count, boolean isDwarvenRecipe)
	{
		this.id = id;
		recipes = new ArrayList<>();
		this.level = level;
		this.recipeId = recipeId;
		this.recipeName = recipeName;
		this.successRate = successRate;
		this.mpCost = mpCost;
		this.itemId = itemId;
		this.count = count;
		this.isDwarvenRecipe = isDwarvenRecipe;
	}
	
	/**
	 * Add a L2RecipeInstance to the RecipeHolder (add a line Item-Quantity needed to the Recipe).
	 * @param recipe
	 */
	public void addRecipe(RecipeInstance recipe)
	{
		recipes.add(recipe);
	}
	
	/**
	 * Return the Identifier of the Instance.
	 * @return
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Return the crafting level needed to use this RecipeHolder.
	 * @return
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Return the Identifier of the RecipeHolder.
	 * @return
	 */
	public int getRecipeId()
	{
		return recipeId;
	}
	
	/**
	 * Return the name of the RecipeHolder.
	 * @return
	 */
	public String getRecipeName()
	{
		return recipeName;
	}
	
	/**
	 * Return the crafting success rate when using the RecipeHolder.
	 * @return
	 */
	public int getSuccessRate()
	{
		return successRate;
	}
	
	/**
	 * Return the crafting MP cost of this RecipeHolder.
	 * @return
	 */
	public int getMpCost()
	{
		return mpCost;
	}
	
	/**
	 * Return rue if the Item crafted with this RecipeHolder is consumable (shot, arrow,...).
	 * @return
	 */
	public boolean isConsumable()
	{
		return (((itemId >= 1463) && (itemId <= 1467)) // Soulshots
			|| ((itemId >= 2509) && (itemId <= 2514)) // Spiritshots
			|| ((itemId >= 3947) && (itemId <= 3952)) // Blessed Spiritshots
			|| ((itemId >= 1341) && (itemId <= 1345)) // Arrows
		);
	}
	
	/**
	 * Return the Identifier of the Item crafted with this RecipeHolder.<BR>
	 * @return
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Return the quantity of Item crafted when using this RecipeHolder.<BR>
	 * @return
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Return <B>true</B> if this a Dwarven recipe or <B>false</B> if its a Common recipe
	 * @return
	 */
	public boolean isDwarvenRecipe()
	{
		return isDwarvenRecipe;
	}
	
	/**
	 * Return the table containing all L2RecipeInstance (1 line of the recipe : Item-Quantity needed) of the RecipeHolder.<BR>
	 * @return
	 */
	public List<RecipeInstance> getRecipes()
	{
		return recipes;
	}
}
