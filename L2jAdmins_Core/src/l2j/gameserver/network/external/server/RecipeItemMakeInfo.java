package l2j.gameserver.network.external.server;

import l2j.gameserver.data.RecipeData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.network.AServerPacket;

/**
 * format dddd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeItemMakeInfo extends AServerPacket
{
	private final int id;
	private final L2PcInstance player;
	private final boolean success;
	
	public RecipeItemMakeInfo(int id, L2PcInstance player, boolean success)
	{
		this.id = id;
		this.player = player;
		this.success = success;
	}
	
	public RecipeItemMakeInfo(int id, L2PcInstance player)
	{
		this.id = id;
		this.player = player;
		success = true;
	}
	
	@Override
	public void writeImpl()
	{
		RecipeList recipe = RecipeData.getRecipeList(id);
		
		if (recipe != null)
		{
			writeC(0xD7);
			
			writeD(id);
			writeD(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
			writeD((int) player.getCurrentMp());
			writeD(player.getStat().getMaxMp());
			writeD(success ? 1 : 0); // item creation success/failed
		}
	}
}
