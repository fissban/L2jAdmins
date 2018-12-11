package l2j.gameserver.network.external.server;

import java.util.Collection;

import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.network.AServerPacket;

/**
 * format d d(dd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeBookItemList extends AServerPacket
{
	private Collection<RecipeList> recipes;
	private final boolean isDwarvenCraft;
	private final int maxMp;
	
	public RecipeBookItemList(boolean isDwarvenCraft, int maxMp)
	{
		this.isDwarvenCraft = isDwarvenCraft;
		this.maxMp = maxMp;
	}
	
	public void addRecipes(Collection<RecipeList> recipeBook)
	{
		recipes = recipeBook;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xD6);
		
		writeD(isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
		writeD(maxMp);
		
		if (recipes == null)
		{
			writeD(0);
		}
		else
		{
			writeD(recipes.size());// number of items in recipe book
			
			int aux = 1;
			for (RecipeList recipe : recipes)
			{
				writeD(recipe.getId());
				writeD(aux);
				aux++;
			}
		}
	}
}
