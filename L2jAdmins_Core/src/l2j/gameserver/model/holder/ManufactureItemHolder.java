package l2j.gameserver.model.holder;

import l2j.gameserver.data.RecipeData;

/**
 * This class ...
 * @version $Revision: 1.1.2.2.2.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class ManufactureItemHolder
{
	private final int recipeId;
	private final int cost;
	
	public ManufactureItemHolder(int recipeId, int cost)
	{
		this.recipeId = recipeId;
		this.cost = cost;
	}
	
	public int getRecipeId()
	{
		return recipeId;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public boolean isDwarven()
	{
		return RecipeData.getRecipeList(recipeId).isDwarvenRecipe();
	}
}
