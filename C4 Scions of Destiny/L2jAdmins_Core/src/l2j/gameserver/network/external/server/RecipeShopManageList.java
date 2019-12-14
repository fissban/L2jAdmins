package l2j.gameserver.network.external.server;

import java.util.Collection;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PrivateStoreList;
import l2j.gameserver.model.holder.ManufactureItemHolder;
import l2j.gameserver.model.recipes.RecipeList;
import l2j.gameserver.network.AServerPacket;

/**
 * dd d(dd) d(ddd)
 * @version $Revision: 1.1.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeShopManageList extends AServerPacket
{
	private L2PcInstance seller;
	private boolean isDwarven;
	private Collection<RecipeList> recipes;
	
	public RecipeShopManageList(L2PcInstance seller, boolean isDwarven)
	{
		this.seller = seller;
		this.isDwarven = isDwarven;
		
		if (isDwarven && seller.hasDwarvenCraft())
		{
			recipes = seller.getDwarvenRecipeBookList();
		}
		else
		{
			recipes = seller.getCommonRecipeBookList();
		}
		
		// clean previous recipes
		if (seller.getPrivateStore().getCreateList() != null)
		{
			PrivateStoreList list = seller.getPrivateStore().getCreateList();
			
			for (ManufactureItemHolder item : list.getList())
			{
				if (item.isDwarven() != isDwarven)
				{
					list.getList().remove(item);
				}
			}
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xd8);
		writeD(seller.getObjectId());
		writeD(seller.getInventory().getAdena());
		writeD(isDwarven ? 0x00 : 0x01);
		
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
		
		if (seller.getPrivateStore().getCreateList() == null)
		{
			writeD(0);
		}
		else
		{
			PrivateStoreList list = seller.getPrivateStore().getCreateList();
			writeD(list.size());
			
			for (ManufactureItemHolder item : list.getList())
			{
				writeD(item.getRecipeId());
				writeD(0x00);
				writeD(item.getCost());
			}
		}
	}
}
