package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.recipes.RecipeInstance;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.util.UtilPrint;
import l2j.util.XmlParser;

/**
 * @author fissban
 */
public class RecipeData extends XmlParser
{
	private static final Map<Integer, RecipeList> recipes = new HashMap<>();
	
	@Override
	public void load()
	{
		loadFile("data/xml/recipes.xml");
		UtilPrint.result("RecipeData", "Loaded recipes", recipes.size());
	}
	
	@Override
	protected void parseFile()
	{
		List<RecipeInstance> recipePartList;
		
		for (Node n : getNodes("recipe"))
		{
			recipePartList = new ArrayList<>();
			
			int id;
			int recipeId;
			String name;
			int craftLevel;
			boolean isDwarven;
			int successRate;
			int mpCost;
			int productId = 0;
			int productCount = 0;
			
			NamedNodeMap attrs = n.getAttributes();
			
			id = parseInt(attrs, "id");
			recipeId = parseInt(attrs, "recipeId");
			name = parseString(attrs, "name");
			craftLevel = parseInt(attrs, "craftLevel");
			
			if (parseString(attrs, "type").equals("dwarven"))
			{
				isDwarven = true;
			}
			else
			{
				isDwarven = false;
			}
			successRate = parseInt(attrs, "successRate");
			mpCost = parseInt(attrs, "mpCost");
			
			for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling())
			{
				attrs = c.getAttributes();
				
				if (c.getNodeName().equals("ingredient"))
				{
					int ingId = parseInt(attrs, "itemId");
					int ingCount = parseInt(attrs, "count");
					recipePartList.add(new RecipeInstance(ingId, ingCount));
				}
				else if (c.getNodeName().equals("production"))
				{
					productId = parseInt(attrs, "itemId");
					productCount = parseInt(attrs, "count");
				}
			}
			
			RecipeList recipeList = null;
			try
			{
				recipeList = new RecipeList(id, craftLevel, recipeId, name, successRate, mpCost, productId, productCount, isDwarven);
				for (RecipeInstance recipePart : recipePartList)
				{
					recipeList.addRecipe(recipePart);
				}
				
			}
			catch (Exception e)
			{
				LOG.warning(getClass().getSimpleName() + ": Error saving recipe Item, skipping!");
				continue;
			}
			recipes.put(id, recipeList);
		}
	}
	
	public static RecipeList getRecipeList(int listId)
	{
		return recipes.get(listId);
	}
	
	/**
	 * Gets the all item ids.
	 * @return the all item ids
	 */
	public static int[] getAllRecipeds()
	{
		int[] idList = new int[recipes.size()];
		int i = 0;
		for (RecipeList rec : recipes.values())
		{
			idList[i++] = rec.getRecipeId();
		}
		return idList;
	}
	
	public static RecipeList getRecipeByItemId(int itemId)
	{
		for (RecipeList find : recipes.values())
		{
			if (find.getRecipeId() == itemId)
			{
				return find;
			}
		}
		return null;
	}
	
	public static RecipeList getValidRecipeList(L2PcInstance player, int id)
	{
		RecipeList recipeList = getRecipeList(id);
		
		if ((recipeList == null) || (recipeList.getRecipes().isEmpty()))
		{
			player.sendMessage("No recipe for: " + id);
			player.getPrivateStore().isInCraftMode(false);
			return (RecipeList) Collections.emptyList();
		}
		return recipeList;
	}
	
	public static RecipeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeData INSTANCE = new RecipeData();
	}
}
